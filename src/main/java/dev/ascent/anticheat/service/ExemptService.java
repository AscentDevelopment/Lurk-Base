package dev.ascent.anticheat.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.UUID;

public final class ExemptService {
    private static final Set<UUID> EXEMPT_ALL = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Set<String>> EXEMPT_MAP = new ConcurrentHashMap<UUID, Set<String>>();

    private ExemptService() {}

    public static boolean isExemptAll(UUID u) { return EXEMPT_ALL.contains(u); }

    public static void setExemptAll(UUID u, boolean v) {
        if (v) EXEMPT_ALL.add(u); else EXEMPT_ALL.remove(u);
    }

    /** check names are stored lowercased */
    public static Set<String> getExemptChecks(UUID u) {
        Set<String> s = EXEMPT_MAP.get(u);
        return (s == null) ? java.util.Collections.<String>emptySet() : java.util.Collections.unmodifiableSet(s);
    }

    public static boolean isExempt(UUID u, String checkSimpleName) {
        if (isExemptAll(u)) return true;
        if (checkSimpleName == null) return false;
        Set<String> s = EXEMPT_MAP.get(u);
        return s != null && s.contains(checkSimpleName.toLowerCase());
    }

    public static void add(UUID u, String checkSimpleName) {
        if (checkSimpleName == null || checkSimpleName.length() == 0) return;
        EXEMPT_MAP.computeIfAbsent(u, k -> new ConcurrentSkipListSet<String>())
                  .add(checkSimpleName.toLowerCase());
    }

    public static boolean remove(UUID u, String checkSimpleName) {
        Set<String> s = EXEMPT_MAP.get(u);
        return s != null && s.remove(checkSimpleName.toLowerCase());
    }

    public static void clear(UUID u) {
        EXEMPT_MAP.remove(u);
        EXEMPT_ALL.remove(u);
    }
}
