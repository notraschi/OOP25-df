package it.unibo.df.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;


/**
 * Context of AiStrategy.
 */
public class AiController {
    private final Queue<Input> actionQueue = new LinkedList<>();
    private final List<AiStrategy> avaiableStrategies;
    private AiStrategy currentStrategy; 

    public AiController(List<AiStrategy> strategies) {
        avaiableStrategies = strategies;
    }

    public Input computeNextInput(GameState gameState) {
        return getInput(gameState);
    }

    public void interrupt() { //+-
        actionQueue.clear();
    }

    private void setStrategy(AiStrategy strategy) {
        this.currentStrategy = strategy;
    }

    private void addInputToQueue(List<Input> inputs) {
        actionQueue.addAll(inputs);
    }

    private Input getInput (GameState gameState) {
        if (actionQueue.isEmpty()) {
            updateStrategy(gameState);
            addInputToQueue(currentStrategy.computeNextAction(gameState));
        }
        return actionQueue.poll();
    }

    private void updateStrategy(GameState gameState) { //+-

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
