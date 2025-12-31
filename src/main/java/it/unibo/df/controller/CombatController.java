package it.unibo.df.controller;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.ai.IdleStrategy;
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
	private final AiController aiController = new AiControllerBuilder().add(new IdleStrategy()).build();

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
			case Move.IDLE -> gameState.setTest("idle"); //MODIFY-ME
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
		handle(aiController.computeNextInput(gameState)); //DELETME
		return gameState;
	}
}
