package dev.ascent.anticheat.mitigation.impl;

import dev.ascent.anticheat.mitigation.Mitigation;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationInfo;
import dev.ascent.anticheat.user.User;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@MitigationInfo(key = "potion", desc = "Downscale/strip/clamp certain potion effects (modern only)")
public final class PotionMitigation implements Mitigation {
    public String key() { return "potion"; }

    public void onApply(User user, MitigationContext ctx) {
        // NO-OP here; you typically enforce in a periodic task or on join/attack hooks.
        // For Beta safety, we reflectively attempt to touch PotionEffect API; if missing, skip.
        applyNow(user, ctx);
    }

    public void onRemove(User user, MitigationContext ctx) { /* no-op */ }

    public static void applyNow(User user, MitigationContext ctx) {
        Player p = (user != null) ? user.getPlayer() : null;
        if (p == null) return;

        try {
            Class<?> PotionEffectType = Class.forName("org.bukkit.potion.PotionEffectType");
            Class<?> PotionEffect = Class.forName("org.bukkit.potion.PotionEffect");

            String mode = val(ctx, "mode", "scale");
            double factor = parseDouble(val(ctx, "factor", "0.5"), 0.5);
            int maxAmp = parseInt(val(ctx, "max_amp", "1"), 1);

            Set<String> allow = parseSet(val(ctx, "allow", ""));
            Set<String> deny  = parseSet(val(ctx, "deny",  ""));
            Set<String> only  = parseSet(val(ctx, "effects", ""));

            // p.getActivePotionEffects() : Collection<PotionEffect>
            Method getActive = Player.class.getMethod("getActivePotionEffects");
            Iterable<?> effects = (Iterable<?>) getActive.invoke(p);
            if (effects == null) return;

            for (Object eff : effects) {
                // eff.getType().getName(), eff.getAmplifier(), eff.getDuration()
                Method getType = PotionEffect.getMethod("getType");
                Method getAmplifier = PotionEffect.getMethod("getAmplifier");
                Method getDuration = PotionEffect.getMethod("getDuration");
                Object type = getType.invoke(eff);
                String name = (String) PotionEffectType.getMethod("getName").invoke(type);
                String key = name == null ? "" : name.toUpperCase(Locale.ROOT);

                if (!only.isEmpty() && !only.contains(key)) continue;
                if (!allow.isEmpty() && !allow.contains(key)) continue;
                if (!deny.isEmpty()  &&  deny.contains(key))  continue;

                int amp = ((Integer) getAmplifier.invoke(eff)).intValue();
                int dur = ((Integer) getDuration.invoke(eff)).intValue();

                if ("strip".equalsIgnoreCase(mode)) {
                    // p.removePotionEffect(type)
                    Method remove = Player.class.getMethod("removePotionEffect", PotionEffectType);
                    remove.invoke(p, type);
                    continue;
                }

                if ("clamp".equalsIgnoreCase(mode)) {
                    int newAmp = Math.min(amp, maxAmp);
                    if (newAmp != amp) {
                        // reapply with new amplifier
                        Object newEffect = PotionEffect
                                .getConstructor(PotionEffectType, int.class, int.class, boolean.class, boolean.class, boolean.class)
                                .newInstance(type, dur, newAmp, true, true, true);
                        // p.addPotionEffect(newEffect, true)
                        Player.class.getMethod("addPotionEffect", PotionEffect, boolean.class)
                                .invoke(p, newEffect, Boolean.TRUE);
                    }
                    continue;
                }

                // scale mode
                int newAmp = (int) Math.floor(amp * factor);
                if (newAmp != amp) {
                    Object newEffect = PotionEffect
                            .getConstructor(PotionEffectType, int.class, int.class, boolean.class, boolean.class, boolean.class)
                            .newInstance(type, dur, newAmp, true, true, true);
                    Player.class.getMethod("addPotionEffect", PotionEffect, boolean.class)
                            .invoke(p, newEffect, Boolean.TRUE);
                }
            }
        } catch (Throwable ignored) {
            // Beta: silently no-op
        }
    }

    private static String val(MitigationContext c, String k, String d) { String v = c.param(k, null); return v != null ? v : d; }
    private static Set<String> parseSet(String s) {
        Set<String> out = new HashSet<String>(); if (s == null || s.length() == 0) return out;
        String[] parts = s.split(",");
        for (int i = 0; i < parts.length; i++) out.add(parts[i].trim().toUpperCase(Locale.ROOT));
        return out;
    }
    private static double parseDouble(String s, double def) { try { return Double.parseDouble(s); } catch (Throwable t) { return def; } }
    private static int parseInt(String s, int def) { try { return Integer.parseInt(s); } catch (Throwable t) { return def; } }
}
