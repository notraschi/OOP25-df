package it.unibo.df.model.special;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Move;

public enum SpecialArsenal {
    INVERT_MOVEMENT(
        new SpecialAbility(
            Arrays.stream(Move.values()).collect(Collectors.toSet()),
            in -> switch ((Move) in) {
                case Move.DOWN -> Optional.of(Move.UP);
                case Move.UP -> Optional.of(Move.DOWN);
                case Move.RIGHT -> Optional.of(Move.LEFT);
                case Move.LEFT -> Optional.of(Move.RIGHT);
            },
            6
        )
    ),
    DENY_MOVEMENT(
        new SpecialAbility(
            Arrays.stream(Move.values()).collect(Collectors.toSet()),
            SpecialAbilityFn::deny,
            2
        )
    ),
    DENY_ATTACK(
        new SpecialAbility(
            Arrays.stream(Attack.values()).collect(Collectors.toSet()),
            SpecialAbilityFn::deny,
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
