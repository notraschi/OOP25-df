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
import it.unibo.df.utility.Vec2D;

/**
 * Its objective is to put pressure on the player, get closer and attack him.
 */
public class PressureStrategy implements AiStrategy {

    private static final int INITIAL_SPECIAL_CHARGES = 2;
    private static final double SPECIAL_CAST_THRESHOLD_START = 0.6;
    private static final double SPECIAL_CAST_THRESHOLD_NEXT = 0.3;

    private static final double NOISE_CHANCE = 0.20;
    private static final double REFLEX_CHANCE = 0.30;

    private static final double CONFIDENCE_SLOPE = 6.0;
    private static final double CONFIDENCE_MIDPOINT = 0.4;
    private static final double BLOODLUST_SLOPE = 5.0;
    private static final double BLOODLUST_MIDPOINT = 0.5;

    private static final double WEIGHT_CONFIDENCE = 0.35;
    private static final double WEIGHT_BLOODLUST = 0.35;
    private static final double WEIGHT_AMMO_SCORE = 0.15;
    private static final double WEIGHT_AMMO_COUNT = 0.15;
    private static final double ATTACK_READY_BONUS = 0.3;

    private final int idEntity;
    private int special;
    private double momentToCastSpecial;
    private Vec2D aimFocus;

    /**
     * Constructor, take the identity of the enemy who owns the strategy.
     * 
     * @param idEntity of enemy
     */
    public PressureStrategy(final int idEntity) {
        this.idEntity = idEntity;
        this.special = INITIAL_SPECIAL_CHARGES;
        this.momentToCastSpecial = SPECIAL_CAST_THRESHOLD_START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Input> computeNextAction(
        final CombatState cs, 
        final List<Ability> loadout
    ) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();

        if (me.hpRatio() < momentToCastSpecial && special > 0) {
            special -= 1;
            momentToCastSpecial = SPECIAL_CAST_THRESHOLD_NEXT;
            return Optional.of(Attack.SPECIAL);
        }

        if (Math.random() < REFLEX_CHANCE) {
            aimFocus = applyNoise(player.position()); 
        }

        final Optional<Input> attack = AiActions.tryBestAttack(me, aimFocus, loadout);
        if (attack.isPresent()) {
            return attack;
        }

        final Optional<Input> aimMove = AiActions.moveForBestAim(me, aimFocus, loadout);
        if (aimMove.isPresent()) {
            return aimMove;
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateUtility(
        final CombatState cs, 
        final List<Ability> loadout
    ) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();
        if (aimFocus == null) {
            aimFocus = player.position();
        }

        final double confidence = 
            CurvesUtility.logistic(me.hpRatio(), CONFIDENCE_SLOPE, CONFIDENCE_MIDPOINT);

        final var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK);

        final double ammoScore = (double) ammo.stream()
            .filter(x -> me.cooldownAbilities().get(x) == 0)
            .count() / (double) ammo.size();

        final double bloodlust =
            CurvesUtility.logistic(player.hpRatio(), BLOODLUST_SLOPE, BLOODLUST_MIDPOINT);

        double utility =
              WEIGHT_CONFIDENCE * confidence
            + WEIGHT_BLOODLUST * bloodlust
            + WEIGHT_AMMO_SCORE * ammoScore
            + WEIGHT_AMMO_COUNT * ammo.size(); //0.1 

        if (AiActions.tryBestAttack(me, this.aimFocus, loadout).isPresent()) {
            utility += ATTACK_READY_BONUS;
        }
        return CurvesUtility.clamp(utility);
    }

    // Helper implemets noise
    private Vec2D applyNoise(final Vec2D realPos) {
        // 20% di chance to get noise
        if (Math.random() < NOISE_CHANCE) {
            final int dx = (int) (Math.random() * 3) - 1; 
            final int dy = (int) (Math.random() * 3) - 1;
            return new Vec2D(realPos.x() + dx, realPos.y() + dy);
        }
        return realPos;
    }
}
