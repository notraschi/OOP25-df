package it.unibo.df.controller;

import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

/**
 * arsenal state.
 */
public final class ArsenalController implements ControllerState {
	private final ArsenalState gameState = new ArsenalState();

	@Override
	public boolean handle(Input input) {
		return true;
	}

	@Override
	public GameState tick() {
		return gameState;
	}
}
