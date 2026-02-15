package it.unibo.df.model.combat;

import java.util.List;
import java.util.Map;

import it.unibo.df.ai.AiStrategyType;
import it.unibo.df.controller.Progress;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.special.SpecialAbilityFactory;

/**
 * Create pre-made enemies.
 */
public final class EnemyFactory {

    public static final int AVAILABLE_ENEMY_TYPES = 3;

    private static final int BASIC_ENEMY_HP = 100;
    private static final int SNIPER_ENEMY_HP = 80;
    private static final int TANK_ENEMY_HP = 150;

    private static final Map<Integer, Ability> ARSENAL = Progress.allRegisteredAbilities();

    private EnemyFactory() { }

    private static Ability getByName(final String name) {
        return ARSENAL.values().stream()
            .filter(a -> a.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Ability not found: " + name));
    }

    /**
     * create a basic enemy with basic loadout.
     * 
     * @param position where enemies spawn
     * @return a representation of enemies
     */
    public static EnemyDefinition createBasicEnemy(final Vec2D position) {
        return new EnemyDefinition(
            position,
            BASIC_ENEMY_HP,
            List.of(
                getByName("Wide Smash"),
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

    /**
     * creates a sniper, equips long-distance ability and fast healing, but has little life.
     * 
     * @param position where enemies spawn
     * @return a representation of enemies
     */
    public static EnemyDefinition createSniper(final Vec2D position) {
        return new EnemyDefinition(
            position,
            SNIPER_ENEMY_HP,
            List.of(
                getByName("Long Shot"),
                getByName("Arrow Burst"),
                getByName("Small Heal")
            ),
            List.of(
                AiStrategyType.ESCAPE,
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE
            ),
            SpecialAbilityFactory.denyMovement()
        );
    }

    /**
     * He creates a tank, has a lot of life and has close attacks, presses and heals, he doesn't run away.
     * 
     * @param position where enemies spawn
     * @return a representation of enemies
     */
    public static EnemyDefinition createTank(final Vec2D position) {
        return new EnemyDefinition(
            position,
            TANK_ENEMY_HP,
            List.of(
                getByName("Close Strike"),
                getByName("Fan Sweep"),
                getByName("Big Heal")
            ),
            List.of(
                AiStrategyType.PRESSURE,
                AiStrategyType.STABILIZE
            ),
            SpecialAbilityFactory.invertMovement()
        );
    }

    /**
     * helper method that helps randomly create enemies based on the type indicated by index.
     * 
     * @param index indicate the type of enemy
     * @param position to spawn enemy
     * @return a representation of enemies
     */
    public static EnemyDefinition createByIndex(final int index, final Vec2D position) {
        return switch (index) {
            case 0 -> createBasicEnemy(position);
            case 1 -> createSniper(position);
            case 2 -> createTank(position);
            default -> throw new IllegalArgumentException("Tipo di nemico non valido: " + index);
        };
    }
}
