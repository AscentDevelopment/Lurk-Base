package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.*;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "cps_cap", desc = "Ignore attacks above a CPS limit")
public final class CPSMitigation implements Mitigation {
    public String key() { return "cps_cap"; }

    public void onApply(User user, MitigationContext ctx) {
        ctx.param("max_cps", "20.0");   // hard ceiling
        ctx.param("soft_cps","5.0");   // start dropping probabilistically if you want
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
