package it.unibo.df.model.combat;

import java.util.List;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.model.abilities.AbilityRegistry;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Create pre-made enemies so that they make sense.
 */
public class EnemyFactory {

    private static final AbilityRegistry arsenal = new AbilityRegistry();
    private EnemyFactory() {}

    public static EnemyDefinition basicEnemy(Vec2D position) {
        return new EnemyDefinition(
            position,
            10,
            List.of(
                arsenal.get(1), //MAGIC NUMBER!!! todo delle costanti con id-abilita 
                arsenal.get(2),
                arsenal.get(3)
            ),
            List.of(
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE,
                AiStrategyType.ESCAPE
            )
        );
    }
}
