package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;

@MitigationInfo(key = "inventory_lock", desc = "Block inventory open/click/close")
public final class InventoryMitigation implements Mitigation {
    public String key() { return "inventory_lock"; }

    public void onApply(User user, MitigationContext ctx) {
        ctx.param("block_open",  "true");
        ctx.param("block_click", "true");
        ctx.param("block_close", "false"); // allow close by default; set true if you want to hard lock GUI
    }

    public void onRemove(User user, MitigationContext ctx) { }
}
