package it.unibo.df.ai;

import java.util.List;
import java.util.Optional;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;
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
    List<Optional<Input>> computeNextAction(GameState gameContext);

    /**
     * 
     * @param gameContext game state is to rate the usefulness of the strategy
     * @return a value in the range 0.0 to 1.0
     */
    double calculateUtility(GameState gameContext);

}
