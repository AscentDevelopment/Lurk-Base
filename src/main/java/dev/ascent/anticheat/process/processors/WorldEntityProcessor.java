package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.DestroyEntityEvent;
import dev.ascent.anticheat.protocol.events.SpawnMobEvent;
import dev.ascent.anticheat.protocol.events.SpawnPlayerEvent;
import dev.ascent.anticheat.user.User;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ProcessorInfo(name = "WorldEntities")
public final class WorldEntityProcessor extends Processor {

    private ListenerHandle h1, h2, h3;

    public static final class EntityInfo {
        public final int id;
        public final String type;
        public final long seenMs;

        public EntityInfo(int id, String type) {
            this.id = id; this.type = type; this.seenMs = System.currentTimeMillis();
        }
    }

    private final Map<Integer, EntityInfo> entities = new ConcurrentHashMap<Integer, EntityInfo>();

    public WorldEntityProcessor(User user) { super(user); }

    @Override
    public void register() {
        h1 = ChecksBus.on(SpawnPlayerEvent.class, e ->
                entities.put(e.getEntityId(), new EntityInfo(e.getEntityId(), "PLAYER")));

        h2 = ChecksBus.on(SpawnMobEvent.class, e ->
                entities.put(e.getEntityId(), new EntityInfo(e.getEntityId(), String.valueOf(e.getMobType()))));

        h3 = ChecksBus.on(DestroyEntityEvent.class, e -> {
            int id = resolveEntityId(e);
            if (id != Integer.MIN_VALUE) entities.remove(id);
        });
    }

    @Override
    public void unregister() {
        if (h1 != null) h1.unsubscribe();
        if (h2 != null) h2.unsubscribe();
        if (h3 != null) h3.unsubscribe();
        h1 = h2 = h3 = null;
        entities.clear();
    }

    public Map<Integer, EntityInfo> getEntities() { return entities; }

    // Try getEntityId(), then getId(), then field 'entityId' or 'id'
    private static int resolveEntityId(DestroyEntityEvent e) {
        try {
            Method m = e.getClass().getMethod("getEntityId");
            Object o = m.invoke(e);
            if (o instanceof Number) return ((Number) o).intValue();
        } catch (Throwable ignored) {}
        try {
            Method m = e.getClass().getMethod("getId");
            Object o = m.invoke(e);
            if (o instanceof Number) return ((Number) o).intValue();
        } catch (Throwable ignored) {}
        try {
            Object v = e.getClass().getField("entityId").get(e);
            if (v instanceof Number) return ((Number) v).intValue();
        } catch (Throwable ignored) {}
        try {
            Object v = e.getClass().getField("id").get(e);
            if (v instanceof Number) return ((Number) v).intValue();
        } catch (Throwable ignored) {}
        return Integer.MIN_VALUE;
    }
}