package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.mitigation.MitigationContext;
import dev.ascent.anticheat.mitigation.MitigationManager;
import dev.ascent.anticheat.mitigation.impl.DamageMitigation;
import dev.ascent.anticheat.mitigation.impl.FakeDamageMitigation;
import dev.ascent.anticheat.mitigation.impl.ReversalMitigation;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.user.UserManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/** Handles combat interactions (damage and attack events). */
public final class CombatListener extends EntityListener {

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event;

        Entity damagerE = dmg.getDamager();
        Entity victimE  = dmg.getEntity();
        if (!(damagerE instanceof Player)) return;

        Player attacker = (Player) damagerE;
        Player victim   = (victimE instanceof Player) ? (Player) victimE : null;

        final Lurk plugin = Lurk.getInstance();
        final MitigationManager mm = plugin.getMitigationManager();
        final UserManager um = plugin.getUserManager();
        final long now = System.currentTimeMillis();

        User attackerUser = um.getUser(attacker);
        User victimUser   = (victim != null) ? um.getUser(victim) : null;

        // 1) Reversal (reflect damage/KB back to attacker)
        if (attackerUser != null) {
            MitigationContext rev = mm.get(attackerUser, "reversal");
            if (rev != null && !rev.isExpired(now)) {
                ReversalMitigation.apply(dmg, attacker, victim, rev);
                return; // original event consumed
            }
        }

        // 2) FakeDamage (cancel real damage but show feedback)
        if (attackerUser != null) {
            MitigationContext fake = mm.get(attackerUser, "fake_damage");
            if (fake != null && !fake.isExpired(now)) {
                event.setCancelled(true);
                FakeDamageMitigation.fakeHit(attacker, victim, fake);
                return;
            }
        }

        // 3) DamageMitigation (scale damage) — OUTGOING (attacker)
        if (attackerUser != null) {
            MitigationContext m = mm.get(attackerUser, "damage");
            if (m != null && !m.isExpired(now)) {
                double mul = DamageMitigation.computeScale(m, 0.0, 0); // use 0,0 if no VL context
                int scaled = (int) Math.max(0, Math.round(dmg.getDamage() * mul));
                dmg.setDamage(scaled); // Beta API expects int
            }
        }

        // 3b) DamageMitigation — INCOMING (victim)
        if (victimUser != null) {
            MitigationContext vin = mm.get(victimUser, "damage");
            if (vin != null && !vin.isExpired(now)) {
                double mul = DamageMitigation.computeScale(vin, 0.0, 0);
                int scaled = (int) Math.max(0, Math.round(dmg.getDamage() * mul));
                dmg.setDamage(scaled); // Beta API expects int
            }
        }

        // If you later tweak KB, do it here (after damage) for Beta.
    }
}
