package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.*;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "angle", desc = "Require face angle within threshold to register hit")
public final class AngleMitigation implements Mitigation {
    public String key() { return "angle"; }

    public void onApply(User user, MitigationContext ctx) {
        // degrees; smaller = stricter (e.g., 55° max off-axis)
        ctx.param("max_angle_deg", "55.0");
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
