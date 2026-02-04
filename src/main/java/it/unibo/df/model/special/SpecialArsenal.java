package it.unibo.df.model.special;

import java.util.Optional;
import java.util.Set;

import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Vec2D;

public enum SpecialArsenal {
    INVERT_MOVEMENT(
        new SpecialAbility<Vec2D>(
            Set.of(new Vec2D(0,1), new Vec2D(0,-1), new Vec2D(1,0), new Vec2D(-1,0)),
            vec -> Optional.of(new Vec2D(-vec.x(), -vec.y())),
            6
        )
    ),
    DENY_MOVEMENT(
        new SpecialAbility<Vec2D>(
            Set.of(new Vec2D(0,1), new Vec2D(0,-1), new Vec2D(1,0), new Vec2D(-1,0)),
            vec -> Optional.empty(),
            2
        )
    ),
    DENY_ATTACK(
        new SpecialAbility<Integer>(
            Set.of(1, 2, 3),
            n -> Optional.empty(),
            5
        )
    );

    public final SpecialAbility effect;

    private SpecialArsenal(SpecialAbility effect) {
        this.effect = effect;
    }

    public SpecialAbilityView asView() {
        return switch (this) {
            case SpecialArsenal.INVERT_MOVEMENT -> SpecialAbilityView.INVERT_MOVEMENT;
            case SpecialArsenal.DENY_MOVEMENT -> SpecialAbilityView.DENY_MOVEMENT;
            case SpecialArsenal.DENY_ATTACK -> SpecialAbilityView.DENY_ATTACK;
        };
    }
}
