package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.service.AlertsService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class AlertsCommand extends AbstractCommand {

    public AlertsCommand() {
        super("alerts", "alerts [on|off|global <on|off>]", "Toggle or control alerts", "lurk.alerts",
                "togglealerts", "ta");
    }

    @Override
    public void execute(CommandContext ctx) {
        CommandSender s = ctx.sender();

        // No args: toggle personal alerts
        if (ctx.argLen() == 0) {
            boolean now = AlertsService.toggle(s);
            s.sendMessage("§7Alerts: §a" + (now ? "enabled" : "disabled"));
            return;
        }

        String sub = ctx.arg(0).toLowerCase();

        // Explicit personal on/off
        if (sub.equals("on") || sub.equals("off")) {
            if (!(s instanceof Player)) {
                s.sendMessage("§cOnly players can toggle their own alerts.");
                return;
            }
            boolean val = sub.equals("on");
            Player p = (Player) s;
            UUID id = p.getUniqueId();

            // Align exact state without triple-flips
            boolean cur = AlertsService.isEnabled(id);
            if (cur != val) {
                AlertsService.toggle(s); // flip once if needed
            }
            s.sendMessage("§7Alerts: §a" + (val ? "enabled" : "disabled"));
            return;
        }

        // Global on/off
        if (sub.equals("global")) {
            if (ctx.argLen() < 2) {
                s.sendMessage("§7Usage: /" + ctx.label() + " alerts global <on|off>");
                return;
            }
            String v = ctx.arg(1).toLowerCase();
            if (!v.equals("on") && !v.equals("off")) {
                s.sendMessage("§cUse on/off.");
                return;
            }
            AlertsService.setGlobalEnabled(v.equals("on"));
            s.sendMessage("§7Global alerts: §a" + (AlertsService.isGlobalEnabled() ? "enabled" : "disabled"));
            return;
        }

        s.sendMessage("§7Usage: /" + ctx.label() + " " + getUsage());
    }
}
