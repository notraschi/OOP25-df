package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.Controller;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Move;

class ControllerTest {
    private Controller controller;

    @BeforeEach
    void clear() {
        controller = new Controller();
    }

    @Test
    void basicCombatInput() {
        assertEquals("yo we fighting!!", ((CombatState) controller.tick()).test());
        controller.handle(Move.LEFT);
        assertEquals("left", ((CombatState) controller.tick()).test());
    }

    @Test //DELETME-PLS
    void aiTestAlVolo() {
        assertEquals("idle", ((CombatState) controller.tick()).test());
    }
}