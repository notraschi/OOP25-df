package it.unibo.df.model.arsenal;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;

/**
 * Default combinations.
 */
public final class DefaultCombinations {

    private DefaultCombinations() { }

    /**
     * Creates a combiner with default combos.
     *
     * @return combiner
     */
    public static AbilityCombiner create() {
        final AbilityCombiner c = new AbilityCombiner();

        // Ex: 1 + 2 -> new ability (id 100)
        c.register(1, 2, (a, b) -> buildCombined(100, a, b));


        return c;
    }

    /**
     * Builds a combined ability.
     *
     * @param newId id of result
     * @param a first ability
     * @param b second ability
     * @return combined ability
     */
    private static Ability buildCombined(final int newId, final Ability a, final Ability b) {
        return new Ability(
            newId,
            a.name() + "+" + b.name(),
            a.unlocked(),
            a.cooldown() + b.cooldown(),
            pickType(a, b),
            a.casterHpDelta() + b.casterHpDelta(),
            a.targetHpDelta() + b.targetHpDelta(),
            a.effect()
        );
    }

    private static AbilityType pickType(final Ability a, final Ability b) {
        if (a.type() == AbilityType.LIFESTEAL || b.type() == AbilityType.LIFESTEAL) {
            return AbilityType.LIFESTEAL;
        }
        if (a.type() == AbilityType.HEAL && b.type() == AbilityType.HEAL) {
            return AbilityType.HEAL;
        }
        return AbilityType.ATTACK;
    }
}

