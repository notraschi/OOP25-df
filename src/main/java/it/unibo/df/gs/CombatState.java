package it.unibo.df.gs;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.df.configurations.Constants;
import it.unibo.df.dto.CombatStatus;
import it.unibo.df.dto.EntityView;
import it.unibo.df.model.abilities.Vec2D;

/**
 * represents the game's state while in combat.
 * 
 * @param player player view
 * @param enemies enemy views by id
 * @param effects effects
 * @param isDisruptActive disrupt
 */
public record CombatState(
    EntityView player,
    Map<Integer, EntityView> enemies,
    // effects casted since last tick
    List<Set<Vec2D>> effects,
    boolean isDisruptActive
) implements GameState {

    /**
     * calculates current match status.
     * 
     * @return the match status as an enum
     */
    public CombatStatus matchStatus() {
        if (player.hp() == 0) {
            return CombatStatus.LOST;
        } else if (enemies.values().stream().map(EntityView::hp).allMatch(hp -> hp == 0)) {
            return CombatStatus.WON;
        } else {
            return CombatStatus.RUNNING;
        }
    }

    /**
     * same as effects, but only the cells that are actually on the board.
     * 
     * @return a list of sets of the affected cells
     */
    public List<Set<Vec2D>> effectsOnBoard() {
        return effects.stream()
            .map(set -> set.stream()
                .filter(pos -> pos.x() >= 0 && pos.x() < Constants.BOARD_SIZE)
                .filter(pos -> pos.y() >= 0 && pos.y() < Constants.BOARD_SIZE)
                .collect(Collectors.toSet())
            )
            .toList();
    }
}
