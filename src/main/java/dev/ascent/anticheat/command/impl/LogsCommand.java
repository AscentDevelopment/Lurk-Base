package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.model.LogEntry;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.user.UserManager;
import dev.ascent.anticheat.service.LogService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class LogsCommand extends AbstractCommand {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    public LogsCommand() {
        super("logs", "logs <player> [limit]", "Show recent violation logs for a player", "lurk.logs");
    }

    public void execute(CommandContext ctx) {
        CommandSender s = ctx.sender();
        if (ctx.argLen() < 1) {
            s.sendMessage("§cUsage: /" + ctx.label() + " logs <player> [limit]");
            return;
        }

        Player target = Lurk.getInstance().getServer().getPlayer(ctx.arg(0));
        if (target == null) {
            s.sendMessage("§cPlayer not found.");
            return;
        }

        UserManager um = Lurk.getInstance().getUserManager();
        User user = um.getUser(target);
        if (user == null) {
            s.sendMessage("§cNo user record (not tracked yet).");
            return;
        }

        int limit = 15;
        if (ctx.argLen() >= 2) {
            try { limit = Math.max(1, Math.min(100, Integer.parseInt(ctx.arg(1)))); } catch (Throwable ignored) {}
        }

        UUID uuid = user.getUuid();
        List<LogEntry> entries = LogService.getLast(uuid, limit);
        if (entries.isEmpty()) {
            s.sendMessage("§7No logs for §f" + target.getName());
            return;
        }

        s.sendMessage("§6Lurk §7— §fLogs for §e" + target.getName() + " §7(latest " + entries.size() + "):");
        for (int i = 0; i < entries.size(); i++) {
            LogEntry le = entries.get(i);
            String t = SDF.format(new Date(le.timeMillis));
            s.sendMessage("§8[" + t + "] §f" + le.checkName + "§7(" + le.checkType + ") §7VL§f" + le.vl + " §7- §f" + le.details);
        }
    }
}
