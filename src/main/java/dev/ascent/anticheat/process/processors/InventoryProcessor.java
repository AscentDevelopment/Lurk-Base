package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.HeldItemChangeEvent;
import dev.ascent.anticheat.protocol.events.WindowClickEvent;
import dev.ascent.anticheat.protocol.events.CloseWindowEvent;
import dev.ascent.anticheat.protocol.events.SetSlotEvent;
import dev.ascent.anticheat.protocol.events.WindowItemsEvent;
import dev.ascent.anticheat.user.User;

import java.lang.reflect.Method;

@ProcessorInfo(name = "Inventory")
public final class InventoryProcessor extends Processor {

    private ListenerHandle h1, h2, h3, h4, h5;

    private int lastHotbarSlot = -1;
    private long lastHotbarSwapMs;

    private int lastClickedSlot = -1;
    private int lastClickedButton = -1;
    private long lastClickMs;

    private long lastCloseMs;
    private long lastSetSlotMs;
    private long lastWindowItemsMs;

    public InventoryProcessor(User user) { super(user); }

    @Override
    public void register() {
        h1 = ChecksBus.on(HeldItemChangeEvent.class, e -> {
            lastHotbarSlot = resolveInt(e, "getNewSlot", "getSlot");
            lastHotbarSwapMs = System.currentTimeMillis();
        });
        h2 = ChecksBus.on(WindowClickEvent.class, e -> {
            lastClickedSlot = resolveInt(e, "getSlot");
            lastClickedButton = resolveInt(e, "getButton", "getMode");
            lastClickMs = System.currentTimeMillis();
        });
        h3 = ChecksBus.on(CloseWindowEvent.class, e -> lastCloseMs = System.currentTimeMillis());
        h4 = ChecksBus.on(SetSlotEvent.class, e -> lastSetSlotMs = System.currentTimeMillis());
        h5 = ChecksBus.on(WindowItemsEvent.class, e -> lastWindowItemsMs = System.currentTimeMillis());
    }

    @Override
    public void unregister() {
        if (h1 != null) h1.unsubscribe();
        if (h2 != null) h2.unsubscribe();
        if (h3 != null) h3.unsubscribe();
        if (h4 != null) h4.unsubscribe();
        if (h5 != null) h5.unsubscribe();
        h1 = h2 = h3 = h4 = h5 = null;
    }

    private static int resolveInt(Object obj, String... methods) {
        for (String m : methods) {
            try {
                Method method = obj.getClass().getMethod(m);
                Object val = method.invoke(obj);
                if (val instanceof Number) return ((Number) val).intValue();
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    public int getLastHotbarSlot() { return lastHotbarSlot; }
    public long getLastHotbarSwapMs() { return lastHotbarSwapMs; }
    public int getLastClickedSlot() { return lastClickedSlot; }
    public int getLastClickedButton() { return lastClickedButton; }
    public long getLastClickMs() { return lastClickMs; }
    public long getLastCloseMs() { return lastCloseMs; }
    public long getLastSetSlotMs() { return lastSetSlotMs; }
    public long getLastWindowItemsMs() { return lastWindowItemsMs; }
}