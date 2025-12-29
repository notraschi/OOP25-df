package it.unibo.df.controller;

import it.unibo.df.gs.CombatState;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.CombatInput;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;

/**
 * combat state.
 */
public final class CombatController implements ControllerState {
	private final CombatState gameState = new CombatState();

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean handle(Input input) { //TODO
		boolean handled;
		switch (input) {
			case CombatInput action -> {
				handled = true;
				switch (action) {
					case Move moveAction -> handleMove(moveAction);
					case Attack attackAction -> handleAttack(attackAction);
				}
			}
			default -> handled = false;
		}
		return handled;
	}

	/**
	 * handles move-related input
	 * 
	 * @param direction the direction to move towards
	 * @return true if input was handled
	 */
	private boolean handleMove(Move direction) { //TODO
		switch (direction) {
			case Move.UP -> gameState.setTest("up");
			case Move.DOWN -> gameState.setTest("down");
			case Move.RIGHT -> gameState.setTest("right");
			case Move.LEFT -> gameState.setTest("left");
		}
		return true;
	}

	/**
	 * handles attack-related input
	 * 
	 * @param ability the ability performed
	 * @return true if input was handled
	 */
	private boolean handleAttack(Attack ability) { //TODO
		System.out.println(ability.ordinal()); 
		return true;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public GameState tick() {
		return gameState;
	}
}
