package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.bus.ChecksBus;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

/** Forwards explosions to the bus for physics/velocity/grace checks. */
public final class ExplosionListener extends EntityListener {

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        ChecksBus.post(event);
    }
}