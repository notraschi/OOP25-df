package it.unibo.df.model.combat;

/*
- why no System Time?
- this cooldown handles _relative_ times, what if the game is paused? what about testing?
  by relying on System time we cannot have those.
- why no TimeUnit?
- moving away from System time means there is no need to TimeUnit,
  everything is milliseconds anyway.
*/

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
