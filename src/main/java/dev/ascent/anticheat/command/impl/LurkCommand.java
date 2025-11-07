package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;

public final class LurkCommand extends AbstractCommand {
    @SuppressWarnings("unused")
    private final Lurk plugin;

    public LurkCommand(Lurk plugin) {
        super("help", "help", "Show all available Lurk commands", "lurk.use", "h", "commands", "lurk");
        this.plugin = plugin;
    }

    public void execute(CommandContext ctx) {
        String root = ctx.label();
        ctx.sender().sendMessage("§6Lurk §7— §fCommands");
        ctx.sender().sendMessage("§e/" + root + " help §7- §fShow this help");
		ctx.sender().sendMessage("§e/" + root + " alerts §7- §fToggle alerts");
		ctx.sender().sendMessage("§e/" + root + " banwave §7- §fManage Lurk banwave");
		ctx.sender().sendMessage("§e/" + root + " bypass §7- §fExempt players from specific or all checks");
        ctx.sender().sendMessage("§e/" + root + " info §7[player] - §fShow player AC-relevant info");
        ctx.sender().sendMessage("§e/" + root + " logs §7- §fShow all violations & logs on a player");
        ctx.sender().sendMessage("§e/" + root + " version §7- §fShow anticheat version");
		ctx.sender().sendMessage("§e/" + root + " reload §7- §fReload the plugin.");
    }
}