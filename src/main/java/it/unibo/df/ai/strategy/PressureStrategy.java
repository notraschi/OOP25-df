package it.unibo.df.ai.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.df.ai.AiStrategy;
import it.unibo.df.ai.util.AiActions;
import it.unibo.df.ai.util.CurvesUtility;
import it.unibo.df.ai.util.TacticsUtility;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

public class PressureStrategy implements AiStrategy{

    private final int idEntity;
    private int special;
    private double momentToCastSpecial = 0.6;
    private Vec2D aimFocus = null;

    public PressureStrategy(int idEntity) {
        this.idEntity = idEntity;
        this.special = 2;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();


       
        //momento di castare special
        if (me.hpRatio() < momentToCastSpecial && special > 0) {
            special -= 1;
            momentToCastSpecial = 0.3;
            System.out.println("cast SPECIAL by "+idEntity);
            return Optional.of(Attack.SPECIAL);
        }

        // 60% di probabilità di aggiornare la mira
        double reflexChance = 0.30;
        if (Math.random() < reflexChance) {
            System.out.println("aggiorno");
            //aggiunta disturbo
            aimFocus = applyNoise(player.position()); 
        }

        //mena troppo, devono missare, 
        Optional<Input> attack = AiActions.tryBestAttack(me, aimFocus, loadout);
        if (attack.isPresent()) return attack;

        //si riposiziona in modo corretto
        Optional<Input> aimMove = AiActions.moveForBestAim(me, aimFocus, loadout);
        if (aimMove.isPresent()) return aimMove;
            
        System.out.println("pressure" + idEntity +"--"+ me.hp());

        //sta fermo/potrei andare addosso al player (?)
        return Optional.empty();       
    }

    @Override //PRESSURE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();
        if(aimFocus == null) aimFocus = player.position();


        // Se ho poca vita, la mia voglia di pressare cala.
        // hp 100% -> 0.98
        // hp 50%  -> 0.65
        // hp 20% -> 0.23
        double confidence = CurvesUtility.logistic(me.hpRatio(), 6, 0.4);//CurvesUtility.exponential(me.hpRatio(), 2);

        var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK); //abilita con cui attacco
        
        //mi calcolo i disponibili sui totali 
        // 0 / 2 -> 0
        // 1 / 2 -> 0.5
        // 2 / 2 -> 1
        double ammoScore = (double) ammo.stream().filter(x -> me.cooldownAbilities().get(x) == 0).count() / (double) ammo.size();

        //meno vita ha il nemico meglio piu voglia di pressare ho
        //trattiamo hp player
        // hp 100% -> 0.08
        // hp 50% -> 0.5
        // hp 10% -> 0.88
        double bloodlust = CurvesUtility.logistic(player.hpRatio(),5,0.5); //vita del nemico FORSE MEGLIO ESPONENZIALE

        double utility = //+- da bilanciare meglio
              0.35 * confidence
            + 0.35 * bloodlust
            + 0.15 * ammoScore
            + 0.15 * ammo.size() * 0.1;

        if (AiActions.tryBestAttack(me, this.aimFocus, loadout).isPresent()) {
            utility += 0.3; //posso colpire ORA allora gli do questo bonus !!
        }

        //System.out.println(utility +"--"+idEntity+"--PRESSURE");

        return CurvesUtility.clamp(utility);
    }


    // Helper per il disturbo
    private Vec2D applyNoise(Vec2D realPos) {
        // 20% di chance di sbagliare di 1 casella anche quando aggiorno
        if (Math.random() < 0.20) {
            int dx = (int)(Math.random() * 3) - 1; 
            int dy = (int)(Math.random() * 3) - 1;
            return new Vec2D(realPos.x() + dx, realPos.y() + dy);
        }
        return realPos;
    }
}
