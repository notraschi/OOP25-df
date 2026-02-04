package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import it.unibo.df.model.combat.Cooldown;

/**
 * represents a SpecialAbility.
 * only castable by enemies, are meant to disrupt player's actions.
 */
public record SpecialAbility<T>(
    Class<T> inputType,
    Set<T> affected,
    Function<T, Optional<T>> fn,
    Cooldown timer
) {
    /**
     * checks if the Special Ability is made for this kind of input.
     * 
     * @param input the input
     * @return true if a transform can be applied
     */
    public boolean canHandle(Object input) {
        return inputType.isInstance(input);
    }

    /**
     * transforms the input (this is the disruption).
     * 
     * @param input the input to transform
     * @return an empty Optional if input was denied, or one with the transformed input
     */
    public Optional<T> trasform(T input) {
        if (!affected.contains(input)) {
            return Optional.of(input);
        } else {
            return fn.apply(input);
        }
    }
}