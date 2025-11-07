package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

/**
 * Click debounce: coalesce multiple attack attempts within a small window.
 *
 * Key: "debounce"
 *
 * Params:
 *  - debounce_ms: long (default 60)
 *  - cps_gate: double (optional – only enforce if current CPS >= gate; your attack path can check this)
 *
 * Usage in your attack/interaction path:
 *  - Read ctx = mm.get(user, "click_debounce")
 *  - if (ctx != null && !ctx.isExpired(now) && !DebounceMitigation.allow(now, ctx)) cancel this swing/attack packet
 *  - else accept and ctx.param("last_accept", String.valueOf(now))
 */
@MitigationInfo(key = "debounce", desc = "Debounce rapid attack clicks into a minimum window")
public final class DebounceMitigation implements Mitigation {
    public String key() { return "debounce"; }

    public void onApply(User user, MitigationContext ctx) {
        // Initialize state if absent
        if (ctx.param("debounce_ms", null) == null) ctx.param("debounce_ms", "60");
        if (ctx.param("last_accept", null) == null) ctx.param("last_accept", "0");
    }

    public void onRemove(User user, MitigationContext ctx) { /* stateless removal */ }

    // Helper used by your combat/interaction pipeline
    public static boolean allow(long nowMs, MitigationContext ctx) {
        long last = parseLong(ctx.param("last_accept", "0"), 0L);
        long win  = parseLong(ctx.param("debounce_ms", "60"), 60L);
        if (nowMs - last < win) return false;
        ctx.param("last_accept", String.valueOf(nowMs));
        return true;
    }

    private static long parseLong(String s, long def) { try { return Long.parseLong(s); } catch (Throwable t) { return def; } }
}
