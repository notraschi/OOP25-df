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

public class PressureStrategy implements AiStrategy{

    private final int idEntity;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();
       
        //mena
        Optional<Input> attack = AiActions.tryBestAttack(me, player, loadout);
        if (attack.isPresent()) return attack;

        //si riposiziona in modo corretto
        Optional<Input> aimMove = AiActions.moveForBestAim(me, player, loadout);
        if (aimMove.isPresent()) return aimMove;
            
        System.out.println("pressure");

        //sta fermo/potrei andare addosso al player (?)
        return Optional.empty();       
    }

    @Override //PRESSURE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();


        // Se ho poca vita, la mia voglia di pressare DEVE crollare per far vincere Escape.
        // HP 100% -> Confidence 1.0
        // HP 50%  -> Confidence 0.25 Crollo rapido! attento che non sia TROPPO RAPIDO
        double confidence = CurvesUtility.exponential(me.hpRatio(), 2);

        var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK);
        
        //mi calcolo i disponibili
        double ammoScore = (double) ammo.stream().filter(x -> me.cooldownAbilities().get(x) == 0).count() / ammo.size();

        double bloodlust = CurvesUtility.inverse(player.hpRatio()); //vita del nemico FORSE MEGLIO ESPONENZIALE

        double utility = confidence * (ammoScore * 0.4 + ammo.size() * 0.1  + bloodlust * 0.4);

        if (AiActions.tryBestAttack(me, player, loadout).isPresent()) {
            utility += 0.3; //posso colpire ORA!!
        }

        return CurvesUtility.clamp(utility);
    }

    /** note personali
     * fattori che considero
     * 
     * - posso colpire
     *      - pronto e sottotiro!
     * - pronto ma non sottotiro
     * vantaggio hp
     * posso killarlo
     * - piccolo bonus se sono vicino
     * 
     */
}
