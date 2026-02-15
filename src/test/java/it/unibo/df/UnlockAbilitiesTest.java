package it.unibo.df;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.configurations.GameConfig;
import it.unibo.df.controller.CombatController;
import it.unibo.df.controller.Controller;
import it.unibo.df.controller.Progress;
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

    @BeforeEach
    void setUp() {
        try {
            final var config = GameConfig.testingWithEnemiesConfig();
            progress = config.progress();
            controller = new Controller(config);
            controller.resetProgress();

            controller.handle(new Equip(ABILITY_1));
            controller.handle(new Equip(ABILITY_2));
            controller.handle(new Equip(ABILITY_3));

            controller.enterBattle();
            // gets controller state (CombatController)
            final var stateControllerField = controller.getClass().getDeclaredField("state");
            stateControllerField.setAccessible(true); // NOPMD: necessary to access model
            final var state = (CombatController) stateControllerField.get(controller);
            final var modelField = state.getClass().getDeclaredField("model");
            modelField.setAccessible(true); // NOPMD
            model = (CombatModel) modelField.get(state);

            assertEquals(ENEMIES_IN_BATTLE, state.tick(0).enemies().size());
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            System.getLogger(UnlockAbilitiesTest.class.getName()).log(System.Logger.Level.ERROR, "error loading model", ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void unlockAbility() {
        try {
            setUp();
            assertEquals(0, model.getKilledEnemies());

            final var enemyField = model.getClass().getDeclaredField("enemies");
            enemyField.setAccessible(true); // NOPMD: necessary to kill enemy
            final var enemies = (Map<Integer, ?>) enemyField.get(model);
            // get first enemy
            final var enemy = enemies.values().iterator().next();
            final var hpField = enemy.getClass().getDeclaredField("hp");
            hpField.setAccessible(true); // NOPMD: this is an inner class
            // artificially killing the enemy
            hpField.setInt(enemy, KILL_HP);

            assertEquals(KILLED_ENEMIES, model.getKilledEnemies());

            controller.enterArsenal();

            assertEquals(UNLOCKED_AFTER_KILL, progress.unlockedAbilities().size());

            controller.saveOnClose();
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            System.getLogger(UnlockAbilitiesTest.class.getName()).log(System.Logger.Level.ERROR, "error killing an enemy", ex);
        }
    }

    @Test
    void resetGame() {
        setUp();
        controller.enterArsenal();

        controller.resetProgress();
        assertEquals(DEFAULT_UNLOCKED, progress.unlockedAbilities().size());
        progress.update(UPDATE_COUNT);
        assertEquals(UNLOCKED_AFTER_UPDATE, progress.unlockedAbilities().size());
        controller.saveOnClose();
        // new game
        final var config = GameConfig.defaultConfig();
        progress = config.progress();
        controller = new Controller(config);
        // cleanup
        assertEquals(UNLOCKED_AFTER_UPDATE, progress.unlockedAbilities().size());
        controller.resetProgress();
        controller.saveOnClose();
    }

    @Test
    void allRegisteredAbilitiesTest() {
        assertEquals(TOTAL_ABILITIES, Progress.allRegisteredAbilities().size());
    }
}
