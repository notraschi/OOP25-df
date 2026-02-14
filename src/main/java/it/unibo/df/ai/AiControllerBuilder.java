package it.unibo.df.ai;

import java.util.ArrayList;
import java.util.List;

import it.unibo.df.ai.strategy.EscapeStrategy;
import it.unibo.df.ai.strategy.PressureStrategy;
import it.unibo.df.ai.strategy.StabilizeStrategy;
import it.unibo.df.model.abilities.Ability;

/**
 * Builder for Ai Controller.
 */
public final class AiControllerBuilder {

    private final List<AiStrategy> strategies = new ArrayList<>();
    private final int idEntity;
    private List<Ability> loadout = new ArrayList<>();

    /**
     * Start build of an AiController. 
     * 
     * @param idEntity to which the controller is associated
     */
    public AiControllerBuilder(final int idEntity) {
        this.idEntity = idEntity;
    }

    /**
     *  Loadout of enemy.
     * 
     * @param loadoutToSet the equipped abilities
     * @return builder
     */
    public AiControllerBuilder setLoadout(final List<Ability> loadoutToSet) {
        this.loadout = loadoutToSet;
        return this;
    }

    /**
     * Adds the strategies we want AI use.
     * 
     * @param type indicate the Strategy.
     * @return builder
     */
    public AiControllerBuilder add(final AiStrategyType type) {
        final AiStrategy strategy = switch (type) {
            case PRESSURE -> new PressureStrategy(idEntity);
            case STABILIZE -> new StabilizeStrategy(idEntity);
            case ESCAPE -> new EscapeStrategy(idEntity);
            default -> throw new IllegalArgumentException("Strategy not implemented: " + type);
        };
        this.strategies.add(strategy);
        return this;
    }

    /**
     * Build AiController with added strategies.
     * 
     * @return AiController
     */
    public AiController build() {
        if (strategies.isEmpty()) {
            throw new IllegalStateException("There are no strategies to follow!");
        }
        return new AiControllerImpl(
            new ArrayList<>(this.strategies),
            new ArrayList<>(this.loadout)
        );
    }

}
