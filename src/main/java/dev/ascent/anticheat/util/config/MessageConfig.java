package dev.ascent.anticheat.util.config;

import org.bukkit.plugin.java.JavaPlugin;

/** messages.yml */
public final class MessageConfig extends BaseConfig {

    private static final String TEMPLATE =
            "# Lurk (Beta 1.7.3) - messages.yml\n" +
            "prefix: \"§6Lurk §7» §r\"\n" +
            "no_permission: \"§cYou do not have permission.\"\n" +
            "player_not_found: \"§cPlayer not found.\"\n" +
            "reloaded: \"§aConfiguration reloaded.\"\n" +
            "alerts_enabled: \"§aAlerts enabled.\"\n" +
            "alerts_disabled: \"§eAlerts disabled.\"\n" +
            "mitigation_applied: \"§aApplied %mit% to %player%\"\n" +
            "mitigation_removed: \"§aRemoved %mit% from %player%\"\n";

    public MessageConfig(JavaPlugin plugin) {
        super(plugin, "messages.yml", TEMPLATE);
    }

    // convenience accessors
    public String prefix() {
        return getStringOr("prefix", "§6Lurk §7» §r");
    }

    public String msg(String key, String def) {
        return getStringOr(key, def);
    }
}
