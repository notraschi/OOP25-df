package it.unibo.df;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.ArsenalController;
import it.unibo.df.controller.Progress;
import it.unibo.df.dto.AbilityView;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Unequip;
import it.unibo.df.model.abilities.Ability;

/**
 * Small tests to ensure the abilities model works.
 */
class ArsenalTest {
    private final Map<Integer, Ability> arsenal = Progress.allRegisteredAbilities();
    private ArsenalController controller;

    @BeforeEach
    void setup() {
        controller = new ArsenalController(arsenal);
    }

    @Test
    void setupTest() {
        var gs = controller.tick(0);
        assertEquals(arsenal.size(), gs.unlocked().size());
        assertTrue(controller.handle(new Equip(1)));
        gs = controller.tick(0);
        assertEquals(0, gs.unlocked().size());
    }

    @Test
    void equipTest() {
        assertTrue(controller.handle(new Equip(1)));
        var gs = controller.tick(0);
        assertEquals(1, gs.equipped().get());
        // equipping it again should not work
        assertFalse(controller.handle(new Equip(1)));
        gs = controller.tick(0);
        assertEquals(Optional.empty(), gs.equipped());
        // equipping weird stuff should not work
        assertFalse(controller.handle(new Equip(-1)));
    }

    @Test
    void combineTest() {
        var gs = controller.tick(0);
        assertEquals(arsenal.size(), gs.unlocked().size());
        // lets combine two abilities
        assertTrue(controller.handle(new Combine(1, 2)));
        gs = controller.tick(0);
        assertEquals(List.of(1, 2), gs.lost());
        assertEquals(List.of(100), gs.unlocked().stream().map(AbilityView::id).toList());
        // both lost abilities shouldnt be equippable
        assertFalse(controller.handle(new Equip(1)));
        assertFalse(controller.handle(new Equip(2)));
        // combined should be equippable (once)
        assertTrue(controller.handle(new Equip(100)));
        assertFalse(controller.handle(new Equip(100)));
        // cannot combine lost abilities
        assertFalse(controller.handle(new Combine(1, 2)));
    }

    @Test
    void unequipTest() {
        assertFalse(controller.handle(new Unequip(-1)));
        assertFalse(controller.handle(new Unequip(1)));
        assertTrue(controller.handle(new Equip(1)));
        // check i cannot combine using an equipped ability
        assertFalse(controller.handle(new Combine(1, 2)));
        assertTrue(controller.handle(new Unequip(1)));
        // now unequip combined abilities
        assertTrue(controller.handle(new Combine(1, 2)));
        assertFalse(controller.handle(new Unequip(100)));
        assertTrue(controller.handle(new Equip(100)));
        controller.tick(0); // flush
        assertTrue(controller.handle(new Unequip(100)));
        final var gs = controller.tick(0);
        assertEquals(100, gs.unequipped().get());
    }
}
