package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

/**
 * Damage scaling for a player under mitigation.
 *
 * Key: "damage"  (kept for config compatibility)
 *
 * Params (all optional):
 *  - scale: double multiplier. If present, used directly (e.g. 0.7 reduce 30%, 1.25 boost 25%)
 *  - base_percent: int (default 20)      reduction baseline if no 'scale' given
 *  - per_vl: int (default 5)             extra reduction per VL over trigger
 *  - max_percent: int (default 80)       cap on reduction percent
 *
 * Use in listener:
 *   double mul = DamageMitigation.computeScale(ctx, currentVl, triggerVl);
 *   damage *= mul;
 */
@MitigationInfo(key = "damage", desc = "Scale damage dealt/taken by the mitigated player")
public final class DamageMitigation implements Mitigation {
    public String key() { return "damage"; }

    public void onApply(User user, MitigationContext ctx) { /* no-op */ }
    public void onRemove(User user, MitigationContext ctx) { /* no-op */ }

    /** Returns a multiplicative scale to apply to damage (can be <1 to reduce or >1 to increase). */
    public static double computeScale(MitigationContext ctx, double currentVl, int triggerVl) {
        // If explicit scale provided, honor it directly.
        String explicit = ctx.param("scale", null);
        if (explicit != null) {
            double s = parseDouble(explicit, 1.0);
            if (s < 0.0) s = 0.0;
            if (s > 5.0) s = 5.0; // sane guard
            return s;
        }

        // Otherwise, use legacy reduction-by-percent model:
        // result scale = 1.0 - reductionPct
        int base = parseInt(ctx.param("base_percent", "20"), 20);
        int per  = parseInt(ctx.param("per_vl",       "5"),  5);
        int cap  = parseInt(ctx.param("max_percent",  "80"), 80);

        int over = Math.max(0, ((int)Math.floor(currentVl)) - triggerVl);
        int pct  = base + over * per;
        if (pct > cap) pct = cap;
        if (pct < 0) pct = 0;

        double reduction = pct / 100.0D;
        double scale = 1.0 - reduction;          // 0.0 .. 1.0
        if (scale < 0.0) scale = 0.0;
        return scale;
    }

    private static int parseInt(String s, int def) { try { return Integer.parseInt(s); } catch (Throwable t) { return def; } }
    private static double parseDouble(String s, double def) { try { return Double.parseDouble(s); } catch (Throwable t) { return def; } }
}
