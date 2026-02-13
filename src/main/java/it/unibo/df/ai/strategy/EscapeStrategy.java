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

        //si ritira
        return AiActions.fleeFromTarget(me, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateUtility(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();

        //all'inizio non scappo tanto, alla fine non scappo tanto do il tutto per tutto, scappo di piu a mid game
        final double fear = CurvesUtility.gaussian(me.hpRatio(), 0.4, 0.3);

        //Rischio immediato ovvero se sono vicino al player
        //normalizzazion mi da fuori questo
        // 0 -> 0.0
        // 1 -> 0.055             danger -> 1
        // 6 -> 0.333             danger -> 0.70
        // 9 -> 0.5 (meta mappa)  danger -> 0.25
        final int dist = TacticsUtility.manhattanDist(me.position(), player.position());
        final double dist01 = TacticsUtility.normalizeManhattanDist(dist);
        final double danger = CurvesUtility.logistic(CurvesUtility.inverse(dist01), 10, 0.7);

        //se ho cooldown attivi allora ho paura, SOLO PER ATTACK
        //mi calcolo i disponibili sui totali
        // 0 / 2 -> 0
        // 1 / 2 -> 0.5
        // 2 / 2 -> 1
        final var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK);
        final double helplessScore = (double) ammo.stream()
            .filter(x -> me.cooldownAbilities().get(x) > 0)
            .count() / (double) ammo.size();

        final double score = helplessScore * danger * fear;
        return CurvesUtility.clamp(score);
    }
}
