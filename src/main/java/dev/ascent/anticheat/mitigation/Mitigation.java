package dev.ascent.anticheat.mitigation;

import dev.ascent.anticheat.user.User;

public interface Mitigation {
    /** Unique key for this mitigation (e.g. "setback", "damage_reduction"). */
    String key();

    /** Called when applied to a user. */
    void onApply(User user, MitigationContext ctx);

    /** Called when removed from a user (expiry or manual). */
    void onRemove(User user, MitigationContext ctx);
}
