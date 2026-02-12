package it.unibo.df.gs;

import java.util.List;
import java.util.Optional;

import it.unibo.df.dto.AbilityView;

/**
 * represents the game's state while in the arsenal.
 */
public record ArsenalState(
    // hold abilities loaded in or gained via combine
    List<AbilityView> unlocked,
    // ids of abilities lost after combine
    List<Integer> lost,
    // id of just-equipped ability
    Optional<Integer> equipped,
    // id of just-unequipped ability
    Optional<Integer> unequipped
) implements GameState {

    public static ArsenalState copyOf(ArsenalState as) {
        return new ArsenalState(
            List.copyOf(as.unlocked),
            List.copyOf(as.lost),
            as.equipped,
            as.unequipped
        );
    }
}
