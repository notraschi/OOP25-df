package it.unibo.df.controller;

import java.util.Optional;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.ai.IdleStrategy;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.CombatInput;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Vec2D;
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
	public boolean handle(Input input) {
		boolean handled;
		switch (input) {
			case CombatInput action -> {
				handled = true;
				switch (action) {
					case Move moveAction -> handleMove(Optional.empty(), moveAction);
					case Attack attackAction -> handleAttack(Optional.empty(), attackAction);
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
	private boolean handleMove(Optional<Integer> entityId, Move direction) { //TODO
		Vec2D delta;
		switch (direction) {
			case Move.UP -> delta = new Vec2D(0, -1);
			case Move.DOWN -> delta = new Vec2D(0, 1);
			case Move.LEFT -> delta = new Vec2D(-1, 0);
			case Move.RIGHT -> delta = new Vec2D(1, 0);
			default -> delta = new Vec2D(0, 0);
		}
		model.move(entityId, delta);
		return true;
	}

	/**
	 * handles attack-related input
	 * 
	 * @param ability the ability performed
	 * @return true if input was handled
	 */
	private boolean handleAttack(Optional<Integer> entityId, Attack ability) {
		model.cast(entityId, ability.ordinal());
		return true;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public GameState tick() {
		aiController.computeNextInput(null).ifPresent(i -> {
			switch ((CombatInput) i) {
				case Move moveAction -> handleMove(Optional.of(1), moveAction);
				case Attack attackAction -> handleAttack(Optional.of(1), attackAction);
			}
		});
		return null;
	}
}
