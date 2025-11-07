package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "knockback", desc = "Scale applied knockback (reduce or increase)")
public final class KnockbackMitigation implements Mitigation {
    public String key() { return "knockback"; }

    public void onApply(User user, MitigationContext ctx) {
        // Defaults: neutral scalar with sane clamps
        ctx.param("scalar", "1.00"); // 1.00 = no change; <1 reduce KB, >1 increase KB
        ctx.param("min",    "0.10"); // don’t fully zero KB unless you want to
        ctx.param("max",    "1.75"); // cap any “over-KB” to avoid grief
    }

    public void onRemove(User user, MitigationContext ctx) { }

    /** Read the configured scalar with clamps. Call this where you apply velocity/KB. */
    public static double computeScalar(MitigationContext ctx) {
        double scalar = parseDouble(safeGet(ctx, "scalar", "1.00"), 1.00);
        double min    = parseDouble(safeGet(ctx, "min",    "0.10"), 0.10);
        double max    = parseDouble(safeGet(ctx, "max",    "1.75"), 1.75);
        if (scalar < min) scalar = min;
        if (scalar > max) scalar = max;
        return scalar;
    }

    private static String safeGet(MitigationContext ctx, String key, String def) {
        try {
            java.lang.reflect.Method m = ctx.getClass().getMethod("get", String.class);
            Object o = m.invoke(ctx, key);
            if (o instanceof String) return (String) o;
        } catch (Throwable ignored) {}
        return def;
    }
    private static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Throwable t) { return def; }
    }
}
