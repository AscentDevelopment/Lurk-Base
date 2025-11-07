package dev.ikara.anticheat.protocol;

/** Marker for classes that will consume adapter events via ChecksBus. */
public interface AdapterListener {
    void register();
    void unregister();
}