package it.unibo.df.ai;

import java.util.List;
import java.util.Optional;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;

public class IdleBehavior implements AiBehavior{

    @Override
    public double evaluate(GameState gameState) { //SADAFA
        return 0.8; //test
    }

    @Override
    public List<Optional<Input>> execute() {
        return List.of(Optional.empty());
    }

}
