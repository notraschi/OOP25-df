package it.unibo.df.model.special;

import java.util.Set;

public record SpecialAbility<T>(
    Set<T> affected,
    SpecialAbilityFn<T> transform,
    int duration // in seconds, to match Ability cooldown
) {
    public boolean isAffected(T input) {
        return affected.contains(input);
    }
}
