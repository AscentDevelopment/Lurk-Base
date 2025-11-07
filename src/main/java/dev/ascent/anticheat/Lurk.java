package dev.ascent.anticheat;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.command.CommandAPI;
import dev.ascent.anticheat.listener.*;
import dev.ascent.anticheat.mitigation.MitigationEngine;
import dev.ascent.anticheat.mitigation.MitigationManager;
import dev.ascent.anticheat.packet.PacketManager;
import dev.ascent.anticheat.protocol.HookInstaller;
import dev.ascent.anticheat.protocol.ProtocolAdapter;
import dev.ascent.anticheat.user.UserManager;
import dev.ascent.anticheat.util.config.CheckConfig;
import dev.ascent.anticheat.util.config.Config;
import dev.ascent.anticheat.util.config.MessageConfig;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main entry for the Lurk Anticheat (Beta 1.7.3 edition).
 * Hooks BetaProtocollib, initializes checks bus, listeners, packet bridge, and mitigations.
 */
public final class Lurk extends JavaPlugin {

    private static final Logger LOG = Logger.getLogger("Minecraft");

    private static Lurk instance;
    private ProtocolAdapter protocolAdapter;
    private HookInstaller hookInstaller;
    private UserManager userManager;
    private PacketManager packetManager;

    // Mitigation system
    private MitigationManager mitigationManager;
    private MitigationEngine mitigationEngine;

    // Configs
    private Config configFile;
    private MessageConfig messageConfig;
    private CheckConfig checkConfig;

    @Override
    public void onEnable() {
        instance = this;
        LOG.info("[Lurk] Initializing...");

        // Bootstrap + event bus
        try {
            Bootstrap.init(this);
        } catch (Throwable t) {
            LOG.warning("[Lurk] Failed to initialize Bootstrap: " + t.getMessage());
        }
        try {
            ChecksBus.init(this, true);
        } catch (Throwable t) {
            LOG.warning("[Lurk] Failed to initialize ChecksBus: " + t.getMessage());
        }

        // Core systems
        this.userManager     = new UserManager();
        this.protocolAdapter = new ProtocolAdapter();
        this.hookInstaller   = new HookInstaller(this, protocolAdapter);
        this.packetManager   = new PacketManager();

        // Mitigations: manager + defaults + engine
        this.mitigationManager = new MitigationManager();
        this.mitigationManager.registerDefaults();
        this.mitigationEngine  = new MitigationEngine(mitigationManager);

        // Load config files (copies defaults from resources on first run)
        this.configFile    = new Config(this);
        this.messageConfig = new MessageConfig(this);
        this.checkConfig   = new CheckConfig(this);

        try { configFile.load();    LOG.info("[Lurk] config.yml loaded."); }    catch (Throwable t) { LOG.warning("[Lurk] Failed to load config.yml: " + t.getMessage()); }
        try { messageConfig.load(); LOG.info("[Lurk] messages.yml loaded."); }  catch (Throwable t) { LOG.warning("[Lurk] Failed to load messages.yml: " + t.getMessage()); }
        try { checkConfig.load();   LOG.info("[Lurk] checks.yml loaded."); }    catch (Throwable t) { LOG.warning("[Lurk] Failed to load checks.yml: " + t.getMessage()); }

        // Install ONLY the rules present & enabled in checks.yml (e.g., Speed.A, BadPackets.A)
        checkConfig.installInto(this.mitigationEngine);

        // Packet routing layer (Beta event bridge)
        try {
            packetManager.start();
        } catch (Throwable t) {
            LOG.warning("[Lurk] Failed to start PacketManager: " + t.getMessage());
        }

        // ---- Legacy listener registration (CraftBukkit 1060 style) ----
        final PluginManager pm = getServer().getPluginManager();

        // Player lifecycle & movement
        UserListener userL     = new UserListener();
        MovementListener moveL = new MovementListener();
        pm.registerEvent(Event.Type.PLAYER_JOIN,  userL, Priority.Lowest,  this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,  userL, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE,  moveL, Priority.Monitor, this);

        // Combat
        CombatListener combatL = new CombatListener();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, combatL, Priority.Monitor, this);

        // Blocks
        MaterialListener matL = new MaterialListener();
        pm.registerEvent(Event.Type.BLOCK_BREAK,  matL, Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,  matL, Priority.Monitor, this);

        // World + explosions
        RealmListener realmL = new RealmListener();
        pm.registerEvent(Event.Type.CHUNK_LOAD,   realmL, Priority.Monitor, this);
        pm.registerEvent(Event.Type.CHUNK_UNLOAD, realmL, Priority.Monitor, this);

        ExplosionListener explodeL = new ExplosionListener();
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, explodeL, Priority.Monitor, this);

        // Commands
        try {
            CommandAPI cmd = new CommandAPI(this);
            if (getCommand("lurk") != null)       getCommand("lurk").setExecutor(cmd);
            if (getCommand("alerts") != null)     getCommand("alerts").setExecutor(cmd);
            if (getCommand("banwave") != null)    getCommand("banwave").setExecutor(cmd);
            if (getCommand("bypass") != null)     getCommand("bypass").setExecutor(cmd);
            if (getCommand("info") != null)       getCommand("info").setExecutor(cmd);
            if (getCommand("logs") != null)       getCommand("logs").setExecutor(cmd);
            if (getCommand("mitigation") != null) getCommand("mitigation").setExecutor(cmd);
            if (getCommand("version") != null)    getCommand("version").setExecutor(cmd);
            if (getCommand("reload") != null)     getCommand("reload").setExecutor(cmd);
            LOG.info("[Lurk] Commands registered (lurk, alerts, banwave, bypass, info, logs, mitigation, version, reload).");
        } catch (Throwable t) {
            LOG.warning("[Lurk] Failed to register commands: " + t.getMessage());
        }

        LOG.info("[Lurk] Enabled successfully.");
    }

    @Override
    public void onDisable() {
        try {
            if (hookInstaller != null) hookInstaller.unregisterAll();
        } catch (Throwable t) {
            LOG.warning("[Lurk] Error unregistering hooks: " + t.getMessage());
        }
        try {
            if (packetManager != null) packetManager.stop();
        } catch (Throwable t) {
            LOG.warning("[Lurk] Error stopping PacketManager: " + t.getMessage());
        }
        instance = null;
        LOG.info("[Lurk] Disabled cleanly.");
    }

    // ---- Getters ----
    public static Lurk getInstance() { return instance; }
    public ProtocolAdapter getProtocolAdapter() { return protocolAdapter; }
    public UserManager getUserManager() { return userManager; }
    public PacketManager getPacketManager() { return packetManager; }
    public MitigationManager getMitigationManager() { return mitigationManager; }
    public MitigationEngine  getMitigationEngine()  { return mitigationEngine; }

    public Config getConfigFile() { return configFile; }
    public MessageConfig getMessageConfig() { return messageConfig; }
    public CheckConfig getCheckConfig() { return checkConfig; }
}
