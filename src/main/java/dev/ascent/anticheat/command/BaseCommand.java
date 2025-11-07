package dev.ascent.anticheat.command;

public interface BaseCommand {
    String getName();
    String[] getAliases();
    String getUsage();
    String getDescription();
    String getPermission();
    boolean canUse(CommandContext ctx);
    void execute(CommandContext ctx);
}