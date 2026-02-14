package it.unibo.df.model.combat;

import java.util.List;
import java.util.Map;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.controller.Progress;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.special.SpecialAbilityFactory;

/**
 * Create pre-made enemies so that they make sense.
 */
public class EnemyFactory {

    private static final Map<Integer, Ability> arsenal = Progress.allRegisteredAbilities();
    
    private EnemyFactory() { }

    private static Ability getByName(String name) {
        return arsenal.values().stream()
            .filter(a -> a.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Ability not found: " + name));
    }

    public static EnemyDefinition basicEnemy(Vec2D position) {
        return new EnemyDefinition(
            position,
            100,
            List.of(
                getByName("Close Strike"),
                getByName("Cross Cut"),
                getByName("Medium Heal")
            ),
            List.of(
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE,
                AiStrategyType.ESCAPE
            ),
            SpecialAbilityFactory.denyAttack()
        );
    }

    public static EnemyDefinition createSniper(Vec2D position) {
        return new EnemyDefinition(
            position,
            80, // Meno vita
            List.of(
                getByName("Long Shot"), // Long Shot - Attacco principale
                getByName("Arrow Burst"),  // Arrow Burst - Attacco medio
                getByName("Small Heal")  // Quick Heal - Cura piccola
            ),
            List.of(
                AiStrategyType.ESCAPE,   // Scappa appena ti avvicini
                AiStrategyType.PRESSURE, // Ti spara da lontano
                AiStrategyType.STABILIZE // Si cura se serve
            ),
            SpecialAbilityFactory.denyMovement()
        );
    }

    public static EnemyDefinition createTank(Vec2D position) {
        return new EnemyDefinition(
            position,
            150,
            List.of(
                getByName("Close Strike"),
                getByName("Fan Sweep"),
                getByName("Big Heal")
            ),
            List.of(
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE
                // Niente Escape: il tank non scappa!
            ),
            SpecialAbilityFactory.invertMovement()
        );
    }

    //BERSERKER
}
