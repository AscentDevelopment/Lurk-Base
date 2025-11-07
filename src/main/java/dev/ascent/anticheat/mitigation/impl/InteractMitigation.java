package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "cancel_interact", desc = "Cancel entity interactions (attack/use)")
public final class InteractMitigation implements Mitigation {
    public String key() { return "cancel_interact"; }

    public void onApply(User user, MitigationContext ctx) {
        ctx.param("cancel_attack", "true");
        ctx.param("cancel_use", "true");
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
