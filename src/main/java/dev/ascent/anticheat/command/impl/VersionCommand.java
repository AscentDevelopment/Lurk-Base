package dev.ascent.anticheat.command.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.AbstractCommand;
import dev.ascent.anticheat.command.CommandContext;

public final class VersionCommand extends AbstractCommand {
    private final Lurk plugin;

    public VersionCommand(Lurk plugin) {
        super("version", "version", "Show anticheat/plugin version", "lurk.version", "ver", "v");
        this.plugin = plugin;
    }

    public void execute(CommandContext ctx) {
        String v = plugin.getDescription().getVersion();
        ctx.sender().sendMessage("§6Lurk §7— §fVersion: §e" + v);
    }
}