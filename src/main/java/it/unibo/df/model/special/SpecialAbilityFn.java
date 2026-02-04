package it.unibo.df.model.special;

import java.util.Optional;

/**
 * similar to AbilityFn, represents
 */
@FunctionalInterface
public interface SpecialAbilityFn<T> {
    Optional<T> transform(T input);
}
