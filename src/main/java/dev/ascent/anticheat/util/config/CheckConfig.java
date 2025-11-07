package dev.ascent.anticheat.util.config;

import dev.ascent.anticheat.mitigation.MitigationEngine;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;

/** checks.yml */
public final class CheckConfig extends BaseConfig {

    private static final String TEMPLATE =
            "# Lurk (Beta 1.7.3) - checks.yml\n" +
            "checks:\n" +
            "  Speed.A:\n" +
            "    enabled: true\n" +
            "    trigger_vl: 5\n" +
            "    every_vl: 3\n" +
            "    duration_ms: 10000\n" +
            "    mitigation: setback\n" +
            "    params: {}\n" +
            "\n" +
            "  BadPackets.A:\n" +
            "    enabled: true\n" +
            "    trigger_vl: 3\n" +
            "    every_vl: 2\n" +
            "    duration_ms: 15000\n" +
            "    mitigation: block\n" +
            "    params:\n" +
            "      deny_place: \"true\"\n" +
            "      deny_break: \"true\"\n";

    public CheckConfig(JavaPlugin plugin) {
        super(plugin, "checks.yml", TEMPLATE);
    }

    // ---- Typed readers ----
    public boolean isEnabled(String checkKey) {
        return getBooleanOr(path(checkKey, "enabled"), true);
    }

    public int triggerVl(String checkKey, int def) {
        return getIntOr(path(checkKey, "trigger_vl"), def);
    }

    public int everyVl(String checkKey, int def) {
        return getIntOr(path(checkKey, "every_vl"), def);
    }

    public long durationMs(String checkKey, long def) {
        return (long) getDoubleOr(path(checkKey, "duration_ms"), (double) def);
    }

    public String mitigationKey(String checkKey, String def) {
        return getStringOr(path(checkKey, "mitigation"), def);
    }

    /** Flat map of params (stringified). */
    public Map<String, String> params(String checkKey) {
        try {
            Object raw = raw().getProperty(path(checkKey, "params"));
            if (raw instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) raw;
                Map<String, String> out = new HashMap<String, String>();
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        out.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
                    }
                }
                return out;
            }
        } catch (Throwable ignored) {}
        return new HashMap<String, String>();
    }

    /** Return all configured check ids, e.g. ["Speed.A", "BadPackets.A"] */
    public Set<String> allChecks() {
        try {
            // Old Bukkit Configuration returns a List<String> here
            List<String> keys = raw().getKeys("checks");
            if (keys == null || keys.isEmpty()) {
                return java.util.Collections.<String>emptySet();
            }
            return new LinkedHashSet<String>(keys);
        } catch (Throwable t) {
            return java.util.Collections.<String>emptySet();
        }
    }

    /** Install enabled rules into the given engine. Call from onEnable() after loading. */
    public void installInto(MitigationEngine engine) {
        if (engine == null) return;
        for (String id : allChecks()) {
            if (!isEnabled(id)) continue;

            MitigationEngine.Rule r = new MitigationEngine.Rule();
            r.mitigationKey = mitigationKey(id, null);
            if (r.mitigationKey == null || r.mitigationKey.length() == 0) continue;

            r.triggerVl  = triggerVl(id, 1);
            r.everyVl    = everyVl(id, 0);
            r.durationMs = durationMs(id, 0L);
            r.params     = params(id);

            engine.putRule(id, r);
        }
    }

    private static String path(String checkKey, String leaf) {
        return "checks." + checkKey + "." + leaf;
    }
}
