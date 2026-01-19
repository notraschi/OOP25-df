package it.unibo.df;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.Controller;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Test class for AbilityRegistry.
 */
final class ControllerTest {
    private Controller controller;

    @BeforeEach
    void setup() {
        // ArsenalController
        controller = new Controller();
    }

    @Test
    void testToBattle() {
        // a valid move id is 1 for example.
        // equip ability with id 1
        assertTrue(controller.handle(new Equip(1)));
        // switches controller to CombatController
        controller.toBattle();
        // perform ability
        controller.handle(Attack.ABILITY1);
        // check for new state update...
        var gs = (CombatState) controller.tick();
        assertNotNull(gs);
        assertEquals(new Vec2D(0, 0), gs.playerPos());
        assertEquals(1, gs.effects().size());
        assertEquals(4, gs.effects().get(0).size());
        assertEquals( // the effect i cased is supposed to hit these four positions
            Set.of(
                new Vec2D(+1, 0),
                new Vec2D(-1, 0), // some are out of bounds but its ok
                new Vec2D(0, +1),
                new Vec2D(0, -1)
            ),
            gs.effects().get(0)
        );
        // move the player
        assertTrue(controller.handle(Move.DOWN));
        gs = (CombatState) controller.tick();
        assertEquals(0, gs.effects().size()); // no new effects to display
        assertEquals(new Vec2D(0, 1), gs.playerPos());
        // move the player out of bounds
        assertFalse(controller.handle(Move.LEFT));
    }
}
