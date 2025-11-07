package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "slowdown", desc = "Scale player movement deltas by a factor")
public final class SlowdownMitigation implements Mitigation {
    public String key() { return "slowdown"; }

    public void onApply(User user, MitigationContext ctx) {
        // Movement processor should multiply deltaX/Z by "factor"
        ctx.param("factor", "0.7"); // 30% slower by default
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
