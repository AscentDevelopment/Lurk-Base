package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.service.ExemptService;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public final class BypassCommand extends AbstractCommand {

    private final Lurk plugin;

    public BypassCommand(Lurk plugin) {
        super(
            "bypass",
            "bypass <player> <all|check> <on|off> | bypass <player> list | bypass <player> clear",
            "Exempt a player from a specific check or all checks",
            "lurk.bypass",
            "exempt"
        );
        this.plugin = plugin;
    }

    public void execute(CommandContext ctx) {
        CommandSender s = ctx.sender();

        if (ctx.argLen() < 2) {
            s.sendMessage("§eUsage: /" + ctx.label() + " " + getUsage());
            return;
        }

        Player target = plugin.getServer().getPlayer(ctx.arg(0));
        if (target == null) {
            s.sendMessage("§cPlayer not found.");
            return;
        }
        UserManager um = plugin.getUserManager();
        User user = um.getUser(target);
        if (user == null) {
            s.sendMessage("§cNo user record (not tracked yet).");
            return;
        }
        UUID id = user.getUuid();

        // list / clear
        if (ctx.argLen() == 2) {
            String op = ctx.arg(1).toLowerCase();

            if (op.equals("list")) {
                boolean all = ExemptService.isExemptAll(id);
                Set<String> checks = ExemptService.getExemptChecks(id);

                StringBuilder sb = new StringBuilder();
                if (checks.isEmpty()) sb.append("none");
                else {
                    Iterator<String> it = checks.iterator();
                    while (it.hasNext()) {
                        sb.append(it.next());
                        if (it.hasNext()) sb.append(", ");
                    }
                }

                s.sendMessage("§7Exemptions for §f" + target.getName() + "§7:");
                s.sendMessage("§7 - All: §f" + all);
                s.sendMessage("§7 - Checks: §f" + sb.toString());
                return;
            }

            if (op.equals("clear")) {
                ExemptService.clear(id);
                s.sendMessage("§aCleared all exemptions for §f" + target.getName());
                return;
            }
        }

        if (ctx.argLen() < 3) {
            s.sendMessage("§eUsage: /" + ctx.label() + " " + getUsage());
            return;
        }

        String what = ctx.arg(1);           // "all" or check simple name
        String toggle = ctx.arg(2).toLowerCase(); // "on"/"off"

        if (!toggle.equals("on") && !toggle.equals("off")) {
            s.sendMessage("§cUse on/off for the third argument.");
            return;
        }
        boolean enable = toggle.equals("on");

        if (what.equalsIgnoreCase("all")) {
            ExemptService.setExemptAll(id, enable);
            s.sendMessage("§a" + (enable ? "Enabled" : "Disabled") + " §fALL-check exemption for §f" + target.getName());
            return;
        }

        // treat as a check simple name (e.g., "SpeedA", "BadPacketsA", or just "speed")
        String key = what;
        ExemptService.setExemptAll(id, false); // ensure they are not in ALL override
        if (enable) {
            ExemptService.add(id, key);
            s.sendMessage("§aAdded exemption: §f" + key + " §7for §f" + target.getName());
        } else {
            boolean removed = ExemptService.remove(id, key);
            s.sendMessage((removed ? "§aRemoved" : "§cNo existing") + " exemption: §f" + key + " §7for §f" + target.getName());
        }
    }
}
