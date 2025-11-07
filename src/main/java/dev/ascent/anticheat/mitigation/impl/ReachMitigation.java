package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.*;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "reach", desc = "Limit effective melee reach")
public final class ReachMitigation implements Mitigation {
    public String key() { return "reach"; }

    public void onApply(User user, MitigationContext ctx) {
        // Your interaction/hitselect path should read this max reach (blocks)
        // Typical vanilla reach is ~3.0–3.1 in older versions.
        ctx.param("max_reach", "3.0");
    }

    public void onRemove(User user, MitigationContext ctx) { }
}