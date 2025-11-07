package dev.ascent.anticheat.service;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.model.BanwaveEntry;
import dev.ascent.anticheat.util.ConsoleMsgPatch;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;

public final class BanwaveService {

    private static final Map<UUID, BanwaveEntry> QUEUE = new LinkedHashMap<UUID, BanwaveEntry>();
    private static String banCommandTemplate = "ban %player% %reason%";

    private BanwaveService() {}

    public static boolean add(UUID uuid, String name, String reason, String addedBy, int evidenceCount) {
        if (uuid == null) return false;
        if (name == null) name = uuid.toString();
        if (reason == null || reason.trim().isEmpty()) reason = "Cheating (banwave)";
        if (QUEUE.containsKey(uuid)) return false;

        BanwaveEntry e = new BanwaveEntry(uuid, name, reason, addedBy, System.currentTimeMillis(), evidenceCount);
        QUEUE.put(uuid, e);
        return true;
    }

    public static boolean remove(UUID uuid) {
        return QUEUE.remove(uuid) != null;
    }

    public static void clear() {
        QUEUE.clear();
    }

    public static int size() {
        return QUEUE.size();
    }

    public static Collection<BanwaveEntry> list() {
        return Collections.unmodifiableCollection(QUEUE.values());
    }

    public static void setBanCommandTemplate(String template) {
        if (template != null && template.indexOf("%player%") >= 0) {
            banCommandTemplate = template;
        }
    }

    public static String getBanCommandTemplate() {
        return banCommandTemplate;
    }

    public static void runBanwave(CommandSender initiator, boolean broadcast) {
        Server srv = Lurk.getInstance().getServer();

        if (QUEUE.isEmpty()) {
            ConsoleMsgPatch.send(initiator, "§cBanwave queue is empty.");
            return;
        }

        if (broadcast) safeBroadcast(srv, "§c§lBanwave starting… §7(" + QUEUE.size() + " players)");

        int success = 0;
        for (BanwaveEntry e : new ArrayList<BanwaveEntry>(QUEUE.values())) {
            String cmd = banCommandTemplate
                    .replace("%player%", e.getName())
                    .replace("%uuid%", e.getUuid().toString())
                    .replace("%reason%", sanitizeReason(e.getReason()));
            try {
                CommandSender console = resolveConsoleSender(srv, initiator);
                srv.dispatchCommand(console, cmd);
                success++;
            } catch (Throwable t) {
                ConsoleMsgPatch.send(initiator, "§cFailed to ban §e" + e.getName() + "§7: " + t.getMessage());
            }
            QUEUE.remove(e.getUuid());
        }

        if (broadcast) safeBroadcast(srv, "§a§lBanwave complete. §7Banned " + success + " players.");
        else ConsoleMsgPatch.send(initiator, "§aBanwave complete. Banned " + success + " players.");
    }

    private static String sanitizeReason(String r) {
        if (r == null) return "Cheating";
        return r.replace('\n', ' ').replace('\r', ' ').trim();
    }

    /**
     * Beta-safe UUID resolution:
     * 1) If an online player with that name is present and has getUniqueId(): use it.
     * 2) Else, fall back to deterministic offline UUID.
     */
    public static UUID resolveUuidByBestEffort(String name) {
        if (name == null) return null;

        try {
            Player p = Lurk.getInstance().getServer().getPlayer(name);
            if (p != null) {
                // Try reflective getUniqueId (Beta may not have it)
                try {
                    Method m = Player.class.getMethod("getUniqueId");
                    Object o = m.invoke(p);
                    if (o instanceof UUID) return (UUID) o;
                } catch (Throwable ignored) { }
            }
        } catch (Throwable ignored) { }

        try {
            return java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes("UTF-8"));
        } catch (Exception ex) {
            return java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        }
    }

    /** Try Server.broadcastMessage; if missing, iterate players. */
    private static void safeBroadcast(Server srv, String msg) {
        try {
            // CraftBukkit 1060 had this on Server; if not, fall back
            Method m = Server.class.getMethod("broadcastMessage", String.class);
            m.invoke(srv, msg);
            return;
        } catch (Throwable ignored) { }

        try {
            Object online = srv.getOnlinePlayers();
            if (online instanceof Player[]) {
                Player[] arr = (Player[]) online;
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != null) arr[i].sendMessage(msg);
                }
            } else if (online instanceof java.util.Collection) {
                java.util.Collection coll = (java.util.Collection) online;
                for (Object o : coll) {
                    if (o instanceof Player) ((Player) o).sendMessage(msg);
                }
            }
        } catch (Throwable ignored2) { }
    }

    /** Try Server.getConsoleSender; else use the initiator; else first op player; else a no-op sender. */
    private static CommandSender resolveConsoleSender(Server srv, CommandSender initiator) {
        try {
            Method m = Server.class.getMethod("getConsoleSender");
            Object o = m.invoke(srv);
            if (o instanceof CommandSender) return (CommandSender) o;
        } catch (Throwable ignored) { }
        if (initiator != null) return initiator;

        try {
            Object online = srv.getOnlinePlayers();
            if (online instanceof Player[]) {
                Player[] arr = (Player[]) online;
                for (int i = 0; i < arr.length; i++) {
                    Player p = arr[i];
                    if (p != null && p.isOp()) return p;
                }
            } else if (online instanceof java.util.Collection) {
                java.util.Collection coll = (java.util.Collection) online;
                for (Object o : coll) {
                    if (o instanceof Player) {
                        Player p = (Player) o;
                        if (p.isOp()) return p;
                    }
                }
            }
        } catch (Throwable ignored2) { }

		return new CommandSender() {
			public void sendMessage(String message) { /* no-op */ }
			public void sendMessage(String[] messages) { /* no-op */ }
			public Server getServer() { return srv; }
			public String getName() { return "CONSOLE"; }
			public boolean isPermissionSet(String name) { return true; }
			public boolean isPermissionSet(org.bukkit.permissions.Permission perm) { return true; }
			public boolean hasPermission(String name) { return true; }
			public boolean hasPermission(org.bukkit.permissions.Permission perm) { return true; }

		public org.bukkit.permissions.PermissionAttachment addAttachment(
					org.bukkit.plugin.Plugin plugin, String name, boolean value) { return null; }

			public org.bukkit.permissions.PermissionAttachment addAttachment(
					org.bukkit.plugin.Plugin plugin) { return null; }

			public org.bukkit.permissions.PermissionAttachment addAttachment(
					org.bukkit.plugin.Plugin plugin, String name, boolean value, int ticks) { return null; }

			public org.bukkit.permissions.PermissionAttachment addAttachment(
					org.bukkit.plugin.Plugin plugin, int ticks) { return null; }

			public void removeAttachment(org.bukkit.permissions.PermissionAttachment attachment) {}
			public void recalculatePermissions() {}
			public java.util.Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions() {
				return java.util.Collections.emptySet();
			}
			public boolean isOp() { return true; }
			public void setOp(boolean value) {}
		};
    }
}
