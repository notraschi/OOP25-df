package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

public class PressureStrategy implements AiStrategy{

    private final int idEntity;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public List<Optional<Input>> computeNextAction(CombatState gameContext, List<Ability> loadout) {
        Random rand = new Random();
        var enemies = gameContext.enemies().get(idEntity);
        var player = gameContext.player();
        var in = TacticsUtility.getMovesToTargetting(enemies.position(), player.position());

        System.out.println(gameContext.activeDisrupt());
        System.out.println(enemies.hp());
        System.out.println(gameContext.effects().size());
        if(in.isEmpty()) {
            System.out.println("specialll");
            return List.of(Optional.of(Attack.SPECIAL));
        } else {
            return List.of(Optional.of(in.get(rand.nextInt(0,in.size()))));
        }
        
    }

    @Override
    public double calculateUtility(CombatState gameContext) {
        return 1;
    }

}
