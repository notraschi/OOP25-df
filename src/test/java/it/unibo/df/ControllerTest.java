package it.unibo.df;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.Controller;
import it.unibo.df.controller.GameConfig;
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

    @Test
    void completeTest() {
        controller = new Controller(GameConfig.testingConfig());

        // a valid move id is 1 for example.
        assertTrue(controller.handle(new Equip(1)));
        assertTrue(controller.handle(new Equip(2)));
        // cannot go to battle right now, loadout isn't full yet!
        var ex = assertThrows(IllegalStateException.class, () -> controller.toBattle());
        assertNotNull(ex);

        assertTrue(controller.handle(new Equip(3)));
        // cannot insert any more abilities
        assertFalse(controller.handle(new Equip(1)));
        // cannot insert unknown abilities
        assertFalse(controller.handle(new Equip(-1)));

        // switches controller to CombatController
        controller.toBattle();
        // perform ability
        controller.handle(Attack.ABILITY1);
        // check for new state update...
        var gs = (CombatState) controller.tick(0);
        assertNotNull(gs);
        assertEquals(new Vec2D(0, 0), gs.player().position());
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
        gs = (CombatState) controller.tick(0);
        assertEquals(0, gs.effects().size()); // no new effects to display
        assertEquals(new Vec2D(0, 1), gs.player().position());
        // move the player out of bounds
        assertFalse(controller.handle(Move.LEFT));
    }
}
