package dev.ascent.anticheat.model;

import java.util.UUID;

public final class BanwaveEntry {
    private final UUID uuid;
    private final String name;
    private final String reason;
    private final String addedBy;
    private final long addedAt;
    private final int evidenceCount;

    public BanwaveEntry(UUID uuid, String name, String reason, String addedBy, long addedAt, int evidenceCount) {
        this.uuid = uuid;
        this.name = name;
        this.reason = reason;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.evidenceCount = evidenceCount;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getReason() { return reason; }
    public String getAddedBy() { return addedBy; }
    public long getAddedAt() { return addedAt; }
    public int getEvidenceCount() { return evidenceCount; }
}
