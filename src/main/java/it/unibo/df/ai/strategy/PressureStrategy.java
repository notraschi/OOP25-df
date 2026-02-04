package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;

public class PressureStrategy implements AiStrategy{

    private final int idEntity;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public List<Optional<Input>> computeNextAction(CombatState gameContext) {
        Random rand = new Random();
        var in = TacticsUtility.getMovesToTargetting(gameContext.enemies().get(idEntity).position(), gameContext.player().position());
        if(in.isEmpty()) {
            return List.of();
        } else {
            return List.of(Optional.of(in.get(rand.nextInt(0,in.size()))));
        }
    }

    @Override
    public double calculateUtility(CombatState gameContext) {
        return 1;
    }

}
