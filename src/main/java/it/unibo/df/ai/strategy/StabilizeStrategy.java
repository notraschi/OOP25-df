package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.CurvesUtility;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;

public class StabilizeStrategy implements AiStrategy {

    private final int idEntity;

    public StabilizeStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState gameContext, List<Ability> loadout) {
        System.out.println("stabilize");
        return Optional.empty();
    }

    @Override
    public double calculateUtility(CombatState gameContext, List<Ability> loadout) {
        var me = gameContext.enemies().get(idEntity);
        var player = gameContext.player();


        //posso killarlo 1. TODO utility per controllarlo

        //hp alti, non mi curo.
        if (me.hpRatio() > 0.9) return 0.0;

        //cresce esponenzialmente al calare della vita
        double panic = CurvesUtility.exponential(CurvesUtility.inverse(me.hpRatio()), 2);

        // C. SAFETY (Peso: BASSO/BONUS)
        // Se sono lontano, è un buon momento. Normalizzo su boardSize (es. 10).
        double dist = TacticsUtility.manhattanDist(me.position(), player.position());
        double safetyFactor = CurvesUtility.logistic(TacticsUtility.normalizeDist((int)dist), 10, 0.4);

        double utility = (panic * 0.8) + (safetyFactor * 0.1);

        // Clamping finale tra 0.0 e 1.0
        return Math.max(0.0, Math.min(1.0, utility));
    }

}
