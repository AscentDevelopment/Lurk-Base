package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Teleports the player back to a last-known safe location.
 * Also supports a "phase" path that snaps out of blocks and optionally
 * sets a short "deny edits" window that your block listeners can honor.
 *
 * Params (all optional):
 *  - mode: "auto" | "generic" | "phase" (default auto)
 *  - phase.deny_edits_ms: long millis (default 0 = off)
 *  - phase.snap_max_dist: double meters safety cap (default 3.0)
 */
@MitigationInfo(key = "setback", desc = "Teleport player to last known safe location (phase-aware)")
public final class SetbackMitigation implements Mitigation {
    public String key() { return "setback"; }

    public void onApply(User user, MitigationContext ctx) {
        if (user == null) return;
        Player p = user.getPlayer();
        if (p == null) return;

        final String mode = safe(ctx.param("mode", "auto"));
        final boolean phasePath = "phase".equals(mode) || ("auto".equals(mode) && detectInsideSolid(p));

        // Find a good target location
        Location target = resolveLastSafe(user, p);
        if (target == null) target = p.getLocation();

        // Safety cap for "phase" snaps
        if (phasePath) {
            double maxDist = parseDouble(ctx.param("phase.snap_max_dist", "3.0"), 3.0);
            if (p.getLocation().distanceSquared(target) > (maxDist * maxDist)) {
                // too far—fallback to a tiny nudge upward to avoid soft-locks
                Location here = p.getLocation().clone();
                target = new Location(here.getWorld(), here.getX(), here.getY() + 0.5, here.getZ(), here.getYaw(), here.getPitch());
            }
        }

        safeTeleport(p, target);

        // Optional: deny block edits briefly after a phase snap (your listeners can read this)
        if (phasePath) {
            long ms = parseLong(ctx.param("phase.deny_edits_ms", "0"), 0L);
            if (ms > 0) {
                ctx.param("deny_edits_until", String.valueOf(System.currentTimeMillis() + ms));
            }
        }
    }

    public void onRemove(User user, MitigationContext ctx) { /* one-shot; nothing to undo */ }

    // ---- helpers ----

    private static boolean detectInsideSolid(Player p) {
        try {
            Object blk = p.getLocation().getBlock();
            // Prefer isEmpty() if present
            try {
                Boolean empty = (Boolean) blk.getClass().getMethod("isEmpty").invoke(blk);
                return empty != null && !empty.booleanValue();
            } catch (NoSuchMethodException ignored) {}
            // Older API: typeId != 0 means solid-ish
            Integer typeId = (Integer) blk.getClass().getMethod("getTypeId").invoke(blk);
            return typeId != null && typeId.intValue() != 0;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Try to use your MovementProcessor if available, otherwise fallback to current location. */
    private static Location resolveLastSafe(User user, Player p) {
        try {
            Object pm = user.getProcessorManager();
            if (pm != null) {
                // pm.get(Class) via reflection
                Object mv = pm.getClass().getMethod("get", Class.class)
                        .invoke(pm, Class.forName("dev.ascent.anticheat.process.processors.MovementProcessor"));
                if (mv != null) {
                    // common helpers you might have—try a few:
                    try {
                        Object loc = mv.getClass().getMethod("getLastSafeBukkitLocation").invoke(mv);
                        if (loc instanceof Location) return (Location) loc;
                    } catch (NoSuchMethodException ignored) {}
                    try {
                        Object loc = mv.getClass().getMethod("toBukkitLastGround").invoke(mv);
                        if (loc instanceof Location) return (Location) loc;
                    } catch (NoSuchMethodException ignored) {}
                    try {
                        Object loc = mv.getClass().getMethod("getLastGroundBukkit").invoke(mv);
                        if (loc instanceof Location) return (Location) loc;
                    } catch (NoSuchMethodException ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return p.getLocation();
    }

    private static void safeTeleport(Player p, Location loc) {
        try {
            Player.class.getMethod("teleport", Location.class).invoke(p, loc);
            return;
        } catch (Throwable ignored) {}
        try { Player.class.getMethod("teleportTo", Location.class).invoke(p, loc); }
        catch (Throwable ignored2) {}
    }

    private static String safe(String s) { return s == null ? "" : s; }
    private static long parseLong(String s, long def) { try { return Long.parseLong(s); } catch (Throwable t) { return def; } }
    private static double parseDouble(String s, double def) { try { return Double.parseDouble(s); } catch (Throwable t) { return def; } }
}
