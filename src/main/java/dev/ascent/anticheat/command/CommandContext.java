package dev.ascent.anticheat.command;

import org.bukkit.command.CommandSender;

public final class CommandContext {
    private final CommandSender sender;
    private final String rootLabel;
    private final String[] args;

    public CommandContext(CommandSender sender, String rootLabel, String[] args) {
        this.sender = sender;
        this.rootLabel = rootLabel;
        this.args = args;
    }

    public CommandSender sender() { return sender; }
    public String label() { return rootLabel; }
    public String[] args() { return args; }
    public int argLen() { return args.length; }
    public String arg(int i) { return (i >= 0 && i < args.length) ? args[i] : null; }
}