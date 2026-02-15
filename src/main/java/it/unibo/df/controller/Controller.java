package it.unibo.df.controller;

import it.unibo.df.configurations.GameConfig;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

/**
 * this is the sole (and therfore main) controller.
 * uses: state pattern, (strategy pattern obv), inversion of control
 */
public final class Controller {
    // private final Progress progress;
    private final GameConfig config;
    private ControllerState state;

    /**
     * creates the main controller.
     * 
     * @param configuration game configuration
     */
    public Controller(final GameConfig configuration) {
        // progress = configuration.progress();
        config = configuration;
        state = new ArsenalController(config.progress().unlockedAbilities());
    }

    /**
     * handles user input.
     * 
     * @param input the input to handle
     * @return wether the input was handled or rejected
     */
    public boolean handle(final Input input) {
        return state.handle(input);
    }

    /**
     * updates game's state.
     * 
     * @param deltaTime time passed since last tick (milliseconds)
     * @return the new game state
     */
    public GameState tick(final long deltaTime) {
        return state.tick(deltaTime);
    }

    /**
     * sets up the battle-phase.
     */
    public void enterBattle() {
        if (state instanceof ArsenalController arsenalController) {
            final var loadout = arsenalController.currentLoadout();
            if (loadout.size() != 3) {
                throw new IllegalStateException("going to the battle unprepared isn't wise...");
            }
            state = new CombatController(loadout, config.numberOfEnemies());
        } else {
            throw new IllegalStateException("already in battle");
        }
    }

    /**
     * sets up the arsenal phase.
     */
    public void enterArsenal() {
        if (state instanceof CombatController combatController) {
            config.progress().update(combatController.killedEnemies());
            state = new ArsenalController(config.progress().unlockedAbilities());
        } else {
            throw new IllegalStateException("already in arsenal");
        }
    }

    /**
     * resets the player progress.
     */
    public void resetProgress() {
        config.progress().reset();
    }

    /**
     * saves progress on application close.
     */
    public void saveOnClose() {
        config.progress().write();
    }
}
