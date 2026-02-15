package it.unibo.df.model.combat;

/**
 * Handles cooldowns, uses milliseconds as unit.
 */
public class Cooldown {
    private final long duration;
    private long remaining;

    /**
     * Creates a cooldown with the given duration.
     *
     * @param time duration in milliseconds
     */
    public Cooldown(final long time) {
        duration = time;
    }

    /**
     * Starts the cooldown.
     */
    public void begin() {
        remaining = duration;
    }

    /**
     * Updates the cooldown timer.
     *
     * @param deltaTime elapsed time in milliseconds
     */
    public void update(final long deltaTime) {
        if (deltaTime > 0) {
            remaining = Math.max(remaining - deltaTime, 0);
        }
    }

    /**
     * @return true if the cooldown is active
     */
    public boolean isActive() {
        return remaining > 0;
    }

    /**
     * @return remaining time in milliseconds
     */
    public long getRemaining() {
        return remaining;
    }
}
