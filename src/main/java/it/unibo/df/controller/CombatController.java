package it.unibo.df.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.gs.CombatState;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.CombatInput;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.CombatModel;
import it.unibo.df.model.combat.EnemyDefinition;
import it.unibo.df.model.combat.EnemyFactory;

/**
 * combat state.
 */
public final class CombatController implements ControllerState {
	//private final AiController aiController;
	private final Map<Integer,AiController> aiControllers = new HashMap<>();
	private final CombatModel model;
	private final List<Set<Vec2D>> effects;
	private CombatState state;

    public CombatController(List<Ability> loadout) {
		model = new CombatModel(loadout);
		spawnEnemy(EnemyFactory.basicEnemy(new Vec2D(3, 3)));
		effects = new LinkedList<>();
		state = buildState();
    }

	private void spawnEnemy(EnemyDefinition enemy) {
		int id = model.addEnemy(enemy);
		var aiBuilder = new AiControllerBuilder(id,enemy.special()).setLoadout(enemy.loadout());
		enemy.strategies().stream().forEach(s -> aiBuilder.add(s));
		aiControllers.put(id, aiBuilder.build());
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean handle(Input input) {
		return switch (input) {
			case CombatInput action -> 
				switch (action) {
					case Move moveAction -> handleMove(Optional.empty(), moveAction);
					case Attack attackAction -> handleAttack(Optional.empty(), attackAction);
				};
			default -> false;
		};
	}

	/**
	 * handles move-related input
	 * 
	 * @param direction the direction to move towards
	 * @return true if input was handled
	 */
	private boolean handleMove(Optional<Integer> entityId, Move direction) {
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

	private CombatState buildState() {
		return new CombatState(
			model.playerView(),
			model.enemyView(),
			List.copyOf(effects),
			model.getDisrupt()
		);
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
	public GameState tick(long deltaTime) {
		model.tick(deltaTime);
		aiControllers.entrySet().stream()
			.forEach(e -> e.getValue().computeNextInput(state).ifPresent(in -> {
				switch ((CombatInput) in) {
					case Move moveAction -> handleMove(Optional.of(e.getKey()), moveAction);
					case Attack attackAction -> handleAttack(Optional.of(e.getKey()), attackAction);
				}
			}
		));
		state = buildState(); 
		effects.clear(); // now we're ready for new effects happening
		return state;
	}
}
