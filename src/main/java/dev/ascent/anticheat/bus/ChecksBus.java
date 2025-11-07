package dev.ascent.anticheat.bus;

import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Simple, type-safe event bus.
 *
 * Usage:
 *   ChecksBus.init(plugin, true); // true = dispatch on main thread (safe for Bukkit API)
 *   ListenerHandle h = ChecksBus.on(PositionUpdateEvent.class, e -> { ... });
 *   ChecksBus.post(new PositionUpdateEvent(...));
 *   h.unsubscribe(); // optional
 */
public final class ChecksBus {

    public interface ListenerHandle {
        void unsubscribe();
    }

    // FIX: removed the extra '>' here
    private static final Map<Class<?>, CopyOnWriteArrayList<Consumer<?>>> LISTENERS = new ConcurrentHashMap<>();
    private static volatile Plugin PLUGIN = null;
    private static volatile boolean DISPATCH_SYNC = true;

    private ChecksBus() {}

    /** Initialize once during onEnable. If plugin is null, dispatch runs on caller thread. */
    public static void init(Plugin plugin, boolean dispatchOnMainThread) {
        PLUGIN = plugin;
        DISPATCH_SYNC = dispatchOnMainThread;
    }

    /** Register a listener for one event class. Returns a handle to remove it later. */
    public static <T> ListenerHandle on(Class<T> eventType, Consumer<T> listener) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(listener, "listener");
        LISTENERS.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<Consumer<?>>())
                 .add(listener);
        return new ListenerHandle() {
            @Override public void unsubscribe() {
                List<Consumer<?>> list = LISTENERS.get(eventType);
                if (list != null) list.remove(listener);
            }
        };
    }

    /** Unregister all listeners for a given event type (rarely needed). */
    public static void clear(Class<?> eventType) {
        LISTENERS.remove(eventType);
    }

    /** Post an event to all matching listeners (exact type + superclasses + interfaces). */
    public static void post(Object event) {
        if (event == null) return;
        Runnable task = new Runnable() {
            @Override public void run() { dispatchNow(event); }
        };

        // Beta 1.7.3 CraftBukkit does not have Bukkit.getScheduler()
        if (DISPATCH_SYNC && PLUGIN != null) {
            try {
                PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, task);
            } catch (Throwable t) {
                // fallback: run inline if scheduler missing
                task.run();
            }
        } else {
            task.run();
        }
    }

    // ---- internal ----

    @SuppressWarnings("unchecked")
    private static void dispatchNow(Object event) {
        // Notify listeners for the concrete class…
        notifyList(event.getClass(), event);

        // …then for each superclass/interface up to Object.
        for (Class<?> type : superTypes(event.getClass())) {
            notifyList(type, event);
        }
    }

    @SuppressWarnings("unchecked")
    private static void notifyList(Class<?> type, Object event) {
        List<Consumer<?>> list = LISTENERS.get(type);
        if (list == null || list.isEmpty()) return;
        for (Consumer<?> raw : list) {
            try {
                ((Consumer<Object>) raw).accept(event);
            } catch (Throwable ignored) {
                // Intentionally swallow; add logging here if you want.
            }
        }
    }

    private static List<Class<?>> superTypes(Class<?> c) {
        List<Class<?>> out = new ArrayList<Class<?>>(8);
        // Superclasses
        Class<?> cur = c.getSuperclass();
        while (cur != null && cur != Object.class) {
            out.add(cur);
            cur = cur.getSuperclass();
        }
        // Interfaces (recursive)
        collectInterfaces(c, out, new HashSet<Class<?>>(4));
        return out;
    }

    private static void collectInterfaces(Class<?> c, List<Class<?>> out, Set<Class<?>> seen) {
        Class<?>[] itfs = c.getInterfaces();
        for (Class<?> itf : itfs) {
            if (seen.add(itf)) {
                out.add(itf);
                collectInterfaces(itf, out, seen);
            }
        }
        Class<?> s = c.getSuperclass();
        if (s != null && s != Object.class) collectInterfaces(s, out, seen);
    }
}