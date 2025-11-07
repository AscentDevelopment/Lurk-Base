package dev.ascent.anticheat.command;

import dev.ascent.anticheat.util.ConsoleMsgPatch;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements BaseCommand {

    private final String name, usage, description, permission;
    private final String[] aliases;

    protected AbstractCommand(String name, String usage, String description, String permission, String... aliases) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.permission = permission;
        this.aliases = (aliases == null) ? new String[0] : aliases;
    }

    public String getName() { return name; }
    public String[] getAliases() { return aliases; }
    public String getUsage() { return usage; }
    public String getDescription() { return description; }
    public String getPermission() { return permission; }

    public boolean canUse(CommandContext ctx) {
        String perm = getPermission();
        if (perm == null || perm.length() == 0) return true;
        CommandSender s = ctx.sender();
        return s.isOp() || s.hasPermission(perm);
    }

    /** Convenience: sends a message to the sender and mirrors to console if needed. */
    protected void msg(CommandContext ctx, String message) {
        ConsoleMsgPatch.send(ctx.sender(), message);
    }

    /** Convenience: colorless/plain message (auto-strips color for console anyway). */
    protected void msg(CommandSender sender, String message) {
        ConsoleMsgPatch.send(sender, message);
    }
}
