package it.unibo.df.ai;

import java.util.Optional;

import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;

/**
 * AI controller for enemies.
 */
@FunctionalInterface
public interface AiController {

    /**
     * Calculate the next move of the entity to make.
     * 
     * @param gameState context of the game
     * @return input to do
     */
    Optional<Input> computeNextInput(CombatState gameState);
}
