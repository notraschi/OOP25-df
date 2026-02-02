package it.unibo.df.gs;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unibo.df.dto.EntityView;
import it.unibo.df.model.abilities.Vec2D;

/**
 * represents the game's state while in combat.
 */
public record CombatState(
    EntityView player,
    Map<Integer, EntityView> enemies,
    List<Set<Vec2D>> effects // effects casted since last tick
) implements GameState {}
