package it.unibo.df.model.arsenal;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityFn;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Default combinations.
 */
public final class DefaultCombinations {

    // Ability ids
    private static final int ABILITY_1 = 1;
    private static final int ABILITY_2 = 2;
    private static final int ABILITY_3 = 3;
    private static final int ABILITY_4 = 4;
    private static final int ABILITY_5 = 5;
    private static final int ABILITY_6 = 6;
    private static final int ABILITY_7 = 7;
    private static final int ABILITY_8 = 8;
    private static final int ABILITY_9 = 9;
    private static final int ABILITY_10 = 10;
    private static final int ABILITY_11 = 11;
    private static final int ABILITY_12 = 12;
    private static final int ABILITY_14 = 14;
    private static final int ABILITY_15 = 15;

    // Combined ids
    private static final int COMBO_100 = 100;
    private static final int COMBO_101 = 101;
    private static final int COMBO_102 = 102;
    private static final int COMBO_103 = 103;
    private static final int COMBO_104 = 104;
    private static final int COMBO_105 = 105;
    private static final int COMBO_106 = 106;
    private static final int COMBO_107 = 107;
    private static final int COMBO_108 = 108;
    private static final int COMBO_109 = 109;
    private static final int COMBO_110 = 110;
    private static final int COMBO_111 = 111;

    private DefaultCombinations() {
    }

    /**
     * Creates a combiner with default combos.
     *
     * @return combiner
     */
    public static AbilityCombiner create() {
        final AbilityCombiner c = new AbilityCombiner();

        c.register(ABILITY_1, ABILITY_2, (a, b) -> buildCombined(COMBO_100, a, b));
        c.register(ABILITY_3, ABILITY_4, (a, b) -> buildCombined(COMBO_101, a, b));
        c.register(ABILITY_3, ABILITY_7, (a, b) -> buildCombined(COMBO_102, a, b));
        c.register(ABILITY_6, ABILITY_8, (a, b) -> buildCombined(COMBO_103, a, b));
        c.register(ABILITY_11, ABILITY_14, (a, b) -> buildCombined(COMBO_104, a, b));
        c.register(ABILITY_11, ABILITY_12, (a, b) -> buildCombined(COMBO_105, a, b));
        c.register(ABILITY_14, ABILITY_15, (a, b) -> buildCombined(COMBO_106, a, b));
        c.register(ABILITY_7, ABILITY_10, (a, b) -> buildCombined(COMBO_107, a, b));
        c.register(ABILITY_3, ABILITY_9, (a, b) -> buildCombined(COMBO_108, a, b));
        c.register(ABILITY_2, ABILITY_8, (a, b) -> buildCombined(COMBO_109, a, b));
        c.register(ABILITY_4, ABILITY_7, (a, b) -> buildCombined(COMBO_110, a, b));
        c.register(ABILITY_5, ABILITY_10, (a, b) -> buildCombined(COMBO_111, a, b));

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
                a.casterHpDelta() + b.casterHpDelta(),
                a.targetHpDelta() + b.targetHpDelta(),
                fuseEffects(a.effect(), b.effect()));
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
}
