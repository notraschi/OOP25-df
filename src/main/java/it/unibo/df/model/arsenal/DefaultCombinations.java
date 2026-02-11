package it.unibo.df.model.arsenal;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityFn;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Default combinations.
 */
public final class DefaultCombinations {

    private DefaultCombinations() {
    }

    /**
     * Creates a combiner with default combos.
     *
     * @return combiner
     */
    public static AbilityCombiner create() {
        final AbilityCombiner c = new AbilityCombiner();

        // Ex: 1 + 2 -> new ability (id 100)
        c.register(1, 2, (a, b) -> buildCombined(100, a, b));
        c.register(3, 4, (a, b) -> buildCombined(101, a, b));
        c.register(3, 7, (a, b) -> buildCombined(101, a, b));
        c.register(6, 8, (a, b) -> buildCombined(101, a, b));
        c.register(11, 14, (a, b) -> buildCombined(101, a, b));
        c.register(11, 12, (a, b) -> buildCombined(101, a, b));
        c.register(14, 15, (a, b) -> buildCombined(101, a, b));
        c.register(7, 10, (a, b) -> buildCombined(101, a, b));
        c.register(3, 9, (a, b) -> buildCombined(101, a, b));
        c.register(2, 8, (a, b) -> buildCombined(101, a, b));
        c.register(4, 7, (a, b) -> buildCombined(101, a, b));
        c.register(5, 10, (a, b) -> buildCombined(101, a, b));

        return c;
    }

    /**
     * Builds a combined ability.
     *
     * @param newId id of result
     * @param a     first ability
     * @param b     second ability
     * @return combined ability
     */
    private static Ability buildCombined(final int newId, final Ability a, final Ability b) {
        return new Ability(
            newId,
            a.name() + "+" + b.name(),
            a.cooldown() + b.cooldown(),
            pickType(a, b),
            a.casterHpDelta() + b.casterHpDelta(),
            a.targetHpDelta() + b.targetHpDelta(),
            fuseEffects(a.effect(), b.effect())
        );
    }

    private static AbilityFn fuseEffects(final AbilityFn first, final AbilityFn second) {
        return caster -> {
            final Optional<Set<Vec2D>> areaA = first.apply(caster);
            final Optional<Set<Vec2D>> areaB = second.apply(caster);
            if (areaA.isEmpty() && areaB.isEmpty()) {
                return Optional.empty();
            }
            final Set<Vec2D> merged = new HashSet<>();
            areaA.ifPresent(merged::addAll);
            areaB.ifPresent(merged::addAll);
            return Optional.of(merged);
        };
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
