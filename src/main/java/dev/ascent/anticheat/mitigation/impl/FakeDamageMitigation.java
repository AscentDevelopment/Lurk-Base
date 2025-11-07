package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@MitigationInfo(key = "fake_damage", desc = "Show hit feedback without applying real damage")
public final class FakeDamageMitigation implements Mitigation {
    public String key() { return "fake_damage"; }

    public void onApply(User user, MitigationContext ctx) { /* one-shot in listener */ }
    public void onRemove(User user, MitigationContext ctx) { }

    public static void fakeHit(Player attacker, Player victim, MitigationContext ctx) {
        boolean doSwing = parseBool(ctx.param("swing", "true"), true);
        boolean doHurt  = parseBool(ctx.param("hurt",  "true"), true);
        boolean doSound = parseBool(ctx.param("sound", "true"), true);

        Object adapter = null;
        try { adapter = Lurk.getInstance().getProtocolAdapter(); } catch (Throwable ignored) {}

        if (doSwing && attacker != null && adapter != null) tryCall(adapter, "sendArmSwing", attacker);
        if (doHurt  && victim   != null && adapter != null) tryCall(adapter, "sendHurtAnimation", victim);
        if (doSound && victim   != null && adapter != null) tryCall(adapter, "playHitSound", victim);

        // Fallbacks (best-effort, harmless if not present)
        if (doSwing && attacker != null && adapter == null) {
            try { attacker.getClass().getMethod("swingArm").invoke(attacker); } catch (Throwable ignored) {}
        }
        // Many Beta builds lack a clean way to trigger red tint; we skip if not supported.
    }

    private static void tryCall(Object adapter, String method, Object arg) {
        try {
            Method m = null;
            for (Method cand : adapter.getClass().getMethods()) {
                if (!cand.getName().equalsIgnoreCase(method)) continue;
                Class<?>[] p = cand.getParameterTypes();
                if (p.length == 1 && p[0].isInstance(arg)) { m = cand; break; }
            }
            if (m != null) m.invoke(adapter, arg);
        } catch (Throwable ignored) {}
    }

    private static boolean parseBool(String s, boolean def) {
        if (s == null) return def;
        if ("true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "1".equals(s)) return true;
        if ("false".equalsIgnoreCase(s) || "no".equalsIgnoreCase(s) || "0".equals(s)) return false;
        return def;
    }
}
