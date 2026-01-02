package it.unibo.df.controller;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.ai.IdleStrategy;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.CombatInput;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.combat.CombatModel;

/**
 * combat state.
 */
public final class CombatController implements ControllerState {
	private final AiController aiController = new AiControllerBuilder().add(new IdleStrategy()).build();
	private final CombatModel model = new CombatModel();

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
		return true;
	}

	/**
	 * handles attack-related input
	 * 
	 * @param ability the ability performed
	 * @return true if input was handled
	 */
	private boolean handleAttack(Attack ability) {
		return true;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public GameState tick() {
		return null;
	}
}
