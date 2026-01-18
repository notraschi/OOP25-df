package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityRegistry;

/**
 * Test class for AbilityRegistry.
 */
final class AbilityRegistryTest {

    @Test
    void testGetAbilityById() {
        final AbilityRegistry registry = new AbilityRegistry();
        final int testId = 1; // Assuming 1 is a valid ability ID in abilities.yml  
        final Ability ability = registry.get(testId);
        assertNotNull(ability, "Ability should not be null");
        assertEquals("Slash",ability.name());
        assertEquals(3, registry.size());

    }
}
