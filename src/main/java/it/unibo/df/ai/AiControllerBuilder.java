package it.unibo.df.ai;

import java.util.ArrayList;
import java.util.List;

import it.unibo.df.ai.strategy.EscapeStrategy;
import it.unibo.df.ai.strategy.PressureStrategy;
import it.unibo.df.ai.strategy.StabilizeStrategy;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.special.SpecialAbilities;

/**
 * Builder for Ai Controller.
 */
public class AiControllerBuilder {

    private final List<AiStrategy> strategies;
    private final List<Ability> loadout;
    private final int idEntity;
    private final SpecialAbilities special;

    /**
     * Start build of an AiController. 
     * 
     * @param idEntity to which the controller is associated
     */
    public AiControllerBuilder(int idEntity, SpecialAbilities special) {
        this.strategies = new ArrayList<>();
        this.loadout = new ArrayList<>();
        this.idEntity = idEntity;
        this.special = special;
    }

    /**
     *  loadout of enemy
     * 
     * @param loadout the equipped abilities
     * @return builder
     */
    public AiControllerBuilder setLoadout(List<Ability> loadout) {
        this.loadout.addAll(loadout);
        return this;
    }

    /**
     * Adds the strategies we want AI use.
     * 
     * @param strategy to add.
     * @return builder
     */
    public AiControllerBuilder add(AiStrategyType type) {
        AiStrategy strategy = switch (type) {
            case PRESSURE -> new PressureStrategy(idEntity);
            case STABILIZE -> new StabilizeStrategy(idEntity);
            case ESCAPE -> new EscapeStrategy(idEntity);
            default -> throw new IllegalArgumentException("Strategia non implementata: " + type);
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
        return new AiController(
            new ArrayList<>(this.strategies),
            new ArrayList<>(this.loadout),
            special
        );
    }

}
