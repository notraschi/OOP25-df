package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;

import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.Cooldown;

/**
 * stores the special abilities, and utilities.
 */
public enum SpecialAbilities {
    INVERT_MOVEMENT(
        new SpecialAbility<Vec2D>(
            Vec2D.class,
            Set.of(new Vec2D(0,1), new Vec2D(0,-1), new Vec2D(1,0), new Vec2D(-1,0)),
            vec -> Optional.of(new Vec2D(-vec.x(), -vec.y())),
            new Cooldown(6000)
        )
    ),
    DENY_MOVEMENT(
        new SpecialAbility<Vec2D>(
            Vec2D.class,
            Set.of(new Vec2D(0,1), new Vec2D(0,-1), new Vec2D(1,0), new Vec2D(-1,0)),
            vec -> Optional.empty(),
            new Cooldown(2000)
        )
    ),
    DENY_ATTACK(
        new SpecialAbility<Integer>(
            Integer.class,
            Set.of(1, 2, 3),
            n -> Optional.empty(),
            new Cooldown(5000)
        )
    );

    public final SpecialAbility ability;

    private SpecialAbilities(SpecialAbility effect) {
        ability = effect;
    }

    /**
     * converts SpecialAbility to the view-able version.
     * 
     * @param special the SpecialAbility
     * @return the view version
     */
    public static SpecialAbilityView asView(Optional<SpecialAbilities> special) {
        if (special.isPresent()) {
            return switch (special.get()) {
                case SpecialAbilities.INVERT_MOVEMENT -> SpecialAbilityView.INVERT_MOVEMENT;
                case SpecialAbilities.DENY_MOVEMENT -> SpecialAbilityView.DENY_MOVEMENT;
                case SpecialAbilities.DENY_ATTACK -> SpecialAbilityView.DENY_ATTACK;
            };
        } else {
            return SpecialAbilityView.NONE;
        }
    }
}
