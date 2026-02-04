package it.unibo.df.model.combat;

import java.util.List;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.model.abilities.AbilityRegistry;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.special.SpecialAbilities;

/**
 * Create pre-made enemies so that they make sense.
 */
public class EnemyFactory {

    private static final AbilityRegistry arsenal = new AbilityRegistry();
    private EnemyFactory() {}

    public static EnemyDefinition basicEnemy(Vec2D position) {
        return new EnemyDefinition(
            position,
            100,
            List.of(
                arsenal.get(1), // TODO: remove MAGIC NUMBERs!!
                arsenal.get(2),
                arsenal.get(3)
            ),
            List.of(
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE,
                AiStrategyType.ESCAPE
            ),
            // TODO: remove this, this is tmp
            SpecialAbilities.DENY_ATTACK
        );
    }
}
