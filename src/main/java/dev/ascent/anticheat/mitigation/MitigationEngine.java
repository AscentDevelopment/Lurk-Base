package dev.ascent.anticheat.mitigation;

import dev.ascent.anticheat.check.Check;
import dev.ascent.anticheat.user.User;

import java.util.HashMap;
import java.util.Map;

public final class MitigationEngine {

    public static final class Rule {
        public String mitigationKey;  // e.g. "setback", "damage_reduction"
        public int triggerVl;         // first VL to trigger (inclusive)
        public int everyVl;           // if >0, re-apply on each (triggerVl + n * everyVl)
        public long durationMs;       // 0 = no timeout
        public Map<String,String> params; // arbitrary per-mitigation options
    }

    // checkId "Speed.A" -> Rule
    private final Map<String, Rule> rules = new HashMap<String, Rule>();
    private final MitigationManager manager;

    public MitigationEngine(MitigationManager manager) {
        this.manager = manager;
    }

    public void putRule(String checkId, Rule r) {
        if (checkId == null || r == null) return;
        rules.put(checkId.toLowerCase(), r);
    }

    /** Call this after a check increments VL (i.e., from Check.fail). */
    public void onViolation(User user, Check check, double newVl) {
        String checkId = check.getCheckName() + "." + check.getCheckType(); // "Speed.A"
        Rule r = rules.get(checkId.toLowerCase());
        if (r == null) return;

        int vlInt = (int)Math.floor(newVl);
        if (vlInt < r.triggerVl) return;
        if (r.everyVl > 0) {
            int delta = vlInt - r.triggerVl;
            if (delta < 0 || (delta % r.everyVl) != 0) return;
        }

        Map<String,String> p = (r.params == null ? new HashMap<String,String>() : new HashMap<String,String>(r.params));
        p.put("trigger_vl", String.valueOf(r.triggerVl));
        p.put("check_id", checkId);

        MitigationContext ctx = new MitigationContext(
                "auto:" + checkId,
                (r.durationMs <= 0 ? 0L : System.currentTimeMillis() + r.durationMs),
                p
        );
        manager.apply(user, r.mitigationKey, ctx);
    }
}
