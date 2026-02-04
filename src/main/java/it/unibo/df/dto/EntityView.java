package it.unibo.df.dto;

import java.util.List;

import it.unibo.df.model.abilities.Vec2D;

public record EntityView(
    int hpMax,
    int hp,
    Vec2D position,
    List<Integer> cooldownAbilities,
    Integer cooldownMove //int moveCooldownTicks //tempo rimanente TODO
) {
    public double hpRatio() {
        return hpMax <= 0 ? 0.0 : (double) hp / (double) hpMax;
    }
}