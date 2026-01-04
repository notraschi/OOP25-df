package it.unibo.df.model.combat;

import java.util.concurrent.TimeUnit;

public class Cooldown {
    private long cooldown = 0;

    public void setCooldown(long delay, TimeUnit unit) {
        cooldown = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit);
    }

    public boolean isExpire() {
        return getRemainingTime(TimeUnit.MILLISECONDS) < 0; 
    }

    public long getRemainingTime(TimeUnit unit){
        return unit.convert(cooldown - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
