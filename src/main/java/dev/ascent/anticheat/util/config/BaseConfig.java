package dev.ascent.anticheat.util.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

/**
 * Beta 1.7.3-friendly base config file wrapper using org.bukkit.util.config.Configuration.
 * - Ensures parent dirs exist
 * - If no file/resource, writes an embedded template (if provided)
 * - Exposes safe getters with defaults
 */
public abstract class BaseConfig {
    protected final JavaPlugin plugin;
    protected final String fileName;
    protected final File file;
    protected final ConfigUtil cfg;
    private final String embeddedTemplate; // nullable

    protected BaseConfig(JavaPlugin plugin, String fileName, String embeddedTemplate) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        this.cfg = new ConfigUtil(plugin, fileName);
        this.embeddedTemplate = embeddedTemplate;
    }

    /** Create file/folders, copy resource if present, else write embedded template if provided. Then load. */
    public void load() {
        // Ensure folders
        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException ignored) {}

        boolean freshlyCreated = false;
        if (!file.exists()) {
            // Try copy default from jar via ConfigUtil logic:
            // ConfigUtil.copyDefaultConfig() is called from cfg.load()
            // But if resource is absent, we want a fallback template.
            freshlyCreated = true;
        }

        // First attempt: let ConfigUtil copy from resources if available
        cfg.load();

        // If file still doesn't exist (no resource), and we have an embedded template, write it then re-load
        if (freshlyCreated && !file.exists() && embeddedTemplate != null && embeddedTemplate.length() > 0) {
            try {
                Files.write(file.toPath(), embeddedTemplate.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {}
            cfg.load();
        }
    }

    public void save() {
        try {
            cfg.save();
        } catch (Exception ignored) {}
    }

    public Configuration raw() {
        return cfg;
    }

    // ---------- Safe getters with defaults ----------
    public String getStringOr(String path, String def) {
        try {
            String v = cfg.getString(path, def);
            return (v != null ? v : def);
        } catch (Throwable t) { return def; }
    }

    public int getIntOr(String path, int def) {
        try { return cfg.getInt(path, def); } catch (Throwable t) { return def; }
    }

    public double getDoubleOr(String path, double def) {
        try { return cfg.getDouble(path, def); } catch (Throwable t) { return def; }
    }

    public boolean getBooleanOr(String path, boolean def) {
        try { return cfg.getBoolean(path, def); } catch (Throwable t) { return def; }
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringListOr(String path, List<String> def) {
        try {
            Object list = cfg.getList(path);
            if (list instanceof List) return (List<String>) list;
            return def;
        } catch (Throwable t) { return def; }
    }

    public List<String> getStringListOrEmpty(String path) {
        return getStringListOr(path, Collections.<String>emptyList());
    }
}
