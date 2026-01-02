package it.unibo.df.ai;

import java.util.List;
import java.util.Optional;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

/**
 * the behavior that the AI ​​may have conditioned by the strategy.
 */
public interface AiBehavior {
    /**
     * evaluate the Behavior.
     * 
     * @param gameState from which we take the factors for evaluate
     * @return a value in the range 0.0 to 1.0
     */
    double evaluate(GameState gameState);

    /**
     * performing the behavior may involve more than one input.
     * 
     * @return ordered list of input to follow.
     */
    List<Optional<Input>> execute();

}
