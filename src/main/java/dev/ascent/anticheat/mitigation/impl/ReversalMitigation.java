package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

@MitigationInfo(key = "reversal", desc = "Reflect hits: attacker suffers damage/KB instead of victim")
public final class ReversalMitigation implements Mitigation {
    public String key() { return "reversal"; }

    public void onApply(User user, MitigationContext ctx) { /* handled on hit */ }
    public void onRemove(User user, MitigationContext ctx) { }

    public static void apply(EntityDamageByEntityEvent dmg, Player attacker, Player victim, MitigationContext ctx) {
        if (attacker == null) return;

        boolean reflectDamage = parseBool(ctx.param("reflect_damage", "true"), true);
        boolean reflectKb     = parseBool(ctx.param("reflect_kb", "true"), true);
        double dmgScale       = parseDouble(ctx.param("damage_scale", "1.0"), 1.0);
        double kbScale        = parseDouble(ctx.param("kb_scale", "1.0"), 1.0);

        // Cancel original hit
        dmg.setCancelled(true);

        // Reflect damage (int for Beta API)
        if (reflectDamage) {
            int amount = (int) Math.max(1, Math.round(dmg.getDamage() * Math.max(0.0, dmgScale)));
            try {
                // Try damage(int, Entity) first (if present in the server build)
                try {
                    attacker.getClass()
                            .getMethod("damage", int.class, org.bukkit.entity.Entity.class)
                            .invoke(attacker, amount, victim);
                } catch (Throwable t2) {
                    // Fallback: damage(int)
                    attacker.damage(amount);
                }
            } catch (Throwable ignored) { }
        }

        // Reflect simple knockback
        if (reflectKb) {
            try {
                Vector kb = computeReverseKb(attacker, victim, kbScale);
                if (kb != null) attacker.setVelocity(kb);
            } catch (Throwable ignored) { }
        }
    }

    private static Vector computeReverseKb(Player attacker, Player victim, double kbScale) {
        Location a = attacker.getLocation();
        Location from = (victim != null ? victim.getLocation() : a);
        Vector dir = a.toVector().subtract(from.toVector()); // away from victim
        if (dir.lengthSquared() < 1.0E-6) dir = new Vector(0.0, 0.0, 0.0);
        else dir.normalize();
        // Vanilla-ish KB: push back + slight lift
        double h = 0.35 * Math.max(0.0, kbScale);
        double y = 0.36 * Math.max(0.0, kbScale);
        return new Vector(dir.getX() * h, y, dir.getZ() * h);
    }

    private static boolean parseBool(String s, boolean def) {
        if (s == null) return def;
        if ("true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "1".equals(s)) return true;
        if ("false".equalsIgnoreCase(s) || "no".equalsIgnoreCase(s) || "0".equals(s)) return false;
        return def;
    }
    private static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Throwable t) { return def; }
    }
}
