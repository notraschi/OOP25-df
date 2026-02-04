package it.unibo.df.model.special;

import java.util.Optional;

import it.unibo.df.input.Input;

/**
 * similar to AbilityFn, represents
 */
@FunctionalInterface
public interface SpecialAbilityFn {
    Optional<Input> transform(Input input);

    static Optional<Input> deny(Input input) {
        return Optional.empty();
    }
}
