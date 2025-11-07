package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.check.Check;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationManager;
import dev.ascent.anticheat.process.processors.InteractionProcessor;
import dev.ascent.anticheat.process.processors.KeepAliveProcessor;
import dev.ascent.anticheat.process.processors.MovementProcessor;
import dev.ascent.anticheat.process.processors.SessionProcessor;
import dev.ascent.anticheat.service.ExemptService;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public final class InfoCommand extends AbstractCommand {
    private final Lurk plugin;

    public InfoCommand(Lurk plugin) {
        super("info", "info [player]", "Show anticheat-relevant info about a player", "lurk.info", "whois", "inspect");
        this.plugin = plugin;
    }

    public void execute(CommandContext ctx) {
        CommandSender sender = ctx.sender();

        Player target;
        if (ctx.argLen() >= 1) {
            target = plugin.getServer().getPlayer(ctx.arg(0));
            if (target == null) { sender.sendMessage("§cPlayer not found."); return; }
        } else {
            if (!(sender instanceof Player)) { sender.sendMessage("§cUsage: /" + ctx.label() + " info <player>"); return; }
            target = (Player) sender;
        }

        UserManager um = plugin.getUserManager();
        User user = um.getUser(target);
        if (user == null) { sender.sendMessage("§cNo user record (not tracked yet)."); return; }

        MovementProcessor    mv = user.getProcessorManager() != null ? user.getProcessorManager().get(MovementProcessor.class)    : null;
        KeepAliveProcessor   ka = user.getProcessorManager() != null ? user.getProcessorManager().get(KeepAliveProcessor.class)   : null;
        SessionProcessor     sp = user.getProcessorManager() != null ? user.getProcessorManager().get(SessionProcessor.class)     : null;
        InteractionProcessor ip = user.getProcessorManager() != null ? user.getProcessorManager().get(InteractionProcessor.class) : null;

        String name  = target.getName();
        String uuid  = user.getUuid().toString();
        String brand = (sp != null && sp.getClientName() != null) ? sp.getClientName() : "unknown/vanilla";
        String proto = (sp != null && sp.getClientProtocol() != null) ? String.valueOf(sp.getClientProtocol().intValue()) : "n/a";

        String ping = "n/a";
        if (ka != null && ka.getLastKeepAliveMs() > 0) {
            long age = System.currentTimeMillis() - ka.getLastKeepAliveMs();
            ping = "~" + age + " ms since last keepalive";
        }

        long now = System.currentTimeMillis();
        long sinceJoinMs = (sp != null ? (now - sp.getJoinMillis()) : -1L);
        String session   = sinceJoinMs >= 0 ? fmtDur(sinceJoinMs) : "n/a";
        String total     = (sp != null && sp.getTotalPlayMillis() >= 0) ? fmtDur(sp.getTotalPlayMillis()) : "n/a";

        String pos="n/a", rot="n/a", vel="n/a", ground="n/a", mouseStep="n/a";
        if (mv != null) {
            pos = String.format("x=%.2f y=%.2f z=%.2f", mv.getTo().getPosX(), mv.getTo().getPosY(), mv.getTo().getPosZ());
            rot = String.format("yaw=%.2f pitch=%.2f", mv.getTo().getYaw(), mv.getTo().getPitch());
            vel = String.format("Δxz=%.4f Δy=%.4f (last Δxz=%.4f)", mv.getDeltaXZ(), mv.getDeltaY(), mv.getLastDeltaXZ());
            ground = String.valueOf(mv.getTo().isOnGround());
            mouseStep = String.format("%.4f° yaw-step", mv.getDeltaYawAbs());
        }

        String cpsLine = "n/a";
        String lastAttack = "n/a";
        if (ip != null) {
            double cpsNow = ip.getCpsNow();
            double cpsAvg = ip.getCpsAvg();
            double cpsMin = ip.getCpsMin();
            double cpsMax = ip.getCpsMax();
            cpsLine = String.format("now=%.2f avg=%.2f min=%.2f max=%.2f", cpsNow, cpsAvg, cpsMin, cpsMax);
            if (ip.getLastAttackMillis() > 0) lastAttack = fmtDur(now - ip.getLastAttackMillis()) + " ago";
        }

        double sessionVL = 0.0D;
        if (user.getChecks() != null) {
            for (int i = 0; i < user.getChecks().size(); i++) {
                Check c = user.getChecks().get(i);
                if (c != null) sessionVL += c.getViolations();
            }
        }
        double totalVL = sessionVL;

        boolean exemptAll = ExemptService.isExemptAll(user.getUuid());
        Set<String> exempt = ExemptService.getExemptChecks(user.getUuid());
        String exList = (exempt == null || exempt.isEmpty()) ? "none" : join(exempt);

        String mitiLine = "none";
        try {
            MitigationManager mm = Lurk.getInstance().getMitigationManager();
            if (mm != null) {
                String[] known = new String[] { "setback","damage_reduction","kb_reduction","attack_cooldown","velocity_cap","run_command" };
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (int i = 0; i < known.length; i++) {
                    String k = known[i];
                    dev.ascent.anticheat.mitigation.MitigationContext mctx = mm.get(user, k); // rename to mctx
                    if (mctx != null && !mctx.isExpired(System.currentTimeMillis())) {
                        if (count++ > 0) sb.append(", ");
                        sb.append(k);
                    }
                }
                if (count > 0) mitiLine = sb.toString();
            }
        } catch (Throwable ignored) { }

        sender.sendMessage("§6Lurk §7— §fPlayer Info");
        sender.sendMessage("§7Name: §f" + name + " §7UUID: §f" + uuid);
        sender.sendMessage("§7Client/Launcher: §f" + brand + " §8(proto: " + proto + ")");
        sender.sendMessage("§7Ping: §f" + ping);
        sender.sendMessage("§7Session: §f" + session + " §8| Total: §f" + total);
        sender.sendMessage("§7Pos: §f" + pos);
        sender.sendMessage("§7Rot: §f" + rot + " §8| MouseStep: §f" + mouseStep);
        sender.sendMessage("§7Motion: §f" + vel + " §8| Ground: §f" + ground);
        sender.sendMessage("§7CPS: §f" + cpsLine + " §8| Last Attack: §f" + lastAttack);
        sender.sendMessage("§7Violations: §f" + sessionVL + " §8(session) §7/ §f" + totalVL + " §8(total)");
        sender.sendMessage("§7Exempt: §fAll=" + exemptAll + " §8| §7Checks: §f" + exList);
        sender.sendMessage("§7Active Mitigations: §f" + mitiLine);
    }

    private static String fmtDur(long ms) {
        long s = ms / 1000L, h = s / 3600L, m = (s % 3600L) / 60L, ss = (s % 60L);
        if (h > 0) return h + "h " + m + "m " + ss + "s";
        if (m > 0) return m + "m " + ss + "s";
        return ss + "s";
    }
    private static String join(Collection<String> c) {
        StringBuilder sb = new StringBuilder(); Iterator<String> it = c.iterator();
        while (it.hasNext()) { sb.append(it.next()); if (it.hasNext()) sb.append(", "); }
        return sb.toString();
    }
}