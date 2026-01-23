package it.unibo.df.model.combat;

import java.util.List;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;

/**
 *  Represent enemies.
 */
public record EnemyDefinition(
    Vec2D position,
    int hp, 
    List<Ability> loadout,
    List<AiStrategyType> strategies
) { }
