package it.unibo.df.model.combat;

/**
 * handles cooldowns, uses milliseconds as unit.
 */
public class Cooldown {
    private final long duration;
    private long remaining;

    public Cooldown(long time) {
        duration = time;
    }

    public void begin() {
        remaining = duration;
    }

    public void update(long deltaTime) {
        if (deltaTime > 0) {
            remaining = Math.max(remaining - deltaTime, 0);
        }
    }

    public boolean isActive() {
        return remaining > 0;
    }

    public long getRemaining() {
        return remaining;
    }
}
