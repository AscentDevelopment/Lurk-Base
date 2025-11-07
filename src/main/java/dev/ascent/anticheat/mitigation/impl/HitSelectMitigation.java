package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

/**
 * Enforces a minimum time between *accepted* hits (hit-select guard).
 *
 * Key: "hit_select_delay"
 *
 * Params:
 *  - min_interval_ms: long (default 100)
 *  - cps_gate: double (optional – your pipeline can check cps >= gate before applying)
 *
 * Usage in your attack pipeline:
 *  - ctx = mm.get(user, "hit_select")
 *  - if (ctx != null && !ctx.isExpired(now) && !HitSelectMitigation.allow(now, ctx)) cancel this hit
 *  - else accept and record
 */
@MitigationInfo(key = "hit_select", desc = "Require a minimum interval between accepted hits")
public final class HitSelectMitigation implements Mitigation {
    public String key() { return "hit_select"; }

    public void onApply(User user, MitigationContext ctx) {
        if (ctx.param("min_interval_ms", null) == null) ctx.param("min_interval_ms", "100");
        if (ctx.param("last_hit", null) == null)         ctx.param("last_hit", "0");
    }

    public void onRemove(User user, MitigationContext ctx) { }

    public static boolean allow(long nowMs, MitigationContext ctx) {
        long last = parseLong(ctx.param("last_hit", "0"), 0L);
        long min  = parseLong(ctx.param("min_interval_ms", "100"), 100L);
        if (nowMs - last < min) return false;
        ctx.param("last_hit", String.valueOf(nowMs));
        return true;
    }

    private static long parseLong(String s, long def) { try { return Long.parseLong(s); } catch (Throwable t) { return def; } }
}
