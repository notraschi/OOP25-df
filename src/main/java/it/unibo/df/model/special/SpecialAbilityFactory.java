package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;

import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.Cooldown;

/**
 * factory for special abilities.
 */
public final class SpecialAbilityFactory {
    private static final Set<Vec2D> ALL_MOVEMENT_INPUTS = Set.of(new Vec2D(0,1), new Vec2D(0,-1), new Vec2D(1,0), new Vec2D(-1,0));
    private static final Set<Integer> ALL_ATTACKS = Set.of(0, 1, 2);
    /**
     * creates a special ability inverting player's movement.
     * 
     * @return the special ability
     */
    public static SpecialAbility<Vec2D> invertMovement() {
        return new SpecialAbility<>(
            SpecialAbilityView.INVERT_MOVEMENT,
            Vec2D.class,
            ALL_MOVEMENT_INPUTS,
            vec -> Optional.of(new Vec2D(-vec.x(), -vec.y())),
            new Cooldown(6000)
        );
    }

    /**
     * creates a special ability dening all movement.
     * 
     * @return the special ability
     */
    public static SpecialAbility<Vec2D> denyMovement() {
        return new SpecialAbility<>(
            SpecialAbilityView.DENY_MOVEMENT,
            Vec2D.class,
            ALL_MOVEMENT_INPUTS,
            vec -> Optional.empty(),
            new Cooldown(2000)
        );
    }

    /**
     * creates a special ability dening all attacks.
     * 
     * @return the special ability.
     */
    public static SpecialAbility<Integer> denyAttack() {
        return new SpecialAbility<>(
            SpecialAbilityView.DENY_ATTACK,
            Integer.class,
            ALL_ATTACKS,
            n -> Optional.empty(),
            new Cooldown(5000)
        );
    }
}
