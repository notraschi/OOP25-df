package it.unibo.df.gs;

import java.util.List;

import it.unibo.df.dto.AbilityView;

/**
 * represents the game's state while in the arsenal.
 */
public record ArsenalState(
    // hold abilities loaded in or gained via combine
    List<AbilityView> unlocked,
    // ids of abilities lost after combine
    List<Integer> lost,
    // ids of abilities (ability) just equipped
    List<Integer> equipped
) implements GameState {

    public void clear() {
        unlocked.clear();
        lost.clear();
        equipped.clear();
    }

    public static ArsenalState copyOf(ArsenalState as) {
        return new ArsenalState(List.copyOf(as.unlocked), List.copyOf(as.lost), List.copyOf(as.equipped));
    }
}
