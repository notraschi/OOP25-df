package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

public class EscapeStrategy implements AiStrategy {

    private final int idEntity;

    public EscapeStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public List<Optional<Input>> computeNextAction(CombatState gameContext, List<Ability> loadout) {
        return List.of(Optional.empty());
    }

    @Override
    public double calculateUtility(CombatState gameContext) {
        return 0.0;
    }

}
