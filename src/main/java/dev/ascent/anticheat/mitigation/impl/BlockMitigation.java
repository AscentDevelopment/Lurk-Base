package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Combined block edit deny + light "ghost block heal".
 *
 * Key is kept "block" for config compatibility.
 *
 * Params:
 *  - deny_place: "true"/"false" (default true)
 *  - deny_break: "true"/"false" (default true)
 *  - heal_radius: integer (default 0 = off; 1..2 recommended)
 *  - heal_ms: long (default 250) – only re-send blocks changed within this recent window (if your listeners tag ctx)
 *
 * Your block listeners should read ctx.param("deny_edits_until") if set by SetbackMitigation (phase path)
 * and honor it as a temporary edit suppression.
 */
@MitigationInfo(key = "block", desc = "Deny place/break and optionally heal ghost blocks")
public final class BlockMitigation implements Mitigation {
    public String key() { return "block"; }

    public void onApply(User user, MitigationContext ctx) {
        // Deny flags for listeners
        ctx.param("deny_place", ctx.param("deny_place", "true"));
        ctx.param("deny_break", ctx.param("deny_break", "true"));

        // Optional immediate heal around the player
        int radius = parseInt(ctx.param("heal_radius", "0"), 0);
        if (radius <= 0) return;

        Player p = (user != null ? user.getPlayer() : null);
        if (p == null) return;

        try { healAround(p, radius); } catch (Throwable ignored) {}
    }

    public void onRemove(User user, MitigationContext ctx) { /* flags auto-expire when ctx expires */ }

    // ---- ghost heal (Beta-friendly via reflection) ----
    private static void healAround(Player p, int r) throws Exception {
        Location base = p.getLocation();
        World w = base.getWorld();
        if (w == null) return;

        // Reflect to avoid import differences:
        Method getBlockAt = World.class.getMethod("getBlockAt", int.class, int.class, int.class);
        Method getTypeId  = getBlockAt.getReturnType().getMethod("getTypeId");  // int (old)
        Method getData    = null;
        try { getData = getBlockAt.getReturnType().getMethod("getData"); } catch (NoSuchMethodException ignored) {}

        // Player#sendBlockChange(Location, int, byte) (old) OR (Location, Material, byte) (new)
        Method sendOld = null, sendNew = null;
        try { sendOld = Player.class.getMethod("sendBlockChange", Location.class, int.class, byte.class); } catch (NoSuchMethodException ignored) {}
        if (sendOld == null) {
            // try new signature with Material
            try {
                Class<?> matCls = Class.forName("org.bukkit.Material");
                sendNew = Player.class.getMethod("sendBlockChange", Location.class, matCls, byte.class);
            } catch (Throwable ignored) {}
        }

        int bx = base.getBlockX(), by = base.getBlockY(), bz = base.getBlockZ();
        for (int x = bx - r; x <= bx + r; x++) {
            for (int y = Math.max(0, by - r); y <= by + r; y++) {
                for (int z = bz - r; z <= bz + r; z++) {
                    Object block = getBlockAt.invoke(w, x, y, z);
                    int typeId = (Integer) getTypeId.invoke(block);
                    byte data = 0;
                    if (getData != null) {
                        Object d = getData.invoke(block);
                        if (d instanceof Byte) data = (Byte) d;
                        else if (d instanceof Integer) data = (byte) ((Integer) d).intValue();
                    }
                    Location at = new Location(w, x, y, z);
                    if (sendOld != null) {
                        sendOld.invoke(p, at, typeId, data);
                    } else if (sendNew != null) {
                        // Material.getMaterial(int)
                        Object mat = Class.forName("org.bukkit.Material").getMethod("getMaterial", int.class).invoke(null, typeId);
                        sendNew.invoke(p, at, mat, data);
                    }
                }
            }
        }
    }

    private static int parseInt(String s, int def) { try { return Integer.parseInt(s); } catch (Throwable t) { return def; } }
}