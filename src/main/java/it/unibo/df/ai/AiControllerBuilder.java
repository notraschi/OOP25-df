package it.unibo.df.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder for Ai Controller.
 */
public class AiControllerBuilder {

    private final List<AiStrategy> strategies;

    public AiControllerBuilder() {
        this.strategies = new ArrayList<>();
    }

    /**
     * Adds the strategies we want AI use.
     * 
     * @param strategy to add.
     * @return builder
     */
    public AiControllerBuilder add(AiStrategy strategy) {
        if (Objects.isNull(strategy)) {
            throw new IllegalStateException("Null strategy is illegal!");            
        }
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
        return new AiController(new ArrayList<>(this.strategies));
    }

}
