package it.unibo.df.ai;

import java.util.List;
import java.util.Optional;

import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

/**
 * AI-driven strategies implemented via Strategy Pattern.
 */
public interface AiStrategy {

    /**
     * Each strategy has a goal, and it tries to make the best moves to achieve the goal.
     * 
     * @param gameContext game state is used to calculate the best action
     * @param loadout on which the next action is calculated
     * @return input
     */
    Optional<Input> computeNextAction(CombatState gameContext, List<Ability> loadout);

    /**
     * Calculate the utility of strategy based on the context.
     * 
     * @param gameContext game state is to rate the usefulness of the strategy
     * @param loadout on which the utility is calculated
     * @return a value between 0.0 to 1.0
     */
    double calculateUtility(CombatState gameContext, List<Ability> loadout);

}
