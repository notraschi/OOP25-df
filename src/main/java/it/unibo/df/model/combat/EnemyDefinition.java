package it.unibo.df.model.combat;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.special.SpecialAbility;
import it.unibo.df.utility.Vec2D;

/**
 * Represent enemies to spawn.
 * 
 * @param position where start at spawn
 * @param hp starting life
 * @param loadout of ability
 * @param strategies that implement
 * @param special equiped
 */
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP2"}, 
    justification = "lists are created using List.of() and are immutable"
)
public record EnemyDefinition(
    Vec2D position,
    int hp, 
    List<Ability> loadout,
    List<AiStrategyType> strategies,
    SpecialAbility<?> special
) { }
