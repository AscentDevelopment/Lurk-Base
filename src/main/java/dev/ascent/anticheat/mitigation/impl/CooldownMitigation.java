package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "attack_cooldown", desc = "Impose a minimum delay between attacks")
public final class CooldownMitigation implements Mitigation {
    public String key() { return "cooldown"; }
    public void onApply(User user, MitigationContext ctx) { }
    public void onRemove(User user, MitigationContext ctx) { }

    /** Returns true if the attack should be cancelled due to cooldown. */
    public static boolean shouldCancel(long nowMs, long lastAttackMs, MitigationContext ctx) {
        long minGap = parseLong(ctx.param("min_interval_ms", "250"), 250L);
        return (nowMs - lastAttackMs) < minGap;
    }
    private static long parseLong(String s, long def) { try { return Long.parseLong(s); } catch (Throwable t) { return def; } }
}
