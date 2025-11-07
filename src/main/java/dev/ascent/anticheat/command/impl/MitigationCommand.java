package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationManager;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.user.UserManager;
import dev.ascent.anticheat.util.ConsoleMsgPatch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class MitigationCommand extends AbstractCommand {
    private final Lurk plugin;

    public MitigationCommand(Lurk plugin) {
        super("mitigation",
              "mitigation <list|apply|remove|clear> …",
              "Manage active mitigations on players",
              "lurk.mitigation",
              "miti","mit");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandContext ctx) {
        CommandSender s = ctx.sender();
        if (ctx.argLen() == 0) { help(s, ctx.label()); return; }

        String sub = ctx.arg(0).toLowerCase();

        if (sub.equals("list")) {
            if (ctx.argLen() < 2) { ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " mitigation list <player>"); return; }
            Player t = plugin.getServer().getPlayer(ctx.arg(1));
            if (t == null) { ConsoleMsgPatch.send(s, "§cPlayer not found."); return; }
            User u = plugin.getUserManager().getUser(t);
            if (u == null) { ConsoleMsgPatch.send(s, "§cNo user record."); return; }

            MitigationManager mm = plugin.getMitigationManager();
            String[] known = new String[] {
				// Combat
                "angle",
				"attack_cooldown",
				"autoblock_cancel",
				"cps_cap",
				"crit_cancel",
				"damage",
				"debounce",
				"fake_damage",
                "knockback",
				"reach",
				"reversal",
				// Movement
				"setback",
                "slowdown",
				// Block & World
                "block",
                "cancel_interact",
                "inventory_lock",
				// Misc
                "run_command",
				"potion",
            };

            StringBuilder sb = new StringBuilder();
            int count = 0;
            long now = System.currentTimeMillis();
            for (int i = 0; i < known.length; i++) {
                String k = known[i];
                MitigationContext c = mm.get(u, k);
                if (c != null && !c.isExpired(now)) {
                    if (count++ > 0) sb.append(", ");
                    sb.append(k);
                }
            }
            if (count == 0) ConsoleMsgPatch.send(s, "§7No active mitigations.");
            else ConsoleMsgPatch.send(s, "§7Active: §f" + sb.toString());
            return;
        }

        if (sub.equals("apply")) {
            if (ctx.argLen() < 3) {
                ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " mitigation apply <player> <key> [durationMs] [k=v ...]");
                return;
            }
            Player t = plugin.getServer().getPlayer(ctx.arg(1));
            if (t == null) { ConsoleMsgPatch.send(s, "§cPlayer not found."); return; }
            User u = plugin.getUserManager().getUser(t);
            if (u == null) { ConsoleMsgPatch.send(s, "§cNo user record."); return; }
            String key = ctx.arg(2).toLowerCase();

            int idx = 3;
            long duration = 0L;
            if (ctx.argLen() > idx) {
                try { duration = Long.parseLong(ctx.arg(idx)); idx++; } catch (Throwable ignored) {}
            }

            Map<String,String> params = new HashMap<String,String>();
            for (int i = idx; i < ctx.argLen(); i++) {
                String token = ctx.arg(i);
                int eq = token.indexOf('=');
                if (eq > 0) {
                    String k = token.substring(0, eq);
                    String v = token.substring(eq + 1);
                    params.put(k, v);
                }
            }

            MitigationContext mctx = new MitigationContext("manual",
                    (duration <= 0 ? 0L : System.currentTimeMillis() + duration),
                    params);

            boolean ok = plugin.getMitigationManager().apply(u, key, mctx);
            if (ok) ConsoleMsgPatch.send(s, "§aApplied §e" + key + "§a to §f" + t.getName() + (duration > 0 ? (" §7(" + duration + "ms)") : ""));
            else ConsoleMsgPatch.send(s, "§cUnknown or unregistered mitigation key: §f" + key);
            return;
        }

        if (sub.equals("remove")) {
            if (ctx.argLen() < 3) {
                ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " mitigation remove <player> <key>");
                return;
            }
            Player t = plugin.getServer().getPlayer(ctx.arg(1));
            if (t == null) { ConsoleMsgPatch.send(s, "§cPlayer not found."); return; }
            User u = plugin.getUserManager().getUser(t);
            if (u == null) { ConsoleMsgPatch.send(s, "§cNo user record."); return; }
            String key = ctx.arg(2).toLowerCase();

            boolean removed = plugin.getMitigationManager().remove(u, key);
            ConsoleMsgPatch.send(s, removed
                    ? ("§aRemoved §e" + key + "§a from §f" + t.getName())
                    : ("§e" + key + " §7was not active for §f" + t.getName()));
            return;
        }

        if (sub.equals("clear")) {
            if (ctx.argLen() < 2) {
                ConsoleMsgPatch.send(s, "§cUsage: /" + ctx.label() + " mitigation clear <player>");
                return;
            }
            Player t = plugin.getServer().getPlayer(ctx.arg(1));
            if (t == null) { ConsoleMsgPatch.send(s, "§cPlayer not found."); return; }
            User u = plugin.getUserManager().getUser(t);
            if (u == null) { ConsoleMsgPatch.send(s, "§cNo user record."); return; }

            plugin.getMitigationManager().clearAll(u);
            ConsoleMsgPatch.send(s, "§aCleared all mitigations for §f" + t.getName());
            return;
        }

        help(s, ctx.label());
    }

    private static void help(CommandSender s, String root) {
        ConsoleMsgPatch.send(s, "§6Lurk §7— §fMitigation");
        ConsoleMsgPatch.send(s, "§e/" + root + " mitigation list <player>");
        ConsoleMsgPatch.send(s, "§e/" + root + " mitigation apply <player> <key> §8[durationMs] [k=v ...]");
        ConsoleMsgPatch.send(s, "§e/" + root + " mitigation remove <player> <key>");
        ConsoleMsgPatch.send(s, "§e/" + root + " mitigation clear <player>");
    }
}
