package it.unibo.df.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.df.ai.AiController;
import it.unibo.df.ai.AiControllerBuilder;
import it.unibo.df.configurations.Constants;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.CombatInput;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.combat.CombatModel;
import it.unibo.df.model.combat.EnemyDefinition;
import it.unibo.df.model.combat.EnemyFactory;
import it.unibo.df.utility.Vec2D;

/**
 * combat state.
 */
public final class CombatController implements ControllerState {
    private final Map<Integer, AiController> aiControllers = new HashMap<>();
    private final CombatModel model;
    private final List<Set<Vec2D>> effects;
    private CombatState state;

    /**
     * creates a combat controller loadout and enemy count.
     *
     * @param loadout abilities
     * @param numberOfEnemies number of enemies
     */
    public CombatController(final List<Ability> loadout, final int numberOfEnemies) {
        if (numberOfEnemies < 0 || numberOfEnemies > EnemyFactory.AVAILABLE_ENEMY_TYPES) {
            throw new IllegalArgumentException("illegal number of enemies");
        }
        model = new CombatModel(loadout);

        final List<Vec2D> availablePoints = generateSpawnPoints(numberOfEnemies);

        final List<Integer> enemieIdx = IntStream
                .range(0, EnemyFactory.AVAILABLE_ENEMY_TYPES)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(enemieIdx);

        IntStream.range(0, numberOfEnemies).forEach(i -> {
            spawnEnemy(EnemyFactory.createByIndex(enemieIdx.get(i), availablePoints.get(i)));
        });

        effects = new LinkedList<>();
        state = buildState();
    }

    private void spawnEnemy(final EnemyDefinition enemy) {
        final int id = model.addEnemy(enemy);
        final var aiBuilder = new AiControllerBuilder(id).setLoadout(enemy.loadout());
        enemy.strategies().stream().forEach(s -> aiBuilder.add(s));
        aiControllers.put(id, aiBuilder.build());
    }

    private List<Vec2D> generateSpawnPoints(final int n) {
        final Random rand = new Random();
        final Set<Vec2D> points = new HashSet<>();

        while (points.size() < n) {
            points.add(new Vec2D(
                    rand.nextInt(3, Constants.BOARD_SIZE),
                rand.nextInt(3, Constants.BOARD_SIZE))
            );
        }
        return new ArrayList<>(points);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean handle(final Input input) {
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
     * handles move-related input.
     * 
     * @param direction the direction to move towards
     * @param entityId of mover
     * @return true if input was handled
     */
    private boolean handleMove(final Optional<Integer> entityId, final Move direction) {
        final Vec2D delta;
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
     * handles attack-related input.
     * 
     * @param ability the ability performed
     * @param entityId of performer
     * @return true if input was handled
     */
    private boolean handleAttack(final Optional<Integer> entityId, final Attack ability) {
        if (ability.equals(Attack.SPECIAL)) {
            model.castSpecial(
                entityId.orElseThrow(() -> new IllegalArgumentException("player cant special"))
            );
        } else {
            model.cast(entityId, ability.ordinal()).ifPresent(affected -> effects.add(affected));
        }
        return true;
    }

    private CombatState buildState() {
        return new CombatState(
                model.playerView(),
                model.enemyView(),
                List.copyOf(effects),
            model.isDisruptActive()
        );
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public CombatState tick(final long deltaTime) {
        model.tick(deltaTime);
        aiControllers.entrySet().stream()
                .filter(e -> model.isEnemyAlive(e.getKey()))
                .forEach(e -> e.getValue().computeNextInput(state).ifPresent(in -> {
                    switch ((CombatInput) in) {
                        case Move moveAction -> handleMove(Optional.of(e.getKey()), moveAction);
                        case Attack attackAction -> handleAttack(Optional.of(e.getKey()), attackAction);
                    }
            }
        ));
        state = buildState();
        effects.clear();
        return state;
    }

    /**
     * @return number of enemies killed
     */
    public int killedEnemies() {
        return model.getKilledEnemies();
    }
}
