package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.*;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "crit_cancel", desc = "Disable critical-hit conditions while active")
public final class CritCancelMitigation implements Mitigation {
    public String key() { return "crit_cancel"; }

    public void onApply(User user, MitigationContext ctx) {
        // Your damage calc should ignore “airborne crit” multipliers when this is active.
        ctx.param("cancel_crit", "true");
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
