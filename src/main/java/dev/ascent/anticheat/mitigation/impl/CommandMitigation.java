package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@MitigationInfo(key = "run_command", desc = "Run a console/server command with placeholders")
public final class CommandMitigation implements Mitigation {
    public String key() { return "run_command"; }

    public void onApply(User user, MitigationContext ctx) {
        String tpl = ctx.param("cmd", "");
        if (tpl == null || tpl.length() == 0) return;

        String name = (user != null && user.getPlayer() != null) ? user.getPlayer().getName() : "Unknown";
        String uuid = (user != null) ? String.valueOf(user.getUuid()) : "Unknown";
        String reason = ctx.param("reason", "mitigation");
        String cmd = tpl.replace("%player%", name).replace("%uuid%", uuid).replace("%reason%", reason);

        CommandSender console = safeConsole();
        if (console == null) return;
        try { Bukkit.getServer().dispatchCommand(console, cmd); } catch (Throwable ignored) {}
    }
    public void onRemove(User user, MitigationContext ctx) { }

    private static CommandSender safeConsole() {
        // Try Bukkit.getConsoleSender() (modern)
        try {
            java.lang.reflect.Method gm = Class.forName("org.bukkit.Bukkit").getMethod("getConsoleSender");
            Object o = gm.invoke(null);
            if (o instanceof CommandSender) return (CommandSender) o;
        } catch (Throwable ignored) { }
        // Fallback: Bukkit.getServer().getConsoleSender() (older)
        try {
            Object srv = Class.forName("org.bukkit.Bukkit").getMethod("getServer").invoke(null);
            java.lang.reflect.Method m = srv.getClass().getMethod("getConsoleSender");
            Object o2 = m.invoke(srv);
            if (o2 instanceof CommandSender) return (CommandSender) o2;
        } catch (Throwable ignored) { }
        return null;
    }
}
