package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import it.unibo.df.configurations.GameConfig;
import it.unibo.df.controller.Controller;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Equip;

/**
 * simple and dumb test to show cooldowns work.
 */
final class CooldownTest {
    private static final int WAIT_MS = 2000;

    @Test
    void completeTest() {
        final Controller controller = new Controller(GameConfig.testingConfig()); // now in arsenal mode

        assertTrue(controller.handle(new Equip(1)));
        assertTrue(controller.handle(new Equip(3)));
        assertTrue(controller.handle(new Equip(2)));

        controller.toBattle();
        assertTrue(controller.handle(Attack.ABILITY1));
        var gs = (CombatState) controller.tick(1000); // timer starts
        assertEquals(1, gs.effects().size());

        // NOTE: if the move isnt available, input is still handled correctly.
        assertTrue(controller.handle(Attack.ABILITY1)); // spam
        gs = (CombatState) controller.tick(1000); // too early
        assertEquals(0, gs.effects().size());

        controller.tick(WAIT_MS);
        controller.handle(Attack.ABILITY1);

        gs = (CombatState) controller.tick(0);
        assertEquals(1, gs.effects().size());
    }
}
