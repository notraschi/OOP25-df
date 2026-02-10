package it.unibo.df.model.combat;

import java.util.List;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.controller.Progress;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.special.SpecialAbilities;

/**
 * Create pre-made enemies so that they make sense.
 */
public class EnemyFactory {

    private static final Progress arsenal = new Progress();
    private EnemyFactory() {}

    public static EnemyDefinition basicEnemy(Vec2D position) {
        return new EnemyDefinition(
            position,
            100,
            List.of(
                arsenal.get(9), // TODO: remove MAGIC NUMBERs!!
                arsenal.get(1),
                arsenal.get(15)
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

    public static EnemyDefinition createSniper(Vec2D position) {
        return new EnemyDefinition(
            position,
            80, // Meno vita
            List.of(
                arsenal.get(3), // Long Shot (LINE_R) - Attacco principale
                arsenal.get(4),  // Arrow Burst (ARROW_R) - Attacco medio
                arsenal.get(14)  // Quick Heal (SELF) - Cura piccola
            ),
            List.of(
                AiStrategyType.ESCAPE,   // Scappa appena ti avvicini
                AiStrategyType.PRESSURE, // Ti spara da lontano
                AiStrategyType.STABILIZE // Si cura se serve
            ),
            SpecialAbilities.DENY_MOVEMENT // Ti blocca per mirare meglio
        );
    }

    public static EnemyDefinition createTank(Vec2D position) {
        return new EnemyDefinition(
            position,
            150, // Tanta vita
            List.of(
                arsenal.get(1),  // Close Strike (ADJ4)
                arsenal.get(5),  // Fan Sweep (ARROWWIDE_U)
                arsenal.get(16)  // Big Heal (SELF)
            ),
            List.of(
                AiStrategyType.PRESSURE, // Ti corre addosso
                AiStrategyType.STABILIZE // Si cura spesso
                // Niente Escape: il tank non scappa!
            ),
            SpecialAbilities.INVERT_MOVEMENT // Ti confonde mentre ti è vicino
        );
    }

    //BERSERKER ONLY ATTACCO RAVVICINATO
}
