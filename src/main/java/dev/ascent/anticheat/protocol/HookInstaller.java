package dev.ascent.anticheat.protocol;

import org.bukkit.plugin.Plugin;

/**
 * No-op stub for Beta 1.7.3 compile compatibility.
 * If you later want per-player channel hooks, wire them here.
 */
public final class HookInstaller {
    private final Plugin plugin;
    private final ProtocolAdapter adapter;

    public HookInstaller(Plugin plugin, ProtocolAdapter adapter) {
        this.plugin  = plugin;
        this.adapter = adapter;
    }

    public void unregisterAll() {
        // nothing yet
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ProtocolAdapter getAdapter() {
        return adapter;
    }
}