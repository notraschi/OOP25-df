package it.unibo.df.controller;

import it.unibo.df.input.Input;

/**
 * arsenal state.
 */
public final class ArsenalState implements ControllerState {
	@Override
	public boolean handle(Input input) {
		return true;
	}

	@Override
	public void tick() {
		return;
	}
}

