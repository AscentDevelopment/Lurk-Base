package dev.ascent.anticheat.process;

import dev.ascent.anticheat.process.processors.DigProcessor;
import dev.ascent.anticheat.process.processors.InteractionProcessor;
import dev.ascent.anticheat.process.processors.InventoryProcessor;
import dev.ascent.anticheat.process.processors.KeepAliveProcessor;
import dev.ascent.anticheat.process.processors.MovementProcessor;
import dev.ascent.anticheat.process.processors.RotationProcessor;
import dev.ascent.anticheat.process.processors.SessionProcessor;
import dev.ascent.anticheat.process.processors.TeleportProcessor;
import dev.ascent.anticheat.process.processors.VelocityProcessor;
import dev.ascent.anticheat.process.processors.WorldEntityProcessor;
import dev.ascent.anticheat.user.User;

import java.util.ArrayList;
import java.util.List;

/** Holds and manages all processors for a single User. */
public class ProcessorManager {

    private final List<Processor> processors = new ArrayList<Processor>();

    // Expose a few commonly-used processors directly:
    private final MovementProcessor movementProcessor;
    private final RotationProcessor rotationProcessor;
    private final VelocityProcessor velocityProcessor;

    public ProcessorManager(User user) {
        // Core
        this.movementProcessor = new MovementProcessor(user);
        this.rotationProcessor = new RotationProcessor(user);
        this.velocityProcessor = new VelocityProcessor(user);

        // Gameplay / interaction
        DigProcessor digProcessor = new DigProcessor(user);
        InteractionProcessor interactionProcessor = new InteractionProcessor(user);
        InventoryProcessor inventoryProcessor = new InventoryProcessor(user);

        // World / networking
        TeleportProcessor teleportProcessor = new TeleportProcessor(user);
        WorldEntityProcessor worldEntityProcessor = new WorldEntityProcessor(user);
        KeepAliveProcessor keepAliveProcessor = new KeepAliveProcessor(user);

        // Session metadata (client brand/proto, session timers, etc.)
        SessionProcessor sessionProcessor = new SessionProcessor(user);

        // Register order can matter for derived fields; keep core first.
        processors.add(movementProcessor);
        processors.add(rotationProcessor);
        processors.add(velocityProcessor);
        processors.add(digProcessor);
        processors.add(interactionProcessor);
        processors.add(inventoryProcessor);
        processors.add(teleportProcessor);
        processors.add(worldEntityProcessor);
        processors.add(keepAliveProcessor);
        processors.add(sessionProcessor);

        for (int i = 0; i < processors.size(); i++) {
            processors.get(i).register();
        }
    }

    public void shutdown() {
        for (int i = processors.size() - 1; i >= 0; i--) {
            try { processors.get(i).unregister(); } catch (Throwable ignored) {}
        }
        processors.clear();
    }

    public List<Processor> getProcessors() { return processors; }

    public MovementProcessor getMovementProcessor() { return movementProcessor; }
    public RotationProcessor getRotationProcessor() { return rotationProcessor; }
    public VelocityProcessor getVelocityProcessor() { return velocityProcessor; }

    /** Convenience: fetch a processor by type if present (used by commands/checks). */
    public <T> T get(Class<T> type) {
        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            if (type.isInstance(p)) return type.cast(p);
        }
        return null;
    }
}
