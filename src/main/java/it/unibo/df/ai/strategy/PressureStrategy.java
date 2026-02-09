package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.CurvesUtility;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

public class PressureStrategy implements AiStrategy{

    private final int idEntity;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState gameContext, List<Ability> loadout) {
        var me = gameContext.enemies().get(idEntity);
        var player = gameContext.player();
        Random rand = new Random();

        System.out.println("pressure");

        // provo a sparare

        // mi sposto in una buona posizione per sparare

        //mi avvicino alla peggio

        return Optional.empty();        
    }

    @Override
    public double calculateUtility(CombatState gameContext, List<Ability> loadout) {
        var me = gameContext.enemies().get(idEntity);
        var player = gameContext.player();

        //usa con prob di 02 di usare attack anche se3 non attacco di 1 dist

        // final double canHitSoon = switch (player.cooldownAbilities().) {  
        //     case 1 -> 1.0;
        //     case 2 -> 0.70;
        //     case 3 -> 0.35;
        //     default -> 0.10;
        // };

        //scappa a curarti, panic
        if (me.hpRatio() < 0.2 && player.hpRatio() > 0.2) {
            return 0.0;
        }

        //kill potential TODO

        //vantaggio hp
        double hpAdvantage = CurvesUtility.logistic(me.hpRatio() - player.hpRatio(), 6, 0);
        System.out.println(hpAdvantage);

        // 2. Kill Potential (semplificato): Se il player ha poca vita, utility schizza a 1.
        double bloodThirst = CurvesUtility.inverse(player.hpRatio()); //TODO

        //piu sono vicino piu presso
        double dist = TacticsUtility.normalizeDist(TacticsUtility.manhattanDist(me.position(), player.position()));
        double closeRangeBonus = CurvesUtility.inverse(dist);

        //bonus ai fattori
        double utility = (hpAdvantage * 0.4) + (bloodThirst * 0.4) + (closeRangeBonus * 0.2);

        //possibile ridurre la utility calcolandoci il rischio immediato!

        return Math.max(0.0, Math.min(1.0, utility));
    }

}
