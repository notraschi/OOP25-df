package it.unibo.df.ai.util;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import it.unibo.df.dto.EntityView;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

public class AiActions {

    private AiActions() { }

    public static Optional<Input> tryBestAttack(EntityView me, Vec2D target, List<Ability> loadout) {

        for(int i = 0; i < loadout.size(); i++) {
            if(me.cooldownAbilities().get(i) == 0) { //ready?
                if(TacticsUtility.canHit(me.position(), target, loadout.get(i))) { //can hit?
                    return Optional.of(Attack.values()[i]); //hit!
                }
            }
        }

        return Optional.empty();
    }

    //l'idea è che io devo attaccare, quindi mi metto in una posizione in cui mi avvicino per poter attaccare
    public static Optional<Input> moveForBestAim(EntityView me, Vec2D target, List<Ability> loadout) {

        var readyAttacks = loadout.stream()
            .filter(a -> a.type() == AbilityType.ATTACK) //o LIFESTEAL  
            .filter(a -> me.cooldownAbilities().get(loadout.indexOf(a)) == 0)
            .toList();

        if(!readyAttacks.isEmpty()) {
            Move bestMove = moveForBestAimfromLoadout(me, target, readyAttacks);
            return Optional.ofNullable(bestMove);
        }

        for (Ability ab : loadout) {
            if(TacticsUtility.canHit(me.position(), target, ab)) return Optional.empty();
        }

        Move bestMove = moveForBestAimfromLoadout(me, target, loadout);


        return Optional.ofNullable(bestMove);
    }

    //migliorabile
    private static Move moveForBestAimfromLoadout(EntityView me, Vec2D target, List<Ability> subsetLoadout) {
        
        Move bestMove = null;

        //Calcolo quanto sono messo bene ORA (Current Baseline)
        int currentBestDist = Integer.MAX_VALUE;
        for (Ability ab : subsetLoadout) {
            if (ab.type() != AbilityType.ATTACK) continue;
            // Calcolo distanza dalla posizione ATTUALE
            int dist = TacticsUtility.distFromHit(me.position(), target, ab);
            if (dist < currentBestDist) {
                currentBestDist = dist;
            }
        }

        // Se sono già in posizione perfetta (dist 0) e stiamo valutando abilità in cooldown,
        // currentBestDist sarà 0. Nessuna mossa futura potrà essere < 0, quindi bestMove resta null.
        // Risultato: Il nemico sta fermo (Camping riuscito).

        int minHitDistance = currentBestDist;

        //Valuto le mosse adiacenti
        for (Move move : Move.values()) {
            var posToEvaluate = TacticsUtility.applyMove(me.position(), move);
            
            if(!TacticsUtility.isValidPos(posToEvaluate)) continue; 

            for (Ability ab : subsetLoadout) {
                if (ab.type() != AbilityType.ATTACK) continue;
                
                int dist = TacticsUtility.distFromHit(posToEvaluate, target, ab);
                
                if( dist < minHitDistance ) {
                    minHitDistance = dist;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    //
    public static Optional<Input> tryToHeal(EntityView me, List<Ability> loadout) {
        var healAbIdx = TacticsUtility.abilityByType(loadout, AbilityType.HEAL);
        if(healAbIdx.isEmpty()) return Optional.empty();

        for (Integer idx : healAbIdx) {
            if(me.cooldownAbilities().get(idx) == 0) {
                return Optional.of(Attack.values()[idx]);
            }
        }
        return Optional.empty();
    }

    /**
     * cerca di massimizzare la distanza dal target.
     */
    public static Optional<Input> fleeFromTarget(EntityView me, EntityView target) {

        var retreatMoves = TacticsUtility.getMovesToRetreat(me.position(), target.position());

        if (!retreatMoves.isEmpty()) {
            Random rand = new Random();
            return Optional.of(retreatMoves.get(rand.nextInt(0,retreatMoves.size())));
        }
        return Optional.empty();
    }

    /**
     * Azione di Mantenimento cerca di restare in un range specifico.
     */
    public static Optional<Input> keepDistance(EntityView me, EntityView target, int minRange, int maxRange) {
        var moves = TacticsUtility.getMovesToMaintainRange(me.position(), target.position(), minRange, maxRange);

        if (!moves.isEmpty()) {
            Random rand = new Random();
            return Optional.of(moves.get(rand.nextInt(0,moves.size())));
        }
        return Optional.empty();
    }

    //wrapper per approach target
}
