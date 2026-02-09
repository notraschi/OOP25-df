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
     * Each strategy gives a different weight and bonus to the actions to evaluate them.
     * 
     * @param gameContext game state is used to calculate the best action
     * @return an action is composed of one or more inputs
     */
    Optional<Input> computeNextAction(CombatState gameContext, List<Ability> loadout);

    /**
     * 
     * @param gameContext game state is to rate the usefulness of the strategy
     * @return a value in the range 0.0 to 1.0
     */
    double calculateUtility(CombatState gameContext, List<Ability> loadout);

}
