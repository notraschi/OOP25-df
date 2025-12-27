package it.unibo.df.controller;

import it.unibo.df.input.Input;

/**
 * combat state.
 */
public final class CombatState implements ControllerState {
	@Override
	public boolean handle(Input input) {
		return true;
	}

	@Override
	public void tick() {
		return;
	}
}

