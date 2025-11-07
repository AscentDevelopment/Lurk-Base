package dev.ascent.anticheat.util.config;

import org.bukkit.plugin.java.JavaPlugin;

/** config.yml */
public final class Config extends BaseConfig {

    private static final String TEMPLATE =
            "# Lurk (Beta 1.7.3) - config.yml\n" +
            "options:\n" +
            "  debug: false\n" +
            "  alerts: true\n" +
            "  mitigations_enabled: true\n" +
            "\n" +
            "mitigation_defaults:\n" +
            "  # Example of global defaults a mitigation might read if not set by checks.yml\n" +
            "  setback:\n" +
            "    cooldown_ms: 300\n" +
            "  damage:\n" +
            "    scale: 1.0   # 1.0 = no change; <1 reduce; >1 increase\n";

    public Config(JavaPlugin plugin) {
        super(plugin, "config.yml", TEMPLATE);
    }

    // convenience accessors
    public boolean isDebug() {
        return getBooleanOr("options.debug", false);
    }

    public boolean alertsEnabled() {
        return getBooleanOr("options.alerts", true);
    }

    public boolean mitigationsEnabled() {
        return getBooleanOr("options.mitigations_enabled", true);
    }
}
