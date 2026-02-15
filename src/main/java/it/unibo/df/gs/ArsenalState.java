package it.unibo.df.gs;

import java.util.List;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.df.dto.AbilityView;

/**
 * Represents the game's state while in the arsenal.
 * 
 * @param unlocked abilities currently available
 * @param lost ids of abilities removed after a combination
 * @param equipped id of the last equipped ability
 * @param unequipped id of the last unequipped ability
 */
@SuppressFBWarnings(
    value = "EI",
    justification = "defensive copies are created using List.of()"
)
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

    /**
     * Returns a copy of the given state.
     *
     * @param as source state
     * @return copied state
     */
    public static ArsenalState copyOf(final ArsenalState as) {
        return new ArsenalState(
            List.copyOf(as.unlocked),
            List.copyOf(as.lost),
            as.equipped,
            as.unequipped
        );
    }
}
