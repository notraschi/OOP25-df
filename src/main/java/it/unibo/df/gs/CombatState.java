package it.unibo.df.gs;

import java.util.List;
import java.util.Set;

import it.unibo.df.model.abilities.Vec2D;

/**
 * represents the game's state while in combat.
 */
public record CombatState(
    Vec2D playerPos,
    Set<Vec2D> enemyPos,
    List<Set<Vec2D>> effects // effects casted since last tick
) implements GameState {}
