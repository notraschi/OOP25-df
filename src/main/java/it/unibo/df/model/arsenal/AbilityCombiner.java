package it.unibo.df.model.arsenal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import it.unibo.df.model.abilities.Ability;

/**
 * Combines abilities using registered recipes.
 */
public final class AbilityCombiner {

    private final Map<AbilityPair, BiFunction<Ability, Ability, Ability>> map = new HashMap<>();

    /**
     * Registers a combination recipe.
     *
     * @param firstId first ability id
     * @param secondId second ability id
     * @param recipe combination function
     */
    public void register(
        final int firstId,
        final int secondId,
        final BiFunction<Ability, Ability, Ability> recipe
    ) {
        map.put(AbilityPair.of(firstId, secondId), recipe);
    }

    /**
     * Combines two abilities if possible.
     *
     * @param first first ability
     * @param second second ability
     * @return combined ability if present
     */
    public Optional<Ability> combine(final Ability first, final Ability second) {
        final var recipe = map.get(AbilityPair.of(first.id(), second.id()));
        if (recipe == null) {
            return Optional.empty();
        }
        return Optional.of(recipe.apply(first, second));
    }
}

