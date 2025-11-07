package dev.ascent.anticheat.user;

import dev.ascent.anticheat.check.Check;
import dev.ascent.anticheat.process.ProcessorManager;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class User {

    private final Player player;
    private final UUID uuid;
    private final String userName;

    // checks for this user
    private final List<Check> checks = new ArrayList<Check>();

    // processors
    private final ProcessorManager processorManager;

    public User(Player player) {
        this.player = player;
        this.userName = player.getName();
        this.uuid = resolveUuid(player, this.userName);
        this.processorManager = new ProcessorManager(this);
    }

    public Player getPlayer() { return player; }
    public UUID getUuid() { return uuid; }
    public String getUserName() { return userName; }

    public List<Check> getChecks() { return checks; }
    public void addCheck(Check check) { if (check != null) checks.add(check); }

    public ProcessorManager getProcessorManager() { return processorManager; }

    private static UUID resolveUuid(Player player, String name) {
        try {
            Method m = Player.class.getMethod("getUniqueId");
            Object o = m.invoke(player);
            if (o instanceof UUID) return (UUID) o;
        } catch (Throwable ignored) {}
        byte[] bytes = ("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(bytes);
    }
}
