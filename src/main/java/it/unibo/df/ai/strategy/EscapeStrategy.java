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
 * His goal is to escape when he has no ability to use.
 */
public class EscapeStrategy implements AiStrategy {

    private static final double FEAR_TARGET = 0.4;
    private static final double FEAR_DEVIATION = 0.3;

    private static final double DANGER_STEEPNESS = 10.0;
    private static final double DANGER_MIDPOINT = 0.7;
    private final int idEntity;

    /**
     * Constructor, take the identity of the enemy who owns the strategy.
     * 
     * @param idEntity of enemy
     */
    public EscapeStrategy(final int idEntity) {
        this.idEntity = idEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Input> computeNextAction(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();
        return AiActions.fleeFromTarget(me, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateUtility(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();

        final double fear = CurvesUtility.gaussian(me.hpRatio(), FEAR_TARGET, FEAR_DEVIATION);

        final int dist = TacticsUtility.manhattanDist(me.position(), player.position());
        final double dist01 = TacticsUtility.normalizeManhattanDist(dist);
        final double danger = CurvesUtility.logistic(CurvesUtility.inverse(dist01), DANGER_STEEPNESS, DANGER_MIDPOINT);

        final var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK);
        final double helplessScore = (double) ammo.stream()
            .filter(x -> me.cooldownAbilities().get(x) > 0)
            .count() / (double) ammo.size();

        final double score = helplessScore * danger * fear;
        return CurvesUtility.clamp(score);
    }
}
