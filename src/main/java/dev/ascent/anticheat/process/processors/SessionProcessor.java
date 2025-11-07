package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.user.User;

@ProcessorInfo(name = "Session")
public final class SessionProcessor extends Processor {

    private final long joinMillis = System.currentTimeMillis();

    // Optional metadata (fill later if you detect)
    private String clientName;
    private Integer clientProtocol;

    // You may persist total across joins; for now runtime only
    private long totalPlayMillis = -1L;

    public SessionProcessor(User user) { super(user); }

    public long getJoinMillis() { return joinMillis; }
    public long getTotalPlayMillis() { return totalPlayMillis; }
    public void setTotalPlayMillis(long v) { this.totalPlayMillis = v; }

    public String getClientName() { return clientName; }
    public void setClientName(String name) { this.clientName = name; }

    public Integer getClientProtocol() { return clientProtocol; }
    public void setClientProtocol(Integer p) { this.clientProtocol = p; }
}
