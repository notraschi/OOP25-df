package it.unibo.df.gs;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unibo.df.dto.EntityView;
import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Vec2D;

/**
 * represents the game's state while in combat.
 */
public record CombatState(
    EntityView player,
    Map<Integer, EntityView> enemies,
    // effects casted since last tick
    List<Set<Vec2D>> effects,
    // active Special Ability (disruptor)
    SpecialAbilityView activeDisrupt
) implements GameState {}
