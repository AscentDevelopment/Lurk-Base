package dev.ascent.anticheat.check;

import dev.ascent.anticheat.check.impl.movement.speed.SpeedA;
import dev.ascent.anticheat.check.impl.other.badpackets.BadPacketsA;
import dev.ascent.anticheat.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CheckManager {

    private final List<Class<? extends Check>> registry =
            new ArrayList<Class<? extends Check>>(Arrays.<Class<? extends Check>>asList(
                    SpeedA.class,
                    BadPacketsA.class
            ));

    public void loadChecks() { /* reserved for future config-driven registry */ }

    public void loadToPlayer(User user) {
        for (int i = 0; i < registry.size(); i++) {
            Class<? extends Check> cls = registry.get(i);
            try {
                Check check = cls.newInstance(); // Beta-safe (no-arg)
                check.setUser(user);
                user.addCheck(check); // ensure User has addCheck(Check) helper
            } catch (Throwable ignored) { }
        }
    }
}
