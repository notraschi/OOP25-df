package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.AiActions;
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
    public Optional<Input> computeNextAction(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        //si ritira
        //System.out.println("escape");
        return AiActions.fleeFromTarget(me, player);
    }

    @Override //ESCAPE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        // Se ho poca vita, voglio scappare, ho paura.
        // Ma attenzione se è TROPPO bassa, vince Stabilize quindi questa curva è lineare/soft. DA BILANCIARE NEL CASO
        double fear = CurvesUtility.inverse(me.hpRatio());  //+-

        //Rischio immediato ovvero se sono vicino al player
        int dist = TacticsUtility.manhattanDist(me.position(), player.position());
        double dist01 = TacticsUtility.normalizeManhattanDist(dist);
        //lineare mi sembra troppo terro esponenziale
        double danger = CurvesUtility.exponential(CurvesUtility.inverse(dist01), 3.0); 

        //DA RIVEDERE IL FATTO DEI HP BASSI, INTERFERISCE CON STABILIZE (?)

        long cooldownsActive = me.cooldownAbilities().stream().filter(c -> c > 0).count();
        double helplessScore = cooldownsActive / loadout.size(); //scappo se ho tutto in cooldown

        double score = Math.max(fear * danger, helplessScore * danger);
        
        return CurvesUtility.clamp(score);
    }

}
