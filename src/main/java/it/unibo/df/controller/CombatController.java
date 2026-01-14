package it.unibo.df.controller;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.ai.IdleStrategy;
import it.unibo.df.gs.CombatState;
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
	private final List<Set<Vec2D>> effects = new LinkedList<>();

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
		return model.move(entityId, delta);
	}

	/**
	 * handles attack-related input
	 * 
	 * @param ability the ability performed
	 * @return true if input was handled
	 */
	private boolean handleAttack(Optional<Integer> entityId, Attack ability) {
		model.cast(entityId, ability.ordinal()).ifPresent(affected -> effects.add(affected));
		return true;
	}

	/**
	 * {@inheritDoc }
	 */
	/*
	explaination of the way controller builds GameState, and patterns used:
	1- controller build GameState (as opposed to model doing it) because i need to have view to know *when* abilites were cast
	1.1 - otherwise controller or model should have a timer to check if an effect is still visible (wrong!)
	2- model exposes methods to get minimal info (playerPos & enemyPos)
	3- IMPORTANT: controller.effects (== ComabatState.effects) contain the cells affected from abilities since last tick()!!
	3.1 - this pattern is called **frame-scoped event buffering**.
	3.2 - view should set up its own timers when it recieves new effects, and make old effects disapper according to said timers
	*/
	@Override
	public GameState tick() {
		aiController.computeNextInput(null).ifPresent(i -> {
			switch ((CombatInput) i) {
				case Move moveAction -> handleMove(Optional.of(1), moveAction);
				case Attack attackAction -> handleAttack(Optional.of(1), attackAction);
			}
		});
		var state = new CombatState(
			model.playerPos(),
			model.enemyPos(),
			Collections.unmodifiableList(effects)
		);
		effects.clear(); // now we're ready for new effects happening
		return state;
	}
}
