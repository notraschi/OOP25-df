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

public class StabilizeStrategy implements AiStrategy {

    private final int idEntity;
    private int healResource;

    public StabilizeStrategy(int idEntity) {
        this.idEntity = idEntity;
        this.healResource = 3;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();
        
        Optional<Input> healInput = AiActions.tryToHeal(me, loadout);
        if (healInput.isPresent() && healResource > 0) {
            System.out.println("AI: Stabilize -> Healing! -> "+idEntity);
            healResource -= 1;
            return healInput;
        }

        System.out.println("stabilize" + idEntity +"--"+ me.hp());
        //indietreggio aspettando la cura

        return AiActions.fleeFromTarget(me, player);
    }

    @Override //STABILIZE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        //CONTROLLARE SE HO CURE
        boolean hasHeal = !TacticsUtility.abilityByType(loadout, AbilityType.HEAL).isEmpty();
        boolean hasLifeSteel = !TacticsUtility.abilityByType(loadout, AbilityType.LIFESTEAL).isEmpty();
        if (!hasHeal && !hasLifeSteel) return 0.0; //non ho cure

        //hp alti, non mi curo o cure finite
        if (me.hpRatio() > 0.8 || healResource <= 0) return 0.0;

        var healIdx = TacticsUtility.abilityByType(loadout, AbilityType.HEAL);
        double healReady = healIdx.stream().anyMatch(i -> me.cooldownAbilities().get(i) == 0) ? 1.0 : 0.5; //desidero curarmi

        //mi curo maggiormente nel renge tra 20% e 40% di vita
        double panic = CurvesUtility.gaussian(me.hpRatio(), 0.3,0.21);
        double utility = panic * healReady;
        
        //System.out.println(utility +"--"+idEntity+"--STABILIZE");

        // Clamping finale 
        return CurvesUtility.clamp(utility);
    }

}
