package dev.ascent.anticheat;

import org.bukkit.plugin.java.JavaPlugin;
import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;

import java.util.logging.Logger;

public final class Bootstrap {

    private static final Logger LOG = Logger.getLogger("Minecraft");

    private Bootstrap() {}

    public static void init(JavaPlugin plugin) {
        // Initialize BetaProtocollib for Beta 1.7.3
        BetaLib.injectAll();
        BetaLib.setDefaultVersion(MinecraftVersion.B_1_7_3);
        LOG.info("[Bootstrap] BetaProtocollib initialized for B1.7.3.");
    }
}