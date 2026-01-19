package it.unibo.df.controller;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

/** 
 * this is the sole (and therfore main) controller.
 * uses: state pattern, (strategy pattern obv), inversion of control
 */
public final class Controller {
	private ControllerState state = new ArsenalController();

	/**
	 * handles user input.
	 * 
	 * @param input the input to handle
	 * @return wether the input was handled or rejected
	 */
	public boolean handle(Input input) {
		return state.handle(input);
	}

	/**
	 * updates game's state.
	 * 
	 * @return the new game state
	 */
	public GameState tick() {
		return state.tick();
	}

	/**
	 * sets up the battle-phase
	 */
	public void toBattle() {
		if (state instanceof ArsenalController arsenalController) {
			var loadout = arsenalController.currentLoadout();
			state = new CombatController(loadout);
		} else {
			throw new IllegalStateException("already in battle");
		}
	}
}

