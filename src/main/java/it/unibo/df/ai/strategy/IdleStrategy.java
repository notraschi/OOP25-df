package it.unibo.df.ai.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiBehavior;
import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.behavior.IdleBehavior;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

public class IdleStrategy implements AiStrategy {

    final List<AiBehavior> behaviors = new ArrayList<>(List.of(new IdleBehavior())); //test

    @Override
    public List<Optional<Input>> computeNextAction(GameState gameContext) {//test //SADAFA
        return bestBehavior().execute();
    }

    @Override
    public double calculateUtility(GameState gameContext) { //SADAFA
        return 0.9; //test
    }

    private AiBehavior bestBehavior() { //SADAFA
        return behaviors.getFirst(); //test
    }

}
