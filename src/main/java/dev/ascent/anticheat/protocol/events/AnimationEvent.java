package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class AnimationEvent extends Event {
    private final Player player;
    private final int animationId;
    private final Object rawData;

    public AnimationEvent(Player player, int animationId, Object rawData) {
        this.player = player; this.animationId = animationId; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getAnimationId() { return animationId; }
    public Object getRawData() { return rawData; }
}