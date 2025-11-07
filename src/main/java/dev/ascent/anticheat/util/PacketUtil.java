package dev.ascent.anticheat.util;

import com.github.dirtpowered.betaprotocollib.model.AbstractPacket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Beta 1.7.3 packet helpers compatible across common BetaProtocollib forks.
 * Uses simple class-name alias matching to avoid hard compile-time coupling.
 */
public final class PacketUtil {

    private PacketUtil() {}

    // ---------- core matcher ----------
    private static boolean isPacket(AbstractPacket<?> p, Set<String> simpleNames) {
        if (p == null) return false;
        String s = p.getClass().getSimpleName();
        return simpleNames.contains(s);
    }
    private static Set<String> names(String... n) { return new HashSet<>(Arrays.asList(n)); }

    // ---------- Client -> Server ----------
    private static final Set<String> PLAYER_POS_NAMES    = names("PlayerPositionPacket");
    private static final Set<String> PLAYER_LOOK_NAMES   = names("PlayerLookPacket");
    private static final Set<String> PLAYER_BASE_NAMES   = names("PlayerPacket", "FlyingPacket");

    private static final Set<String> USE_ENTITY_NAMES    = names("UseEntityPacket");
    private static final Set<String> BLOCK_DIG_NAMES     = names("BlockDigPacket", "DigPacket", "Packet14BlockDig");
    private static final Set<String> BLOCK_PLACE_NAMES   = names("BlockPlacePacket", "PlaceBlockPacket", "Packet15Place");
    private static final Set<String> HELD_CHANGE_NAMES   = names("HeldItemChangePacket", "SetCurrentItemPacket");
    private static final Set<String> ANIMATION_NAMES     = names("AnimationPacket", "ArmAnimationPacket", "Packet18ArmAnimation");
    private static final Set<String> ENTITY_ACTION_NAMES = names("EntityActionPacket");
    private static final Set<String> WINDOW_CLICK_NAMES  = names("WindowClickPacket", "ClickWindowPacket");
    private static final Set<String> CLOSE_WINDOW_NAMES  = names("CloseWindowPacket");
    private static final Set<String> CHAT_NAMES          = names("ChatPacket", "Packet3Chat");
    private static final Set<String> HANDSHAKE_NAMES     = names("HandshakePacket", "Packet2Handshake", "Handshake");
    private static final Set<String> LOGIN_NAMES         = names("LoginPacket", "Packet1Login", "LoginRequestPacket");

    // ---------- Server -> Client ----------
    private static final Set<String> VEL_NAMES           = names("EntityVelocityPacket", "Packet28EntityVelocity");
    private static final Set<String> TP_NAMES            = names("EntityTeleportPacket", "Packet34EntityTeleport");

    private static final Set<String> ENT_MOVE_NAMES      = names("EntityMovePacket", "EntityRelMovePacket", "Packet31RelEntityMove");
    private static final Set<String> ENT_LOOK_NAMES      = names("EntityLookPacket", "EntityRelLookPacket", "Packet32EntityLook");
    private static final Set<String> ENT_MOVELOOK_NAMES  = names("EntityMoveLookPacket", "EntityRelMoveLookPacket", "Packet33RelEntityMoveLook");

    private static final Set<String> SPAWN_PLAYER_NAMES  = names("SpawnPlayerPacket", "NamedEntitySpawnPacket", "Packet20NamedEntitySpawn");
    private static final Set<String> SPAWN_MOB_NAMES     = names("SpawnMobPacket", "MobSpawnPacket", "Packet24MobSpawn");
    private static final Set<String> DESTROY_ENTITY_NAMES= names("DestroyEntityPacket", "EntityDestroyPacket", "Packet29DestroyEntity");

    private static final Set<String> SET_SLOT_NAMES      = names("SetSlotPacket", "Packet103SetSlot");
    private static final Set<String> WINDOW_ITEMS_NAMES  = names("WindowItemsPacket", "Packet104WindowItems");

    private static final Set<String> KEEPALIVE_NAMES     = names("KeepAlivePacket", "Packet0KeepAlive");
    private static final Set<String> TIME_UPDATE_NAMES   = names("TimeUpdatePacket", "UpdateTimePacket", "Packet4UpdateTime");

    // ----- Extra AC-useful (new) -----
    private static final Set<String> CONFIRM_TX_NAMES    = names("ConfirmTransactionPacket", "TransactionPacket", "Packet106Transaction");
    private static final Set<String> OPEN_WINDOW_NAMES   = names("OpenWindowPacket", "Packet100OpenWindow");
    private static final Set<String> UPDATE_HEALTH_NAMES = names("UpdateHealthPacket", "Packet8UpdateHealth");
    private static final Set<String> RESPAWN_NAMES       = names("RespawnPacket", "Packet9Respawn");
    private static final Set<String> EXPLOSION_NAMES     = names("ExplosionPacket", "Packet60Explosion");

    private static final Set<String> BLOCK_CHANGE_NAMES      = names("BlockChangePacket", "Packet53BlockChange");
    private static final Set<String> MULTI_BLOCK_CHANGE_NAMES= names("MultiBlockChangePacket", "Packet52MultiBlockChange");

    private static final Set<String> MAP_CHUNK_NAMES     = names("MapChunkPacket", "Packet51MapChunk");
    private static final Set<String> PRE_CHUNK_NAMES     = names("PreChunkPacket", "Packet50PreChunk");

    private static final Set<String> ENTITY_METADATA_NAMES= names("EntityMetadataPacket", "Packet40EntityMetadata", "EntityMetadata");
    private static final Set<String> ENTITY_STATUS_NAMES  = names("EntityStatusPacket", "Packet38EntityStatus");

    private static final Set<String> PICKUP_SPAWN_NAMES  = names("PickupSpawnPacket", "Packet21PickupSpawn");

    // ---------- predicates ----------
    // movement (client)
    public static boolean isPlayerPosition(AbstractPacket<?> p) { return isPacket(p, PLAYER_POS_NAMES); }
    public static boolean isPlayerLook(AbstractPacket<?> p)     { return isPacket(p, PLAYER_LOOK_NAMES); }
    public static boolean isPlayerBase(AbstractPacket<?> p)     { return isPacket(p, PLAYER_BASE_NAMES); }
    public static boolean isAnyMovement(AbstractPacket<?> p)    { return isPlayerBase(p) || isPlayerPosition(p) || isPlayerLook(p); }

    // actions (client)
    public static boolean isUseEntity(AbstractPacket<?> p)      { return isPacket(p, USE_ENTITY_NAMES); }
    public static boolean isBlockDig(AbstractPacket<?> p)       { return isPacket(p, BLOCK_DIG_NAMES); }
    public static boolean isBlockPlace(AbstractPacket<?> p)     { return isPacket(p, BLOCK_PLACE_NAMES); }
    public static boolean isHeldItemChange(AbstractPacket<?> p) { return isPacket(p, HELD_CHANGE_NAMES); }
    public static boolean isAnimation(AbstractPacket<?> p)      { return isPacket(p, ANIMATION_NAMES); }
    public static boolean isEntityAction(AbstractPacket<?> p)   { return isPacket(p, ENTITY_ACTION_NAMES); }
    public static boolean isWindowClick(AbstractPacket<?> p)    { return isPacket(p, WINDOW_CLICK_NAMES); }
    public static boolean isCloseWindow(AbstractPacket<?> p)    { return isPacket(p, CLOSE_WINDOW_NAMES); }
    public static boolean isChat(AbstractPacket<?> p)           { return isPacket(p, CHAT_NAMES); }
    public static boolean isHandshake(AbstractPacket<?> p)      { return isPacket(p, HANDSHAKE_NAMES); }
    public static boolean isLogin(AbstractPacket<?> p)          { return isPacket(p, LOGIN_NAMES); }

    // server entity updates
    public static boolean isEntityVelocity(AbstractPacket<?> p) { return isPacket(p, VEL_NAMES); }
    public static boolean isEntityTeleport(AbstractPacket<?> p) { return isPacket(p, TP_NAMES); }
    public static boolean isEntityMove(AbstractPacket<?> p)     { return isPacket(p, ENT_MOVE_NAMES); }
    public static boolean isEntityLook(AbstractPacket<?> p)     { return isPacket(p, ENT_LOOK_NAMES); }
    public static boolean isEntityMoveLook(AbstractPacket<?> p) { return isPacket(p, ENT_MOVELOOK_NAMES); }
    public static boolean isAnyEntityMoveFromServer(AbstractPacket<?> p) {
        return isEntityTeleport(p) || isEntityMove(p) || isEntityLook(p) || isEntityMoveLook(p);
    }

    // spawns/despawns
    public static boolean isSpawnPlayer(AbstractPacket<?> p)    { return isPacket(p, SPAWN_PLAYER_NAMES); }
    public static boolean isSpawnMob(AbstractPacket<?> p)       { return isPacket(p, SPAWN_MOB_NAMES); }
    public static boolean isDestroyEntity(AbstractPacket<?> p)  { return isPacket(p, DESTROY_ENTITY_NAMES); }

    // inventory sync
    public static boolean isSetSlot(AbstractPacket<?> p)        { return isPacket(p, SET_SLOT_NAMES); }
    public static boolean isWindowItems(AbstractPacket<?> p)    { return isPacket(p, WINDOW_ITEMS_NAMES); }
    public static boolean isAnyInventory(AbstractPacket<?> p)   { return isWindowClick(p) || isCloseWindow(p) || isSetSlot(p) || isWindowItems(p); }

    // timing / health / session
    public static boolean isKeepAlive(AbstractPacket<?> p)      { return isPacket(p, KEEPALIVE_NAMES); }
    public static boolean isTimeUpdate(AbstractPacket<?> p)     { return isPacket(p, TIME_UPDATE_NAMES); }
    public static boolean isConfirmTransaction(AbstractPacket<?> p) { return isPacket(p, CONFIRM_TX_NAMES); }
    public static boolean isOpenWindow(AbstractPacket<?> p)     { return isPacket(p, OPEN_WINDOW_NAMES); }
    public static boolean isUpdateHealth(AbstractPacket<?> p)   { return isPacket(p, UPDATE_HEALTH_NAMES); }
    public static boolean isRespawn(AbstractPacket<?> p)        { return isPacket(p, RESPAWN_NAMES); }

    // world
    public static boolean isExplosion(AbstractPacket<?> p)      { return isPacket(p, EXPLOSION_NAMES); }
    public static boolean isBlockChange(AbstractPacket<?> p)    { return isPacket(p, BLOCK_CHANGE_NAMES); }
    public static boolean isMultiBlockChange(AbstractPacket<?> p){ return isPacket(p, MULTI_BLOCK_CHANGE_NAMES); }
    public static boolean isMapChunk(AbstractPacket<?> p)       { return isPacket(p, MAP_CHUNK_NAMES); }
    public static boolean isPreChunk(AbstractPacket<?> p)       { return isPacket(p, PRE_CHUNK_NAMES); }
    public static boolean isAnyChunkPacket(AbstractPacket<?> p) { return isMapChunk(p) || isPreChunk(p); }

    // entity metadata/status
    public static boolean isEntityMetadata(AbstractPacket<?> p) { return isPacket(p, ENTITY_METADATA_NAMES); }
    public static boolean isEntityStatus(AbstractPacket<?> p)   { return isPacket(p, ENTITY_STATUS_NAMES); }

    // items
    public static boolean isPickupSpawn(AbstractPacket<?> p)    { return isPacket(p, PICKUP_SPAWN_NAMES); }
}