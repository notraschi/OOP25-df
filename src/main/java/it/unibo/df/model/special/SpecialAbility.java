package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import it.unibo.df.model.combat.Cooldown;

public record SpecialAbility<T>(
    Class<T> inputType,
    Set<T> affected,
    Function<T, Optional<T>> fn,
    Cooldown timer
) { 
    public boolean canHandle(Object input) {
        return inputType.isInstance(input);
    }

    public Optional<T> trasform(T input) {
        if (!affected.contains(input)) {
            return Optional.of(input);
        } else {
            return fn.apply(input);
        }
    }
}