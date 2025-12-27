package it.unibo.df.controller;

import it.unibo.df.input.Input;

/**
 * this is a interface that allows a single controller to switch behavior.
 */
public sealed interface ControllerState permits ArsenalState, CombatState {

	/**
	 * this function handles user input.
	 *
	 * @param input is the user's input
	 * @return wether the input was handled or rejected
	 */
	boolean handle(Input input);

	/**
	 * this function updates the game's state, and should return a new GameState.
	 */
	void tick();
}

