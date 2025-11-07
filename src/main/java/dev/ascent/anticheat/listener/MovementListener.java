package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.bus.ChecksBus;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/** Forwards movement events to the anticheat bus. */
public final class MovementListener extends PlayerListener {

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        ChecksBus.post(event);
    }
}