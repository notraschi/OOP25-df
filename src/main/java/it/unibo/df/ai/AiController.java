package it.unibo.df.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;


/**
 * Context of AiStrategy.
 */
public class AiController {
    private final Queue<Optional<Input>> actionQueue = new LinkedList<>();
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

    public void interrupt() { //+-
        actionQueue.clear();
    }

    private void setStrategy(AiStrategy strategy) {
        this.currentStrategy = strategy;
    }

    private void addInputToQueue(List<Optional<Input>> inputs) {
        actionQueue.addAll(inputs);
    }

    private Optional<Input> getInput (CombatState gameState) {
        if (actionQueue.isEmpty()) {
            updateStrategy(gameState);
            addInputToQueue(currentStrategy.computeNextAction(gameState, loadout));
        }
        return actionQueue.poll();
    }

    private void updateStrategy(CombatState gameState) { //+-

        AiStrategy bestStrategy = this.currentStrategy;
        double maxUtility = (Objects.isNull(bestStrategy)) 
                        ? Double.NEGATIVE_INFINITY
                        : currentStrategy.calculateUtility(gameState) + 0.10; //the best candidate must exceed it by 0.10

        for (AiStrategy strategy: avaiableStrategies) { //use continue to skipp calculation of current strategy, brutto
            double utility = strategy.calculateUtility(gameState);
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
