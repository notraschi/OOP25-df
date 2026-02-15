package it.unibo.df.ai;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

/**
 * Controller for enemy Ai.
 */
public class AiControllerImpl implements AiController {
    private static final double UTILITY_SWITCH_MARGIN = 0.05;
    private final List<AiStrategy> availableStrategies;
    private final List<Ability> loadout;
    private AiStrategy currentStrategy; 

    /**
     * Constructor of AiController.
     * 
     * @param strategies that AI can use
     * @param loadout equiped
     */
    public AiControllerImpl(final List<AiStrategy> strategies, final List<Ability> loadout) {
        this.availableStrategies = strategies;
        this.loadout = loadout;
        if (this.availableStrategies.isEmpty()) {
            throw new IllegalArgumentException("AI must have a strategy");
        }
        this.currentStrategy = this.availableStrategies.get(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Optional<Input> computeNextInput(final CombatState gameState) {
        return getInput(gameState);
    }

    private void setStrategy(final AiStrategy strategy) {
        this.currentStrategy = strategy;
    }

    private Optional<Input> getInput(final CombatState gameState) {
        updateStrategy(gameState);
        return currentStrategy.computeNextAction(gameState, loadout);
    }

    private void updateStrategy(final CombatState gameState) {

        AiStrategy bestStrategy = this.currentStrategy;
        double maxUtility = Objects.isNull(bestStrategy)
                        ? Double.NEGATIVE_INFINITY
                        : currentStrategy.calculateUtility(gameState, loadout) + UTILITY_SWITCH_MARGIN;

        for (final AiStrategy strategy: availableStrategies) {
            final double utility = strategy.calculateUtility(gameState, loadout);
            if (maxUtility < utility) {
                maxUtility = utility;
                bestStrategy = strategy;
            }
        }
        if (!Objects.isNull(bestStrategy) && !bestStrategy.equals(currentStrategy)) {
            setStrategy(bestStrategy);
        }
    }
}
