package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.*;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "autoblock_cancel", desc = "Cancel autoblock (attack+use) synergy")
public final class AutoBlockMitigation implements Mitigation {
    public String key() { return "autoblock_cancel"; }

    public void onApply(User user, MitigationContext ctx) {
        // Interaction listener: if attack overlaps use-item window, cancel the use or the hit.
        ctx.param("cancel_use_during_attack", "true");
        ctx.param("cancel_attack_during_use", "false");
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
