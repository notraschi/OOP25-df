package it.unibo.df.ai;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

/**
 * Context of AiStrategy.
 */
public class AiController {
    private final List<AiStrategy> avaiableStrategies;
    private final List<Ability> loadout;
    private AiStrategy currentStrategy; 

    public AiController(List<AiStrategy> strategies, List<Ability> loadout) {
        this.avaiableStrategies = strategies;
        this.loadout = loadout;
    }

    public Optional<Input> computeNextInput(CombatState gameState) {
        return getInput(gameState);
    }

    private void setStrategy(AiStrategy strategy) {
        this.currentStrategy = strategy;
    }

    private Optional<Input> getInput (CombatState gameState) {

        updateStrategy(gameState);
        return currentStrategy.computeNextAction(gameState, loadout);
    }

    private void updateStrategy(CombatState gameState) { //+-

        AiStrategy bestStrategy = this.currentStrategy;
        double maxUtility = (Objects.isNull(bestStrategy))
                        ? Double.NEGATIVE_INFINITY
                        : currentStrategy.calculateUtility(gameState, loadout) + 0.05; //the best candidate must exceed it by 0.10 (?)

        for (AiStrategy strategy: avaiableStrategies) { //use continue to skipp calculation of current strategy, brutto
            double utility = strategy.calculateUtility(gameState, loadout);
            if(maxUtility < utility) {
                maxUtility = utility;
                bestStrategy = strategy;
            }
        }

        if ((!Objects.isNull(bestStrategy)) && (bestStrategy != currentStrategy)) {
            setStrategy(bestStrategy);
        }
    }
}
