package it.unibo.df.model.abilities;

/**
 * Functional interface defining the logic of an ability.
 */
@FunctionalInterface

public interface AbilityFn {
    
    /**
     * Applies the ability logic and computes its effect.
     *
     * @param gameState the current game state
     * @param caster the entity using the ability
     * @param target the target position
     * @return the resulting ability effect
     */
    AbilityEffect apply(Object gameState, Object caster, Vec2D target);
}

