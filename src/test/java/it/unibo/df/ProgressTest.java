package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.unibo.df.controller.Progress;
import it.unibo.df.model.abilities.Ability;

/**
 * Test class for AbilityRegistry.
 */
final class ProgressTest {

    @Test
    void testGetAbilityById() {
        final Progress registry = new Progress();
        final int testId = 1; // Assuming 1 is a valid ability ID in abilities.yml  
        final Ability ability = registry.get(testId);
        assertNotNull(ability, "Ability should not be null");
        assertEquals("Slash",ability.name());
        assertEquals(3, registry.size());

    }
}
