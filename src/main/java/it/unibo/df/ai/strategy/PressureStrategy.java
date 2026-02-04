package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

public class PressureStrategy implements AiStrategy{

    private final int idEntity;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public List<Optional<Input>> computeNextAction(GameState gameContext) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'computeNextAction'");
    }

    @Override
    public double calculateUtility(GameState gameContext) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateUtility'");
    }

}
