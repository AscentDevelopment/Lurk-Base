package dev.ascent.anticheat.protocol;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.betaprotocollib.model.AbstractPacket;
import com.github.dirtpowered.betaprotocollib.registry.PacketRegistry;
import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.protocol.events.*;
import dev.ascent.anticheat.util.PacketUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Objects;

public final class ProtocolAdapter {

    private final PacketRegistry registry;

    public ProtocolAdapter() {
        this.registry = BetaLib.getRegistryFor(MinecraftVersion.B_1_7_3);
    }

    public PacketRegistry getRegistry() {
        return registry;
    }

    /**
     * Dispatch a decoded BetaProtocollib packet to your internal bus.
     * Call this from wherever you intercept packets (when you wire hooks later).
     */
    public void handle(Player player, AbstractPacket<?> packet, Object data) {
        try {
            // -------------------- CLIENT -> SERVER: movement --------------------
            if (PacketUtil.isPlayerPosition(packet)) {
                // V1_7_3PlayerPositionPacketData: getX/getY/getZ/isOnGround
                double x = getDouble(data, "getX");
                double y = getDouble(data, "getY");
                double z = getDouble(data, "getZ");
                boolean onGround = getBoolean(data, "isOnGround");
                ChecksBus.post(new PositionUpdateEvent(player, x, y, z, 0.0f, 0.0f, onGround, PositionUpdateEvent.Kind.CLIENT_POSITION));
                return;
            }
            if (PacketUtil.isPlayerLook(packet)) {
                // V1_7_3PlayerLookPacketData: getYaw/getPitch/isOnGround
                float yaw = (float) getDouble(data, "getYaw");    // some builds may return double
                float pitch = (float) getDouble(data, "getPitch");
                boolean onGround = getBoolean(data, "isOnGround");
                Location loc = player.getLocation();
                ChecksBus.post(new PositionUpdateEvent(player, loc.getX(), loc.getY(), loc.getZ(), yaw, pitch, onGround, PositionUpdateEvent.Kind.CLIENT_LOOK));
                return;
            }
            if (PacketUtil.isPlayerBase(packet)) {
                // Player/Flying: only onGround heartbeat
                boolean onGround = getBoolean(data, "isOnGround");
                Location loc = player.getLocation();
                ChecksBus.post(new PositionUpdateEvent(player, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), onGround, PositionUpdateEvent.Kind.CLIENT_FLYING));
                return;
            }

            // -------------------- CLIENT -> SERVER: actions --------------------
            if (PacketUtil.isUseEntity(packet)) {
                // V1_7_3UseEntityPacketData: getTargetEntityId()/isLeftClick()
                int targetId = getInt(data, "getTargetEntityId");
                boolean leftClick = getBoolean(data, "isLeftClick", "getMouse"); // some forks used getMouse()
                ChecksBus.post(new UseEntityEvent(player, targetId, leftClick));
                return;
            }
            if (PacketUtil.isBlockDig(packet)) {
                // V1_7_3BlockDigPacketData: getX/getY/getZ/getStatus/getFace
                int x = getInt(data, "getX");
                int y = getInt(data, "getY");
                int z = getInt(data, "getZ");
                int status = getInt(data, "getStatus");
                int face = getInt(data, "getFace");
                ChecksBus.post(new BlockDigEvent(player, x, y, z, status, face));
                return;
            }
            if (PacketUtil.isBlockPlace(packet)) {
                // getX/getY/getZ/getFace
                int x = getInt(data, "getX");
                int y = getInt(data, "getY");
                int z = getInt(data, "getZ");
                int face = getInt(data, "getFace");
                ChecksBus.post(new BlockPlaceEvent(player, x, y, z, face, data));
                return;
            }
            if (PacketUtil.isHeldItemChange(packet)) {
                // HeldItemChange/SetCurrentItem: getSlot()/getItemId() (slot is key for AC)
                int slot = getInt(data, "getSlot", "getHotbarSlot", "getItem");
                ChecksBus.post(new HeldItemChangeEvent(player, slot, data));
                return;
            }
            if (PacketUtil.isAnimation(packet)) {
                // Animation/ArmAnimation: getAnimationId()
                int animId = getInt(data, "getAnimationId", "getType");
                ChecksBus.post(new AnimationEvent(player, animId, data));
                return;
            }
            if (PacketUtil.isEntityAction(packet)) {
                // EntityAction: getActionId()/getAuxData()
                int action = getInt(data, "getAction", "getActionId");
                int aux = getInt(data, "getAuxData", "getParam");
                ChecksBus.post(new EntityActionEvent(player, action, aux, data));
                return;
            }
            if (PacketUtil.isWindowClick(packet)) {
                int windowId = getInt(data, "getWindowId");
                int slot = getInt(data, "getSlot");
                ChecksBus.post(new WindowClickEvent(player, windowId, slot, data));
                return;
            }
            if (PacketUtil.isCloseWindow(packet)) {
                int windowId = getInt(data, "getWindowId");
                ChecksBus.post(new CloseWindowEvent(player, windowId, data));
                return;
            }
            if (PacketUtil.isChat(packet)) {
                // Optional hook for spam/macro heuristics (message extraction skipped)
                // String msg = getString(data, "getMessage");
                // (No event needed unless you want one)
            }
            if (PacketUtil.isHandshake(packet)) {
                ChecksBus.post(new HandshakeEvent(player, data));
                return;
            }
            if (PacketUtil.isLogin(packet)) {
                int eid = getInt(data, "getEntityId", "getPlayerEntityId");
                ChecksBus.post(new LoginEvent(player, eid, data));
                return;
            }

            // -------------------- SERVER -> CLIENT: entity updates --------------------
            if (PacketUtil.isEntityVelocity(packet)) {
                int eid = getInt(data, "getEntityId");
                int mx = getInt(data, "getMotionX");
                int my = getInt(data, "getMotionY");
                int mz = getInt(data, "getMotionZ");
                ChecksBus.post(new VelocityEvent(player, eid, mx, my, mz));
                return;
            }
            if (PacketUtil.isEntityTeleport(packet)) {
                int eid = getInt(data, "getEntityId");
                double x = getDouble(data, "getX");
                double y = getDouble(data, "getY");
                double z = getDouble(data, "getZ");
                float yaw = (float) getDouble(data, "getYaw");
                float pitch = (float) getDouble(data, "getPitch");
                ChecksBus.post(new EntityTeleportEvent(player, eid, x, y, z, yaw, pitch, data));
                return;
            }
            if (PacketUtil.isEntityMove(packet)) {
                int eid = getInt(data, "getEntityId");
                double dx = normRel(getInt(data, "getDx", "getDeltaX", "getDeltaPosX"));
                double dy = normRel(getInt(data, "getDy", "getDeltaY", "getDeltaPosY"));
                double dz = normRel(getInt(data, "getDz", "getDeltaZ", "getDeltaPosZ"));
                ChecksBus.post(new EntityMoveEvent(player, eid, dx, dy, dz, data));
                return;
            }
            if (PacketUtil.isEntityLook(packet)) {
                int eid = getInt(data, "getEntityId");
                float yaw = byteAngle(getInt(data, "getYaw"));
                float pitch = byteAngle(getInt(data, "getPitch"));
                ChecksBus.post(new EntityLookEvent(player, eid, yaw, pitch, data));
                return;
            }
            if (PacketUtil.isEntityMoveLook(packet)) {
                int eid = getInt(data, "getEntityId");
                double dx = normRel(getInt(data, "getDx", "getDeltaX", "getDeltaPosX"));
                double dy = normRel(getInt(data, "getDy", "getDeltaY", "getDeltaPosY"));
                double dz = normRel(getInt(data, "getDz", "getDeltaZ", "getDeltaPosZ"));
                float yaw = byteAngle(getInt(data, "getYaw"));
                float pitch = byteAngle(getInt(data, "getPitch"));
                // Post both to reuse downstream logic
                ChecksBus.post(new EntityMoveEvent(player, eid, dx, dy, dz, data));
                ChecksBus.post(new EntityLookEvent(player, eid, yaw, pitch, data));
                return;
            }

            // -------------------- SERVER -> CLIENT: spawns/despawns --------------------
            if (PacketUtil.isSpawnPlayer(packet)) {
                int eid = getInt(data, "getEntityId");
                ChecksBus.post(new SpawnPlayerEvent(player, eid, data));
                return;
            }
            if (PacketUtil.isSpawnMob(packet)) {
                int eid = getInt(data, "getEntityId");
                int type = getInt(data, "getMobType", "getType");
                ChecksBus.post(new SpawnMobEvent(player, eid, type, data));
                return;
            }
            if (PacketUtil.isDestroyEntity(packet)) {
                // some packets carry int[], others a single id
                int[] ids = getIntArray(data, "getEntityIds");
                if (ids == null) {
                    ids = new int[]{ getInt(data, "getEntityId") };
                }
                ChecksBus.post(new DestroyEntityEvent(player, ids, data));
                return;
            }

            // -------------------- INVENTORY SYNC --------------------
            if (PacketUtil.isSetSlot(packet)) {
                int windowId = getInt(data, "getWindowId");
                int slot = getInt(data, "getSlot");
                ChecksBus.post(new SetSlotEvent(player, windowId, slot, data));
                return;
            }
            if (PacketUtil.isWindowItems(packet)) {
                int windowId = getInt(data, "getWindowId");
                int count = getInt(data, "getItemCount", "getCount", "size"); // best effort
                ChecksBus.post(new WindowItemsEvent(player, windowId, count, data));
                return;
            }

            // -------------------- TIMING / SESSION --------------------
            if (PacketUtil.isKeepAlive(packet)) {
                int id = getInt(data, "getId", "getKeepAliveId");
                ChecksBus.post(new KeepAliveEvent(player, id, data));
                return;
            }
            if (PacketUtil.isTimeUpdate(packet)) {
                // Optional to use; usually not needed for AC
                // long worldTime = getLong(data, "getTime");
            }
            if (PacketUtil.isConfirmTransaction(packet)) {
                int windowId = getInt(data, "getWindowId");
                short action = getShort(data, "getActionNumber", "getAction");
                boolean accepted = getBoolean(data, "isAccepted", "isAcknowledged", "getAccepted");
                ChecksBus.post(new TransactionEvent(player, windowId, action, accepted, data));
                return;
            }
            if (PacketUtil.isOpenWindow(packet)) {
                int windowId = getInt(data, "getWindowId");
                int typeId = getInt(data, "getType", "getTypeId");
                String title = getString(data, "getTitle", "getWindowTitle");
                int slots = getInt(data, "getSlots", "getSlotCount");
                ChecksBus.post(new OpenWindowEvent(player, windowId, typeId, title, slots, data));
                return;
            }
            if (PacketUtil.isUpdateHealth(packet)) {
                float health = (float) getDouble(data, "getHealth");
                ChecksBus.post(new UpdateHealthEvent(player, health, data));
                return;
            }
            if (PacketUtil.isRespawn(packet)) {
                int dimension = getInt(data, "getDimension", "getWorld", "getEnv");
                ChecksBus.post(new RespawnEvent(player, dimension, data));
                return;
            }

            // -------------------- WORLD --------------------
            if (PacketUtil.isExplosion(packet)) {
                double x = getDouble(data, "getX");
                double y = getDouble(data, "getY");
                double z = getDouble(data, "getZ");
                float strength = (float) getDouble(data, "getStrength", "getPower");
                ChecksBus.post(new ExplosionEvent(player, x, y, z, strength, data));
                return;
            }
            if (PacketUtil.isBlockChange(packet)) {
                int x = getInt(data, "getX");
                int y = getInt(data, "getY");
                int z = getInt(data, "getZ");
                int id = getInt(data, "getBlockId", "getType");
                int meta = getInt(data, "getBlockData", "getData");
                ChecksBus.post(new BlockChangeEvent(player, x, y, z, id, meta, data));
                return;
            }
            if (PacketUtil.isMultiBlockChange(packet)) {
                int cx = getInt(data, "getChunkX");
                int cz = getInt(data, "getChunkZ");
                int count = getInt(data, "getCount", "getChangeCount");
                ChecksBus.post(new MultiBlockChangeEvent(player, cx, cz, count, data));
                return;
            }
            if (PacketUtil.isMapChunk(packet)) {
                int cx = getInt(data, "getChunkX");
                int cz = getInt(data, "getChunkZ");
                ChecksBus.post(new ChunkLoadEvent(player, cx, cz, data));
                return;
            }
            if (PacketUtil.isPreChunk(packet)) {
                int cx = getInt(data, "getChunkX");
                int cz = getInt(data, "getChunkZ");
                boolean mode = getBoolean(data, "isMode", "getMode"); // 1=load, 0=unload in some forks
                if (mode) {
                    ChecksBus.post(new ChunkLoadEvent(player, cx, cz, data));
                } else {
                    ChecksBus.post(new ChunkUnloadEvent(player, cx, cz, data));
                }
                return;
            }

            // -------------------- ENTITY META / STATUS / PICKUP --------------------
            if (PacketUtil.isEntityMetadata(packet)) {
                int eid = getInt(data, "getEntityId");
                Object meta = getObject(data, "getMetadata");
                ChecksBus.post(new EntityMetadataEvent(player, eid, meta, data));
                return;
            }
            if (PacketUtil.isEntityStatus(packet)) {
                int eid = getInt(data, "getEntityId");
                int status = getInt(data, "getStatus");
                ChecksBus.post(new EntityStatusEvent(player, eid, status, data));
                return;
            }
            if (PacketUtil.isPickupSpawn(packet)) {
                int eid = getInt(data, "getEntityId");
                int itemId = getInt(data, "getItemId", "getId");
                int count = getInt(data, "getCount", "getAmount");
                double x = getDouble(data, "getX");
                double y = getDouble(data, "getY");
                double z = getDouble(data, "getZ");
                ChecksBus.post(new PickupSpawnEvent(player, eid, itemId, count, x, y, z, data));
            }

        } catch (Throwable t) {
            // We NEVER want to crash the server or plugin while decoding a packet.
            // Log lightly once per session in your own logger if needed; swallow here.
        }
    }

    // ------------------------------------------------------------------------------------
    // Helpers: reflection with small fallbacks so we compile against any fork cleanly.
    // ------------------------------------------------------------------------------------
    private static Method findMethod(Object obj, String name) {
        if (obj == null) return null;
        for (Method m : obj.getClass().getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) return m;
        }
        return null;
    }

    private static Object invoke(Object obj, String... names) {
        if (obj == null) return null;
        for (String n : names) {
            Method m = findMethod(obj, n);
            if (m != null) {
                try { return m.invoke(obj); } catch (Throwable ignored) { }
            }
        }
        return null;
    }

    private static int getInt(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof Boolean) return ((Boolean) v) ? 1 : 0;
        return 0;
    }

    private static short getShort(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof Number) return ((Number) v).shortValue();
        return 0;
    }

    private static long getLong(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof Number) return ((Number) v).longValue();
        return 0L;
    }

    private static double getDouble(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }

    private static boolean getBoolean(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Number) return ((Number) v).intValue() != 0;
        return false;
    }

    private static String getString(Object obj, String... names) {
        Object v = invoke(obj, names);
        return v != null ? Objects.toString(v, null) : null;
    }

    private static int[] getIntArray(Object obj, String... names) {
        Object v = invoke(obj, names);
        if (v instanceof int[]) return (int[]) v;
        return null;
    }

    private static Object getObject(Object obj, String... names) {
        return invoke(obj, names);
    }

    // Beta rel move deltas are bytes scaled by 32 in many builds; normalize to blocks/tick if needed.
    private static double normRel(int delta) {
        // Many beta protocols use delta = floor(d * 32)
        return delta / 32.0;
    }

    // Convert byte angle (0..255) to degrees (0..360)
    private static float byteAngle(int b) {
        return (b & 0xFF) * (360.0f / 256.0f);
    }
}