package it.unibo.df.model.special;

import java.util.Set;

import it.unibo.df.input.Input;

public record SpecialAbility(
    Set<Input> affected,
    SpecialAbilityFn transform,
    int duration // in seconds, to match Ability cooldown
) {
    public boolean isAffected(Input input) {
        return affected.contains(input);
    }
}
