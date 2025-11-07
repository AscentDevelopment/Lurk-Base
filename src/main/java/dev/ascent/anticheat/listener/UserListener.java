package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.Lurk;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/** Handles user join/quit and user map management. */
public final class UserListener extends PlayerListener {

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Lurk.getInstance().getUserManager().addUser(p);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Lurk.getInstance().getUserManager().removeUser(p);
    }
}