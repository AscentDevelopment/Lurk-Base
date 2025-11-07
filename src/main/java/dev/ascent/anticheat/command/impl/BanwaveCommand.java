package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.model.BanwaveEntry;
import dev.ascent.anticheat.service.BanwaveService;
import dev.ascent.anticheat.util.ConsoleMsgPatch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public final class BanwaveCommand extends AbstractCommand {
    private final Lurk plugin;

    public BanwaveCommand(Lurk plugin) {
        super("banwave",
              "banwave <add|remove|list|clear|run|template> …",
              "Manage and execute banwaves",
              "lurk.banwave",
              "bw");
        this.plugin = plugin;
    }

    public void execute(CommandContext ctx) {
        CommandSender s = ctx.sender();
        if (ctx.argLen() == 0) { help(s, ctx.label()); return; }

        String sub = ctx.arg(0).toLowerCase();

        if (sub.equals("add")) {
            if (ctx.argLen() < 2) { ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " banwave add <player> [reason...]"); return; }
            String name = ctx.arg(1);
            String reason = joinArgs(ctx, 2);
            UUID uuid = BanwaveService.resolveUuidByBestEffort(name);
            String addedBy = (s instanceof Player) ? ((Player) s).getName() : "CONSOLE";
            boolean ok = BanwaveService.add(uuid, name, reason, addedBy, 0);
            ConsoleMsgPatch.send(s, ok ? ("§aQueued §e" + name + " §7for banwave.") : ("§e" + name + " §7is already queued."));
            return;
        }

        if (sub.equals("remove")) {
            if (ctx.argLen() < 2) { ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " banwave remove <player>"); return; }
            String name = ctx.arg(1);
            UUID uuid = BanwaveService.resolveUuidByBestEffort(name);
            boolean ok = BanwaveService.remove(uuid);
            ConsoleMsgPatch.send(s, ok ? ("§aRemoved §e" + name + " §7from queue.") : ("§e" + name + " §7was not in the queue."));
            return;
        }

        if (sub.equals("list")) {
            Collection<BanwaveEntry> list = BanwaveService.list();
            ConsoleMsgPatch.send(s, "§6Banwave Queue §7(" + BanwaveService.size() + "):");
            if (list.isEmpty()) { ConsoleMsgPatch.send(s, "§7<empty>"); return; }
            for (Iterator<BanwaveEntry> it = list.iterator(); it.hasNext();) {
                BanwaveEntry e = it.next();
                ConsoleMsgPatch.send(s, "§f• §e" + e.getName() + " §8(" + e.getUuid() + ")§7 — " + e.getReason());
            }
            return;
        }

        if (sub.equals("clear")) { BanwaveService.clear(); ConsoleMsgPatch.send(s, "§aCleared the banwave queue."); return; }

        if (sub.equals("run")) {
            boolean silent = (ctx.argLen() >= 2) && ctx.arg(1).equalsIgnoreCase("silent");
            BanwaveService.runBanwave(s, !silent);
            return;
        }

        if (sub.equals("template")) {
            if (ctx.argLen() == 1) {
                ConsoleMsgPatch.send(s, "§7Current ban template: §f" + BanwaveService.getBanCommandTemplate());
                ConsoleMsgPatch.send(s, "§7Placeholders: §f%player% §7%uuid% §7%reason%");
                return;
            }
            String newTpl = joinArgs(ctx, 1);
            if (newTpl.indexOf("%player%") < 0) { ConsoleMsgPatch.send(s, "§cTemplate must include %player%."); return; }
            BanwaveService.setBanCommandTemplate(newTpl);
            ConsoleMsgPatch.send(s, "§aUpdated ban template to: §f" + newTpl);
            return;
        }

        help(s, ctx.label());
    }

    private static String joinArgs(CommandContext ctx, int start) {
        if (ctx.argLen() <= start) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < ctx.argLen(); i++) { if (i > start) sb.append(' '); sb.append(ctx.arg(i)); }
        return sb.toString();
    }

    private static void help(CommandSender s, String root) {
        ConsoleMsgPatch.send(s, "§6Lurk §7— §fBanwave");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave add <player> [reason...]");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave remove <player>");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave list");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave clear");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave run §8[silent]");
        ConsoleMsgPatch.send(s, "§e/" + root + " banwave template §8[set a custom console command]");
    }
}
