package dev.ascent.anticheat.mitigation;

import java.util.Map;

public final class MitigationContext {
    private final String reason;
    private final long expiresAtMillis; // <=0 means no auto-expiry
    private final Map<String,String> params;

    public MitigationContext(String reason, long expiresAtMillis, Map<String,String> params) {
        this.reason = (reason == null ? "" : reason);
        this.expiresAtMillis = expiresAtMillis;
        this.params = params;
    }

    public String reason() { return reason; }
    public long expiresAtMillis() { return expiresAtMillis; }
    public boolean isExpired(long now) { return expiresAtMillis > 0 && now >= expiresAtMillis; }
    public Map<String,String> params() { return params; }

    public String param(String key, String def) {
        if (params == null) return def;
        String v = params.get(key);
        return (v == null ? def : v);
    }
}
