package it.unibo.df.controller;

/**
 * this is a interface that allows a single controller to switch behavior.
 */
public sealed interface ControllerState permits ArsenalState, CombatState {

	/**
	 * this function handles user input.
	 */
	void handle(/*input*/);

	/**
	 * this function updates the game's state, and should return a new GameState.
	 */
	void tick();
}

