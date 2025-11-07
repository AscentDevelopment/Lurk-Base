package dev.ascent.anticheat.check;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.model.LogEntry;
import dev.ascent.anticheat.protocol.events.Event;
import dev.ascent.anticheat.service.AlertsService;
import dev.ascent.anticheat.service.LogService;
import dev.ascent.anticheat.user.User;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class Check extends Event {

    private User user;
    private CheckData data;
    private double violations;
    private double punishmentVL;
    private String checkName, checkType;
    private boolean enabled;
    private boolean experimental;

    public Check() {
        if (getClass().isAnnotationPresent(CheckData.class)) {
            this.data = getClass().getAnnotation(CheckData.class);
            this.punishmentVL = this.data.punishmentVL();
            this.checkName = this.data.name();
            this.checkType = this.data.type();
            this.enabled = this.data.enabled();
            this.experimental = this.data.experimental();
        } else {
            this.punishmentVL = 20.0;
            this.checkName = getClass().getSimpleName();
            this.checkType = "A";
            this.enabled = true;
            this.experimental = false;
        }
    }

    // ----- getters/setters (no Lombok) -----
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CheckData getData() { return data; }
    public void setData(CheckData data) { this.data = data; }

    public double getViolations() { return violations; }
    public void setViolations(double violations) { this.violations = violations; }

    public double getPunishmentVL() { return punishmentVL; }
    public void setPunishmentVL(double punishmentVL) { this.punishmentVL = punishmentVL; }

    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isExperimental() { return experimental; }
    public void setExperimental(boolean experimental) { this.experimental = experimental; }

    // ----- alert/log -----
    public void fail(String... facts) {
        this.violations += 1.0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < facts.length; i++) {
            sb.append(facts[i]);
            if (i < facts.length - 1) sb.append(", ");
        }
        final String factsStr = sb.toString();

        String type = this.checkType + (this.experimental ? "" : "*");

        final Player target = this.user.getPlayer();
        final String alert =
                ChatColor.GOLD + "Lurk " + ChatColor.DARK_GRAY + "» " +
                ChatColor.WHITE + this.user.getUserName() + ChatColor.GRAY + " failed " +
                ChatColor.RED + this.checkName + ChatColor.RED + " [" +
                ChatColor.RED + type + ChatColor.RED + "] " +
                ChatColor.GRAY + "(" + ChatColor.GRAY + this.violations +
                ChatColor.GRAY + "/" + this.punishmentVL + ") " +
                ChatColor.GRAY + "[" + factsStr + "]";

        // 1) store log
        LogService.add(this.user.getUuid(),
                new LogEntry(System.currentTimeMillis(), this.checkName, type, this.violations, factsStr));

        // 2) staff alerts (works on both Beta arrays and modern collections)
        if (!AlertsService.isGlobalEnabled()) return;

        Server srv = Lurk.getInstance().getServer();
        Object online = srv.getOnlinePlayers(); // runtime-typed

        if (online instanceof Player[]) {
            Player[] arr = (Player[]) online;
            for (int i = 0; i < arr.length; i++) {
                Player p = arr[i];
                if (!shouldReceiveAlert(p, target)) continue;
                p.sendMessage(alert);
            }
        } else if (online instanceof java.util.Collection) {
            java.util.Collection<?> coll = (java.util.Collection<?>) online;
            for (Object o : coll) {
                if (!(o instanceof Player)) continue;
                Player p = (Player) o;
                if (!shouldReceiveAlert(p, target)) continue;
                p.sendMessage(alert);
            }
        }
    }

    private static boolean shouldReceiveAlert(Player p, Player target) {
        if (p == null || p.equals(target)) return false;
        if (!p.isOp() && !p.hasPermission("lurk.alerts")) return false;
        return AlertsService.isEnabled(p.getUniqueId());
    }
}
