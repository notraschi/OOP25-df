package it.unibo.df;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import it.unibo.df.configurations.GameConfig;
import it.unibo.df.controller.CombatController;
import it.unibo.df.controller.Controller;
import it.unibo.df.controller.Progress;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Equip;
import it.unibo.df.model.combat.CombatModel;

final class UnlockAbilitiesTest {
    private static final int ABILITY_1 = 1;
    private static final int ABILITY_2 = 2;
    private static final int ABILITY_3 = 3;
    private static final int ENEMIES_IN_BATTLE = 2;
    private static final int KILLED_ENEMIES = 1;
    private static final int UNLOCKED_AFTER_KILL = 6;
    private static final int DEFAULT_UNLOCKED = 5;
    private static final int UPDATE_COUNT = 4;
    private static final int UNLOCKED_AFTER_UPDATE = 9;
    private static final int TOTAL_ABILITIES = 16;
    private static final int KILL_HP = 0;

    private Controller controller;
    private Progress progress;
    private CombatModel model;

    void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        controller = new Controller(GameConfig.defaultConfig());
        controller.resetProgress();

        controller.handle(new Equip(ABILITY_1));
        controller.handle(new Equip(ABILITY_2));
        controller.handle(new Equip(ABILITY_3));
        // gets Progress
        final var progressField = controller.getClass().getDeclaredField("progress");
        progressField.setAccessible(true);
        progress = (Progress) progressField.get(controller);

        controller.toBattle();
        // gets controller state (CombatController)
        final var stateControllerField = controller.getClass().getDeclaredField("state");
        stateControllerField.setAccessible(true);
        final var state = (CombatController) stateControllerField.get(controller);
        final var modelField = state.getClass().getDeclaredField("model");
        modelField.setAccessible(true);
        model = (CombatModel) modelField.get(state);

        assertEquals(ENEMIES_IN_BATTLE, ((CombatState) state.tick(0)).enemies().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void unlockAbility() throws NoSuchFieldException, IllegalAccessException {
        setup();
        assertEquals(0, model.getKilledEnemies());

        final var enemyField = model.getClass().getDeclaredField("enemies");
        enemyField.setAccessible(true);
        final var enemies = (Map<Integer, ?>) enemyField.get(model);
        // get first enemy
        final var enemy = enemies.values().iterator().next();
        final var hpField = enemy.getClass().getDeclaredField("hp");
        hpField.setAccessible(true);
        // artificially killing the enemy
        hpField.setInt(enemy, KILL_HP);

        assertEquals(KILLED_ENEMIES, model.getKilledEnemies());

        controller.toArsenal();

        assertEquals(UNLOCKED_AFTER_KILL, progress.unlockedAbilities().size());

        controller.saveOnClose();
    }

    @Test
    void resetGame() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        setup();
        controller.toArsenal();

        controller.resetProgress();
        assertEquals(DEFAULT_UNLOCKED, progress.unlockedAbilities().size());
        progress.update(UPDATE_COUNT);
        assertEquals(UNLOCKED_AFTER_UPDATE, progress.unlockedAbilities().size());
        controller.saveOnClose();
        // new game
        controller = new Controller(GameConfig.testingConfig());
        // gets Progress
        final var progressField = controller.getClass().getDeclaredField("progress");
        progressField.setAccessible(true);
        progress = (Progress) progressField.get(controller);
        // cleanup
        assertEquals(UNLOCKED_AFTER_UPDATE, progress.unlockedAbilities().size());
        controller.resetProgress();
        controller.saveOnClose();
    }

    @Test
    void getAllAbilities() {
        assertEquals(TOTAL_ABILITIES, Progress.allRegisteredAbilities().size());
    }
}
