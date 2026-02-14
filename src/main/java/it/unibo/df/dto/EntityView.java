package it.unibo.df.dto;

import java.util.List;

import it.unibo.df.model.abilities.Vec2D;

/**
 * Contains information on the entities in game.
 * 
 * @param hpMax life value at the start of the match
 * @param hp life value during the match
 * @param position of entity
 * @param cooldownAbilities cooldown ability during the match
 * @param cooldownMove cooldown movement during the match
 */
public record EntityView(
    int hpMax,
    int hp,
    Vec2D position,
    List<Long> cooldownAbilities,
    int cooldownMove
) {
    /**
     * Calculate the hp rateo.
     * 
     * @return hp value between 0.0 to 1.0
     */
    public double hpRatio() {
        return hpMax <= 0 ? 0.0 : (double) hp / (double) hpMax;
    }
}
