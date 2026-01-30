package it.unibo.df.gs;

import java.util.List;

/**
 * represents the game's state while in the arsenal.
 */
public record ArsenalState(
    // hold abilities loaded in or gained via combine
    List<AbilityView> unlocked,
    // abilities lost after combine
    List<AbilityView> lost,
    // abilities (ability) just equipped
    List<AbilityView> equipped
) implements GameState {

   /*  public static ArsenalState setup(List<AbilityView> abs) {
        return new ArsenalState(abs, List.of(), List.of());
    }

    public static ArsenalState equip(AbilityView ab) {
        return new ArsenalState(List.of(), List.of(), List.of(ab));
    }

    public static ArsenalState combine(List<AbilityView> ingredients, AbilityView result) {
        return new ArsenalState(List.of(), ingredients, List.of(result));
    }

    public static ArsenalState unchanged() {
        return new ArsenalState(List.of(), List.of(), List.of());
    } */

    public static ArsenalState copyOf(ArsenalState as) {
        return new ArsenalState(List.copyOf(as.unlocked), List.copyOf(as.lost), List.copyOf(as.equipped));
    }
}
