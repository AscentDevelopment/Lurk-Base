package dev.ascent.anticheat.process;

import dev.ascent.anticheat.user.User;

/** Base processor: resolves name from @ProcessorInfo and stores user reference. */
public class Processor {

    private final String name;
    private final User data;

    public Processor(final User data) {
        ProcessorInfo info = this.getClass().getAnnotation(ProcessorInfo.class);
        this.name = info != null ? info.name() : this.getClass().getSimpleName();
        this.data = data;
    }

    public String getName() { return name; }
    public User getData() { return data; }

    /** Optional lifecycle hooks. */
    public void register() {}
    public void unregister() {}
}