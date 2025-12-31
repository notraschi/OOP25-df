package it.unibo.df.ai;

import java.util.List;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;

public class IdleBehavior implements AiBehavior{

    @Override
    public double evaluate(GameState gameState) { //SADAFA
        return 0.8; //test
    }

    @Override
    public List<Input> execute() {
        return List.of(Move.IDLE);
    }

}
