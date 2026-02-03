package it.unibo.df.controller;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

/**
 * this is a interface that allows a single controller to switch behavior.
 */
public sealed interface ControllerState permits ArsenalController, CombatController {

	/**
	 * handles user input.
	 *
	 * @param input is the user's input
	 * @return wether the input was handled or rejected
	 */
	boolean handle(Input input);

	/**
	 * updates the game's state.
	 * 
	 * @param deltaTime in millisecs, time passed since last tick
	 * @return the current game state
	 */
	GameState tick(long deltaTime);
}

