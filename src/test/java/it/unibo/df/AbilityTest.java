package it.unibo.df;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityFn;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Small tests to ensure the abilities model works.
 */
class AbilityTest {

    @Test
    void abilityAndEffectAreCreated() {
        AbilityFn fn = (caster) -> Optional.empty();
        Ability a = new Ability(1, "BasicHeal", 3, 5, 0, fn);

        assertEquals(1, a.id());
        assertNotNull(a.effect());
    }

    @Test
    void lifestealProducesDamageAndHeal() {
        Ability a = new Ability(
                2,
                "BasicLifeSteal",
                10,
                4,
                -8,
                (caster) -> Optional.of(Set.of(new Vec2D(1, 1))));

        assertEquals(-8, a.targetHpDelta());
        assertEquals(4, a.casterHpDelta());
    }
}
