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

/**
 * Its objective is to put pressure on the player, get closer and attack him.
 */
public class PressureStrategy implements AiStrategy {

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
        this.special = 2;
        this.momentToCastSpecial = 0.6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Input> computeNextAction(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();

        if (me.hpRatio() < momentToCastSpecial && special > 0) {
            special -= 1;
            momentToCastSpecial = 0.3;
            return Optional.of(Attack.SPECIAL);
        }

        final double reflexChance = 0.30;
        if (Math.random() < reflexChance) {
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
    public double calculateUtility(final CombatState cs, final List<Ability> loadout) {
        final var me = cs.enemies().get(idEntity);
        final var player = cs.player();
        if (aimFocus == null) {
            aimFocus = player.position();
        }
        // Se ho poca vita, la mia voglia di pressare cala.
        // hp 100% -> 0.98
        // hp 50%  -> 0.65
        // hp 20% -> 0.23
        final double confidence = CurvesUtility.logistic(me.hpRatio(), 6, 0.4);

        final var ammo = TacticsUtility.abilityByType(loadout, AbilityType.ATTACK);

        //mi calcolo i disponibili sui totali 
        // 0 / 2 -> 0
        // 1 / 2 -> 0.5
        // 2 / 2 -> 1
        final double ammoScore = (double) ammo.stream()
            .filter(x -> me.cooldownAbilities().get(x) == 0)
            .count() / (double) ammo.size();

        //meno vita ha il nemico meglio piu voglia di pressare ho
        //trattiamo hp player
        // hp 100% -> 0.08
        // hp 50% -> 0.5
        // hp 10% -> 0.88
        final double bloodlust = CurvesUtility.logistic(player.hpRatio(), 5, 0.5);

        double utility =
              0.35 * confidence
            + 0.35 * bloodlust
            + 0.15 * ammoScore
            + 0.15 * ammo.size() * 0.1;

        if (AiActions.tryBestAttack(me, this.aimFocus, loadout).isPresent()) {
            utility += 0.3;
        }

        //System.out.println(utility +"--"+idEntity+"--PRESSURE");

        return CurvesUtility.clamp(utility);
    }

    // Helper implemets noise
    private Vec2D applyNoise(final Vec2D realPos) {
        // 20% di chance to get noise
        if (Math.random() < 0.20) {
            final int dx = (int) (Math.random() * 3) - 1; 
            final int dy = (int) (Math.random() * 3) - 1;
            return new Vec2D(realPos.x() + dx, realPos.y() + dy);
        }
        return realPos;
    }
}
