package it.unibo.df;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.CombatController;
import it.unibo.df.controller.Controller;
import it.unibo.df.controller.Progress;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Equip;
import it.unibo.df.model.combat.CombatModel;

final class UnlockAbilitiesTest {
    private Controller controller;
    private Progress progress;
    private CombatModel model; 

    void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        controller = new Controller();
        controller.handle(new Equip(1));
        controller.handle(new Equip(2));
        controller.handle(new Equip(3));
        // gets Progress
        var progressField = controller.getClass().getDeclaredField("progress");
        progressField.setAccessible(true);
        progress = (Progress) progressField.get(controller);

        controller.toBattle();
        // gets controller state (CombatController)
        var stateControllerField = controller.getClass().getDeclaredField("state");
        stateControllerField.setAccessible(true);
        var state = (CombatController) stateControllerField.get(controller);
        var modelField = state.getClass().getDeclaredField("model");
        modelField.setAccessible(true);
        model = (CombatModel) modelField.get(state);

        assertEquals(1, ((CombatState) state.tick(0)).enemies().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void unlockAbility() throws NoSuchFieldException, IllegalAccessException {
        setup();
        assertEquals(0, model.getKilledEnemies());

        var enemyField = model.getClass().getDeclaredField("enemies");
        enemyField.setAccessible(true);
        var enemies = (Map<Integer, ?>) enemyField.get(model);
        // get first enemy
        var enemy = enemies.values().iterator().next();
        var hpField = enemy.getClass().getDeclaredField("hp");
        hpField.setAccessible(true);
        // artificially killing the enemy
        hpField.setInt(enemy, 0);

        assertEquals(1, model.getKilledEnemies());
        
        controller.toArsenal();

        assertEquals(5, progress.unlockedAbilities().size());
        
        controller.saveOnClose();
    }

    @Test
    void resetGame() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        setup();
        assertEquals(4, progress.unlockedAbilities().size());
        unlockAbility();
        assertEquals(5, progress.unlockedAbilities().size());
        controller.resetProgress();
        controller.saveOnClose();
        setup();
        assertEquals(4, progress.unlockedAbilities().size());
    }

    @Test
    void getAllAbilities() {
        assertEquals(16, Progress.allRegisteredAbilities().size());
    }
}
