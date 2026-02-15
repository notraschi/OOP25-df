package it.unibo.df.model.abilities;

import java.util.Optional;
import java.util.Set;

import it.unibo.df.utility.Vec2D;

/**
 * Functional interface defining the logic of an ability.
 */
@FunctionalInterface
public interface AbilityFn {

    /**
     * Applies the ability effect.
     *
     * @param caster the position of the entity using the ability
     * @return an optional set of affected cells
     */
    Optional<Set<Vec2D>> apply(Vec2D caster);
}

