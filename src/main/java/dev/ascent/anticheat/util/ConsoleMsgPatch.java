package dev.ascent.anticheat.util;

import dev.ascent.anticheat.Lurk;
import org.bukkit.command.CommandSender;

/**
 * Handles message dispatch for both player and console senders without
 * printing duplicates on Beta 1.7.3, where modern API calls may not exist.
 */
public final class ConsoleMsgPatch {
    private ConsoleMsgPatch() {}

    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null) return;

        // Always try to send directly
        try {
            sender.sendMessage(message);
        } catch (Throwable ignored) {}

        // Mirror to console ONLY if sender is a player
        if (isPlayer(sender)) {
            try {
                Object srv = Lurk.getInstance().getServer();
                if (srv != null) {
                    // Try reflection for getLogger() (modern) or use System.out as fallback
                    try {
                        java.lang.reflect.Method m = srv.getClass().getMethod("getLogger");
                        Object logger = m.invoke(srv);
                        if (logger != null) {
                            java.lang.reflect.Method info = logger.getClass().getMethod("info", String.class);
                            info.invoke(logger, stripColor(message));
                            return;
                        }
                    } catch (Throwable ignored2) { }
                }
                System.out.println("[Lurk] " + stripColor(message));
            } catch (Throwable ignored) { }
        }
    }

    private static boolean isPlayer(Object obj) {
        if (obj == null) return false;
        try {
            Class<?> playerCls = Class.forName("org.bukkit.entity.Player");
            return playerCls.isInstance(obj);
        } catch (Throwable ignored) {
            // Beta fallback: try name heuristic
            try {
                if (obj instanceof CommandSender) {
                    // Beta CommandSender usually implements getName(), but use reflection
                    java.lang.reflect.Method m = obj.getClass().getMethod("getName");
                    Object name = m.invoke(obj);
                    if (name instanceof String) {
                        String n = ((String) name).toLowerCase();
                        return !n.contains("console") && !n.equals("server");
                    }
                }
            } catch (Throwable ignored2) {}
            return false;
        }
    }

    private static String stripColor(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '§') { i++; continue; }
            out.append(c);
        }
        return out.toString();
    }
}
