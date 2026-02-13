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

/**
 * Has as its goal to cure itself.
 */
public class StabilizeStrategy implements AiStrategy {

    private static final int INITIAL_HEAL_RESOURCE = 3;

    private static final double HP_THRESHOLD_SAFE = 0.8;
    private static final double SCORE_HEAL_READY = 1.0;
    private static final double SCORE_HEAL_COOLDOWN = 0.5;

    private static final double PANIC_TARGET = 0.3;
    private static final double PANIC_DEVIATION = 0.21;

    private final int idEntity;
    private int healResource;

    /**
     * Constructor, take the identity of the enemy who owns the strategy.
     * 
     * @param idEntity of enemy
     */
    public StabilizeStrategy(final int idEntity) {
        this.idEntity = idEntity;
        this.healResource = INITIAL_HEAL_RESOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Input> computeNextAction(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();

        final Optional<Input> healInput = AiActions.tryToHeal(me, loadout);
        if (healInput.isPresent() && healResource > 0) {
            //System.out.println("AI: "+ idEntity + " -> Healing!");
            healResource -= 1;
            return healInput;
        }
        return AiActions.fleeFromTarget(me, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateUtility(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);

        //CONTROLLARE SE HO CURE
        final boolean hasHeal = !TacticsUtility.abilityByType(loadout, AbilityType.HEAL).isEmpty();
        final boolean hasLifeSteel = !TacticsUtility.abilityByType(loadout, AbilityType.LIFESTEAL).isEmpty();
        if (!hasHeal && !hasLifeSteel) {
            return 0.0;
        }

        //hp alti, non mi curo o cure finite
        if (me.hpRatio() > HP_THRESHOLD_SAFE || healResource <= 0) {
            return 0.0;
        }
        final var healIdx = TacticsUtility.abilityByType(loadout, AbilityType.HEAL);
        final double healReady = healIdx.stream()
            .anyMatch(i -> me.cooldownAbilities().get(i) == 0) 
                ? SCORE_HEAL_READY 
                : SCORE_HEAL_COOLDOWN;

        //mi curo maggiormente nel renge tra 20% e 40% di vita
        final double panic = CurvesUtility.gaussian(me.hpRatio(), PANIC_TARGET, PANIC_DEVIATION);
        final double utility = panic * healReady;

        return CurvesUtility.clamp(utility);
    }
}
