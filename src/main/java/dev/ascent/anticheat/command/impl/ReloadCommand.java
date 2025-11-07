package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;

public final class ReloadCommand extends AbstractCommand {

    public ReloadCommand() {
        super("reload", "reload", "Reload Lurk configuration (lightweight)", "lurk.reload", "rl");
    }

    public void execute(CommandContext ctx) {
        // For Beta 1.7.3 we don't rely on Bukkit's modern config system here.
        // You can wire your own YAML reader and refresh services if needed.
        ctx.sender().sendMessage("§6Lurk §7— §fNothing to reload yet (no external config).");
        ctx.sender().sendMessage("§7Tip: add a ConfigService and read your YAML, then refresh limits/flags.");
    }
}
