package it.unibo.df.controller;

import it.unibo.df.input.Input;

/** 
 * this is the sole (and therfore main) controller.
 * uses: state pattern, (strategy pattern obv), inversion of control
 */
public final class Controller {
	private ControllerState state;

	/**
	 * handles user input.
	 * 
	 * @param input the input to handle
	 * @return wether the input was handled or rejected
	 */
	public boolean hadle(Input input) {
		return state.handle(input);
	}

	/**
	 * updates game's state.
	 */
	public void tick() {
		// return
		state.tick();
	}
}

