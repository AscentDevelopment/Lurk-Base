package dev.ascent.anticheat.model;

public final class LogEntry {
    public final long timeMillis;
    public final String checkName;
    public final String checkType;
    public final double vl;
    public final String details;

    public LogEntry(long timeMillis, String checkName, String checkType, double vl, String details) {
        this.timeMillis = timeMillis;
        this.checkName = checkName;
        this.checkType = checkType;
        this.vl = vl;
        this.details = details;
    }
}
