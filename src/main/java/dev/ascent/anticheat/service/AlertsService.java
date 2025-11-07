package dev.ascent.anticheat.service;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AlertsService {
    private static final Map<UUID, Boolean> STAFF_ALERTS = new ConcurrentHashMap<UUID, Boolean>();
    private static volatile boolean GLOBAL_ENABLED = true;

    private AlertsService() {}

    public static boolean isGlobalEnabled() { return GLOBAL_ENABLED; }
    public static void setGlobalEnabled(boolean enabled) { GLOBAL_ENABLED = enabled; }

    public static boolean toggle(CommandSender sender) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        boolean newState = !isEnabled(p.getUniqueId());
        STAFF_ALERTS.put(p.getUniqueId(), Boolean.valueOf(newState));
        return newState;
    }

    public static boolean isEnabled(UUID uuid) {
        Boolean v = STAFF_ALERTS.get(uuid);
        return v != null ? v.booleanValue() : true; // default on
    }
}
