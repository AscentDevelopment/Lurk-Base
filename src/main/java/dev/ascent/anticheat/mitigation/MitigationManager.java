package dev.ascent.anticheat.mitigation;

import dev.ascent.anticheat.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Registry + per-user lifecycle for mitigations. */
public final class MitigationManager {

    // ---------- Registry (key -> factory) ----------
    private final Map<String, Supplier<Mitigation>> registry =
            new LinkedHashMap<String, Supplier<Mitigation>>();

    // ---------- Active per-user (uuid -> key -> context) ----------
    private final Map<UUID, Map<String, MitigationContext>> active =
            new ConcurrentHashMap<UUID, Map<String, MitigationContext>>();

    // --- Registration API ---
    public void register(Mitigation impl) {
        if (impl == null) return;
        String k = safeKey(impl.key());
        if (k == null) return;
        final Mitigation instance = impl; // keep single instance
        registry.put(k, new Supplier<Mitigation>() {
            public Mitigation get() { return instance; }
        });
    }

    public void register(String key, Supplier<Mitigation> factory) {
        String k = safeKey(key);
        if (k == null || factory == null) return;
        registry.put(k, factory);
    }

    public boolean isRegistered(String key) {
        String k = safeKey(key);
        return k != null && registry.containsKey(k);
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    /** Call once in onEnable() */
    public void registerDefaults() {
		// Combat
		register("angle",              dev.ascent.anticheat.mitigation.impl.AngleMitigation::new);
        register("attack_cooldown",    dev.ascent.anticheat.mitigation.impl.CooldownMitigation::new);
		register("autoblock_cancel",   dev.ascent.anticheat.mitigation.impl.AutoBlockMitigation::new);
		register("cps_cap",            dev.ascent.anticheat.mitigation.impl.CPSMitigation::new);
		register("crit_cancel",        dev.ascent.anticheat.mitigation.impl.CritCancelMitigation::new);
        register("damage",             dev.ascent.anticheat.mitigation.impl.DamageMitigation::new);
		register("debounce",           dev.ascent.anticheat.mitigation.impl.DebounceMitigation::new);
		register("fake_damage",  	   dev.ascent.anticheat.mitigation.impl.FakeDamageMitigation::new);
        register("knockback",          dev.ascent.anticheat.mitigation.impl.KnockbackMitigation::new);
		register("reach",   		   dev.ascent.anticheat.mitigation.impl.ReachMitigation::new);
		register("reversal",     	   dev.ascent.anticheat.mitigation.impl.ReversalMitigation::new);
		// Movement
		register("setback",            dev.ascent.anticheat.mitigation.impl.SetbackMitigation::new);
		register("slowdown",           dev.ascent.anticheat.mitigation.impl.SlowdownMitigation::new);
		// Blocks & World
        register("block",              dev.ascent.anticheat.mitigation.impl.BlockMitigation::new);
		register("cancel_interact",    dev.ascent.anticheat.mitigation.impl.InteractMitigation::new);
        register("inventory_lock",     dev.ascent.anticheat.mitigation.impl.InventoryMitigation::new);
		// Misc
        register("run_command",        dev.ascent.anticheat.mitigation.impl.CommandMitigation::new);
		register("potion",       	   dev.ascent.anticheat.mitigation.impl.PotionMitigation::new);
    }

    // --- Apply / Remove / Query ---
    public boolean apply(User user, String key, MitigationContext ctx) {
        if (user == null) return false;
        String k = safeKey(key);
        if (k == null) return false;

        Supplier<Mitigation> sup = registry.get(k);
        if (sup == null) return false;

        Map<String, MitigationContext> map = active.get(user.getUuid());
        if (map == null) {
            map = new ConcurrentHashMap<String, MitigationContext>();
            active.put(user.getUuid(), map);
        }
        map.put(k, ctx);

        try {
            Mitigation impl = sup.get();
            if (impl != null) impl.onApply(user, ctx);
        } catch (Throwable ignored) {}
        return true;
    }

    public boolean remove(User user, String key) {
        if (user == null) return false;
        String k = safeKey(key);
        if (k == null) return false;

        Map<String, MitigationContext> map = active.get(user.getUuid());
        if (map == null) return false;

        MitigationContext ctx = map.remove(k);
        if (ctx == null) return false;

        Supplier<Mitigation> sup = registry.get(k);
        if (sup != null) {
            try {
                Mitigation impl = sup.get();
                if (impl != null) impl.onRemove(user, ctx);
            } catch (Throwable ignored) {}
        }
        if (map.isEmpty()) active.remove(user.getUuid());
        return true;
    }

    /** Remove ALL active mitigations for a user and invoke onRemove for each. */
    public void clearAll(User user) {
        if (user == null) return;
        Map<String, MitigationContext> map = active.remove(user.getUuid());
        if (map == null || map.isEmpty()) return;

        for (Map.Entry<String, MitigationContext> e : map.entrySet()) {
            String key = e.getKey();
            MitigationContext ctx = e.getValue();
            Supplier<Mitigation> sup = registry.get(key);
            if (sup != null) {
                try {
                    Mitigation impl = sup.get();
                    if (impl != null) impl.onRemove(user, ctx);
                } catch (Throwable ignored) {}
            }
        }
    }

    /** Returns context if active and not expired; null otherwise (auto-cleans expired). */
    public MitigationContext get(User user, String key) {
        if (user == null) return null;
        String k = safeKey(key);
        if (k == null) return null;

        Map<String, MitigationContext> map = active.get(user.getUuid());
        if (map == null) return null;

        MitigationContext ctx = map.get(k);
        if (ctx == null) return null;

        if (ctx.isExpired(System.currentTimeMillis())) {
            map.remove(k);
            if (map.isEmpty()) active.remove(user.getUuid());
            return null;
        }
        return ctx;
    }

    /** For UI/debug. */
    public List<String> listActive(User user) {
        Map<String, MitigationContext> map = active.get(user.getUuid());
        if (map == null || map.isEmpty()) return Collections.emptyList();
        long now = System.currentTimeMillis();
        ArrayList<String> out = new ArrayList<String>();
        for (Map.Entry<String, MitigationContext> e : map.entrySet()) {
            MitigationContext c = e.getValue();
            if (c != null && !c.isExpired(now)) out.add(e.getKey());
        }
        return out;
    }

    /** Periodic cleanup (optional). Call from a scheduler if you want. */
    public void reapExpired(long nowMs) {
        for (Iterator<Map.Entry<UUID, Map<String, MitigationContext>>> it = active.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, Map<String, MitigationContext>> en = it.next();
            Map<String, MitigationContext> map = en.getValue();
            for (Iterator<Map.Entry<String, MitigationContext>> it2 = map.entrySet().iterator(); it2.hasNext();) {
                Map.Entry<String, MitigationContext> en2 = it2.next();
                if (en2.getValue() != null && en2.getValue().isExpired(nowMs)) it2.remove();
            }
            if (map.isEmpty()) it.remove();
        }
    }

    // --- helpers ---
    private static String safeKey(String k) {
        if (k == null) return null;
        k = k.trim();
        if (k.length() == 0) return null;
        return k.toLowerCase(Locale.ROOT);
    }
}
