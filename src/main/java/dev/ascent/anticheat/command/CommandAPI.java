package dev.ascent.anticheat.command;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.command.impl.*;
import dev.ascent.anticheat.util.ConsoleMsgPatch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public final class CommandAPI implements CommandExecutor {

    private final Map<String, BaseCommand> subs = new LinkedHashMap<String, BaseCommand>();

    private final Set<String> primaryRoots = new HashSet<String>(Arrays.asList("lurk"));

    private final Set<String> directRoots  = new HashSet<String>(Arrays.asList(
            "alerts", "bypass", "exempt", "info", "logs", "version", "reload",
            "mitigation", "miti", "mit", "banwave"
    ));

    private final Lurk plugin;

    public CommandAPI(Lurk plugin) {
        this.plugin = plugin;

        register(new LurkCommand(plugin));     // help
        register(new InfoCommand(plugin));     // info
        register(new VersionCommand(plugin));  // version
        register(new AlertsCommand());         // alerts
        register(new LogsCommand());           // logs
        register(new MitigationCommand(plugin)); // mitigation  <-- pass plugin
        register(new ReloadCommand());         // reload
        register(new BypassCommand(plugin));   // bypass/exempt
        register(new BanwaveCommand(plugin));  // banwave
    }

    private void register(BaseCommand cmd) {
        subs.put(cmd.getName().toLowerCase(), cmd);
        String[] aliases = cmd.getAliases();
        for (int i = 0; i < aliases.length; i++) {
            subs.put(aliases[i].toLowerCase(), cmd);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String root = command.getName().toLowerCase();

        if (primaryRoots.contains(root)) {
            if (args.length == 0) {
                BaseCommand help = subs.get("help");
                if (help != null) help.execute(new CommandContext(sender, label, new String[0]));
                else ConsoleMsgPatch.send(sender, "§cNo help command registered.");
                return true;
            }

            BaseCommand sub = subs.get(args[0].toLowerCase());
            if (sub == null) {
                ConsoleMsgPatch.send(sender, "§cUnknown subcommand. Try §e/" + label + " help");
                return true;
            }

            String[] subArgs = (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
            CommandContext ctx = new CommandContext(sender, label, subArgs);

            if (!sub.canUse(ctx)) {
                ConsoleMsgPatch.send(sender, "§cYou do not have permission.");
                return true;
            }

            try { sub.execute(ctx); }
            catch (Throwable t) { ConsoleMsgPatch.send(sender, "§cAn error occurred while executing that command."); }
            return true;
        }

        if (directRoots.contains(root) || subs.containsKey(root)) {
            BaseCommand sub = subs.get(root);
            if (sub == null) { ConsoleMsgPatch.send(sender, "§cUnknown command."); return true; }
            CommandContext ctx = new CommandContext(sender, root, args);

            if (!sub.canUse(ctx)) { ConsoleMsgPatch.send(sender, "§cYou do not have permission."); return true; }

            try { sub.execute(ctx); }
            catch (Throwable t) { ConsoleMsgPatch.send(sender, "§cAn error occurred while executing that command."); }
            return true;
        }

        ConsoleMsgPatch.send(sender, "§cUnknown command.");
        return true;
    }
}
