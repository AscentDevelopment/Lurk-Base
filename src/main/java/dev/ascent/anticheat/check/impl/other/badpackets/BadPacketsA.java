package dev.ascent.anticheat.check.impl.other.badpackets;

import dev.ascent.anticheat.check.Check;
import dev.ascent.anticheat.check.CheckData;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;

@CheckData(
        name = "BadPackets",
        description = "Impossible pitch (>90 abs)",
        punishmentVL = 3
)
public final class BadPacketsA extends Check {

    public void onPosition(PositionUpdateEvent e) {
        final String k = e.getKind().name();
        final boolean isMoveLike =
                "POSITION".equals(k) || "LOOK".equals(k) || "POSITION_LOOK".equals(k) ||
                "CLIENT_POSITION".equals(k) || "CLIENT_LOOK".equals(k) || "CLIENT_POSITION_LOOK".equals(k);

        if (!isMoveLike) return;

        double pitchAbs = Math.abs(
                getUser()
                        .getProcessorManager()
                        .getMovementProcessor()
                        .getTo()
                        .getPitch()
        );

        if (pitchAbs > 90.0D) {
            this.fail("Impossible pitch", "pitch=" + pitchAbs);
        }
    }
}
