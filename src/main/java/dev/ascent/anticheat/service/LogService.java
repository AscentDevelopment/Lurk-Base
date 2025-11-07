package dev.ascent.anticheat.service;

import dev.ascent.anticheat.model.LogEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LogService {
    private static final int PER_PLAYER_LIMIT = 200;

    private static final Map<UUID, Deque<LogEntry>> LOGS = new ConcurrentHashMap<UUID, Deque<LogEntry>>();

    private LogService() {}

    public static void add(UUID target, LogEntry entry) {
        Deque<LogEntry> q = LOGS.get(target);
        if (q == null) {
            q = new ArrayDeque<LogEntry>(PER_PLAYER_LIMIT + 8);
            LOGS.put(target, q);
        }
        q.addLast(entry);
        while (q.size() > PER_PLAYER_LIMIT) q.removeFirst();
    }

    public static List<LogEntry> getLast(UUID target, int limit) {
        Deque<LogEntry> q = LOGS.get(target);
        if (q == null || q.isEmpty()) return Collections.emptyList();
        List<LogEntry> out = new ArrayList<LogEntry>(Math.min(limit, q.size()));
        Iterator<LogEntry> it = q.descendingIterator();
        int c = 0;
        while (it.hasNext() && c++ < limit) out.add(it.next());
        return out;
    }
}
