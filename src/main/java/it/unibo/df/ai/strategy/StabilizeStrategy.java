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

    public StabilizeStrategy(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public Optional<Input> computeNextAction(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        int dist = TacticsUtility.manhattanDist(me.position(), player.position()); 

        //prova anche se è in pericolo il che non va bene, impongo almeno distanza 2 +-
        if(dist < 3)  return AiActions.fleeFromTarget(me, player);
        
        Optional<Input> healInput = AiActions.tryToHeal(me, loadout);
        if (healInput.isPresent()) {
            System.out.println("AI: Stabilize -> Healing!");
            return healInput;
        }

        System.out.println("stabilize");
        //indietreggio aspettando la cura

        return AiActions.fleeFromTarget(me, player);
    }

    @Override //STABILIZE
    public double calculateUtility(CombatState cs, List<Ability> loadout) {
        var me = cs.enemies().get(idEntity);
        var player = cs.player();

        //posso killarlo (?) TODO

        //CONTROLLARE SE HO CURE
        boolean hasHeal = !TacticsUtility.abilityByType(loadout, AbilityType.HEAL).isEmpty();
        boolean hasLifeSteel = !TacticsUtility.abilityByType(loadout, AbilityType.LIFESTEAL).isEmpty();
        if (!hasHeal && !hasLifeSteel) return 0.0; //non ho cure

        //hp alti, non mi curo.
        if (me.hpRatio() > 0.8) return 0.0;

        var healIdx = TacticsUtility.abilityByType(loadout, AbilityType.HEAL);
        double healReady = healIdx.stream().anyMatch(i -> me.cooldownAbilities().get(i) == 0) ? 1.0 : 0.5; //desidero curarmi

        //cresce esponenzialmente al calare della vita
        double panic = CurvesUtility.exponential(CurvesUtility.inverse(me.hpRatio()), 3);

        double utility = panic * healReady;
        
        // Clamping finale 
        return CurvesUtility.clamp(utility);
    }

}
