package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityEffect;
import it.unibo.df.model.abilities.AbilityFn;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Small tests to ensure the abilities model works.
 */
class AbilityTest {

    @Test
    void abilityAndEffectAreCreated() {
        AbilityFn fn = (gs, caster, target) -> new AbilityEffect(-5, 0, Optional.empty());

        Ability a = new Ability(1, "Attack", 3, AbilityType.ATTACK, 5, 0, fn);

        assertEquals(1  , a.id());
        assertNotNull(a.effect());
    }

    @Test
    void lifestealProducesDamageAndHeal() {
        AbilityFn lifesteal = (gs, caster, target) -> new AbilityEffect(-8, +4, Optional.empty());

        AbilityEffect e = lifesteal.apply(new Object(), new Object(), new Vec2D(1, 2));

        assertEquals(-8, e.targetHpDelta());
        assertEquals(4, e.casterHpDelta());
    }
}

    
