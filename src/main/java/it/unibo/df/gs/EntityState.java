package it.unibo.df.gs;

import it.unibo.df.model.abilities.Vec2D;

public record EntityState(
    int hpMax,
    int hp,
    Vec2D position
    //cooldown abilities
    //int moveCooldownTicks //tempo rimanente TODO
) {
    public double hpRatio() {
        return hpMax <= 0 ? 0.0 : (double) hp / (double) hpMax;
    }
}
