package dev.ascent.anticheat.user;

import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal user registry compatible with current build.
 */
public final class UserManager {

    private final Map<UUID, User> userMap = new ConcurrentHashMap<UUID, User>();

    /** Create/register a User for this player. Idempotent on UUID. */
    public User addUser(Player player) {
        UUID id = resolveUuid(player);
        User existing = userMap.get(id);
        if (existing != null) return existing;

        User user = new User(player);
        userMap.put(user.getUuid(), user);
        return user;
    }

    /** Remove a User for this player, if present. */
    public void removeUser(Player player) {
        userMap.remove(resolveUuid(player));
    }

    public User getUser(Player player) {
        return userMap.get(resolveUuid(player));
    }

    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    public int size() { return userMap.size(); }

    public Iterable<User> allUsers() { return userMap.values(); }

    // Same offline-safe UUID derivation used in User
    private static UUID resolveUuid(Player player) {
        try {
            Object o = Player.class.getMethod("getUniqueId").invoke(player);
            if (o instanceof UUID) return (UUID) o;
        } catch (Throwable ignored) {}
        byte[] bytes = ("OfflinePlayer:" + player.getName()).getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(bytes);
    }
}