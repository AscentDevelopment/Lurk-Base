package dev.ascent.anticheat.check.impl.movement.speed;

import dev.ascent.anticheat.check.Check;
import dev.ascent.anticheat.check.CheckData;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;

@CheckData(
        name = "Speed",
        description = "Basic air-friction prediction check",
        experimental = true,
        punishmentVL = 12
)
public final class SpeedA extends Check {

    private double threshold;
    private int exemptTeleportTicks;

    // no @Override (base Event doesn't declare onPosition)
    public void onPosition(PositionUpdateEvent e) {
        final String k = e.getKind().name();

        if ("SERVER_POSLOOK".equals(k)) { // server teleport/correction
            this.exemptTeleportTicks = 20;
            return;
        }

        final boolean isMove =
                "POSITION".equals(k) || "LOOK".equals(k) || "POSITION_LOOK".equals(k) ||
                "CLIENT_POSITION".equals(k) || "CLIENT_LOOK".equals(k) || "CLIENT_POSITION_LOOK".equals(k);

        if (!isMove) return;

        if (this.exemptTeleportTicks > 0) this.exemptTeleportTicks--;

        // movement deltas & ground flags
        final double deltaXZ = getUser().getProcessorManager().getMovementProcessor().getDeltaXZ();
        final double lastDeltaXZ = getUser().getProcessorManager().getMovementProcessor().getLastDeltaXZ();
        final boolean ground = getUser().getProcessorManager().getMovementProcessor().isOnGround();
        final boolean lastGround = getUser().getProcessorManager().getMovementProcessor().wasOnGround();

        double predicted = (lastDeltaXZ * 0.91D) + 0.026D;
        if (Math.abs(predicted) < 0.005D) predicted = 0.0D;

        double diff = deltaXZ - predicted;

        if (this.exemptTeleportTicks == 0 && !ground && !lastGround) {
            if (diff > 1.0E-12D) {
                if (++this.threshold > 3.5D) {
                    this.fail("Air friction mismatch", "d=" + diff, "t=" + this.threshold);
                }
            } else {
                this.threshold -= Math.min(this.threshold, 0.01D);
            }
        } else {
            this.threshold -= Math.min(this.threshold, 0.001D);
        }
    }
}
