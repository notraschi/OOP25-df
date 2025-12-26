package it.unibo.df.controller;

/** 
 * this is the sole (and therfore main) controller.
 * uses: state pattern, (strategy pattern obv), inversion of control
 */
public final class Controller {
	private ControllerState state;

	/**
	 * handles user input.
	 */
	public void hadle(/*input*/) {
		state.handle(/*input*/);
	}

	/**
	 * updates game's state.
	 */
	public void tick() {
		// return
		state.tick();
	}
}

