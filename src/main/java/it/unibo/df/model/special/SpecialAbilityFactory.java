package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;

import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.Cooldown;

/**
 * factory for special abilities.
 */
public final class SpecialAbilityFactory {
    private static final int INVERT_MOVEMENT_MS = 6000;
    private static final int DENY_MOVEMENT_MS = 2000;
    private static final int DENY_ATTACK_MS = 5000;

    private static final Set<Vec2D> ALL_MOVEMENT_INPUTS = Set.of(
        new Vec2D(0, 1), 
        new Vec2D(0, -1), 
        new Vec2D(1, 0), 
        new Vec2D(-1, 0) 
    );
    private static final Set<Integer> ALL_ATTACKS = Set.of(0, 1, 2);

    private SpecialAbilityFactory() {
    }

    /**
     * creates a special ability inverting player's movement.
     * 
     * @return the special ability
     */
    public static SpecialAbility<Vec2D> invertMovement() {
        return new SpecialAbility<>(
            Vec2D.class,
            ALL_MOVEMENT_INPUTS,
            vec -> Optional.of(new Vec2D(-vec.x(), -vec.y())),
            new Cooldown(INVERT_MOVEMENT_MS)
        );
    }

    /**
     * creates a special ability dening all movement.
     * 
     * @return the special ability
     */
    public static SpecialAbility<Vec2D> denyMovement() {
        return new SpecialAbility<>(
            Vec2D.class,
            ALL_MOVEMENT_INPUTS,
            vec -> Optional.empty(),
            new Cooldown(DENY_MOVEMENT_MS)
        );
    }

    /**
     * creates a special ability dening all attacks.
     * 
     * @return the special ability.
     */
    public static SpecialAbility<Integer> denyAttack() {
        return new SpecialAbility<>(
            Integer.class,
            ALL_ATTACKS,
            n -> Optional.empty(),
            new Cooldown(DENY_ATTACK_MS)
        );
    }
}
