package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.CurvesUtility;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

public class EscapeStrategy implements AiStrategy {

    private final int idEntity; 

    public EscapeStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState gameContext, List<Ability> loadout) {
        System.out.println("escape");
        return Optional.empty();
    }

    @Override
    public double calculateUtility(CombatState gameContext, List<Ability> loadout) {
        var me = gameContext.enemies().get(idEntity);
        var player = gameContext.player();

        // FATTORI DI AUMENTO
        // 1. Rischio immediato: se sono vicino al player
        int dist = TacticsUtility.manhattanDist(me.position(), player.position());
        double danger = switch (dist) {
            case 1 -> 1.0; //panico sei vicino
            case 2 -> 0.60; //allerta
            case 3 -> 0.5; // allerta
            default -> 0.0;
        };

        //meno vita ho più ho paura
        double lowHp = CurvesUtility.inverse(me.hpRatio());

        long cooldownSum = me.cooldownAbilities().stream().filter(c -> c > 0).count();
        double helpless = cooldownSum / 3.0; //scappo se ho tutto in cooldown
        
        // Peso: HP bassi e "Helpless" contano molto
        double utility = (lowHp * 0.3) + (danger * 0.3) + (helpless * 0.4);
        return Math.max(0.0, Math.min(1.0, utility));
    }

}
