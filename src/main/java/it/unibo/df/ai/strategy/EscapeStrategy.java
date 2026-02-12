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
import it.unibo.df.model.abilities.AbilityType;

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
        System.out.println("escape" + idEntity +"--"+ me.hp());
        return AiActions.fleeFromTarget(me, player);
    }

    @Override //ESCAPE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        //all'inizio non scappo tanto, alla fine non scappo tanto do il tutto per tutto, scappo di piu a mid game
        double fear = CurvesUtility.gaussian(me.hpRatio(), 0.4, 0.3);

        //Rischio immediato ovvero se sono vicino al player
        //normalizzazion mi da fuori questo
        // 0 -> 0.0
        // 1 -> 0.055             danger -> 1
        // 6 -> 0.333             danger -> 0.70
        // 9 -> 0.5 (meta mappa)  danger -> 0.25
        int dist = TacticsUtility.manhattanDist(me.position(), player.position());
        double dist01 = TacticsUtility.normalizeManhattanDist(dist);
        double danger = CurvesUtility.logistic(CurvesUtility.inverse(dist01), 10, 0.8);

        //se ho cooldown attivi allora ho paura, PER TUTTI I COOLDOWN
        // 3 -> 1
        // 2 -> 0.6
        // 1 -> 0.3 
        //quando finisco le cure, cura sempre attiva, questo 0.3 costante
        // long cooldownsActive = me.cooldownAbilities().stream().filter(c -> c > 0).count();
        // double helplessScore = (double) cooldownsActive / (double) loadout.size();

        var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK); //abilita con cui attacco
        //se ho cooldown attivi allora ho paura, SOLO PER ATTACK
        //mi calcolo i disponibili sui totali 
        // 0 / 2 -> 0
        // 1 / 2 -> 0.5
        // 2 / 2 -> 1
        double helplessScore = (double) ammo.stream().filter(x -> me.cooldownAbilities().get(x) > 0).count() / (double) ammo.size();


        double score = helplessScore * danger * fear;//Math.max(fear * danger, helplessScore * danger);
        //System.out.println(score +"--"+idEntity+"--ESCAPE--danger: "+danger+"--activecooldown--"+cooldownsActive+"--helplessScore--"+helplessScore);

        return CurvesUtility.clamp(score);
    }

    /**
     * l'idea è
     * me ne voglio andare se abilita di attacco sono in cooldown e sono vicino, e se ho poca vita il peso del ho abilita in cooldown è maggiore
     */

}
