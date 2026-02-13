package it.unibo.df.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.df.dto.EntityView;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

/**
 *  Utility class used to perform actions in strategies.
 */
public final class AiActions {

    private AiActions() { }

    /**
     * Try to cast an attack that hits.
     * 
     * @param me caster
     * @param target position 
     * @param loadout of caster
     * @return input
     */
    public static Optional<Input> tryBestAttack(final EntityView me, final Vec2D target, final List<Ability> loadout) {
        for (int i = 0; i < loadout.size(); i++) {
            if (me.cooldownAbilities().get(i) == 0) {
                if (TacticsUtility.canHit(me.position(), target, loadout.get(i))) {
                    return Optional.of(Attack.values()[i]);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Try to move into a position from which it can hit with an available attack,
     * otherwise it moves into a position from which it can aim.
     * 
     * @param me caster
     * @param target position 
     * @param loadout of caster
     * @return input
     */
    public static Optional<Input> moveForBestAim(final EntityView me, final Vec2D target, final List<Ability> loadout) {

        final var readyAttacks = loadout.stream()
            .filter(a -> a.type() == AbilityType.ATTACK)
            .filter(a -> me.cooldownAbilities().get(loadout.indexOf(a)) == 0)
            .toList();

        if (!readyAttacks.isEmpty()) {
            final Move bestMove = moveForBestAimfromLoadout(me, target, readyAttacks);
            return Optional.ofNullable(bestMove);
        }

        for (final Ability ab : loadout) {
            if (TacticsUtility.canHit(me.position(), target, ab)) {
                return Optional.empty();
            }
        }
        final Move bestMove = moveForBestAimfromLoadout(me, target, loadout);
        return Optional.ofNullable(bestMove);
    }

    private static Move moveForBestAimfromLoadout(final EntityView me, final Vec2D target, final List<Ability> subsetLoadout) {

        Move bestMove = null;

        int currentBestDist = Integer.MAX_VALUE;
        for (final Ability ab : subsetLoadout) {
            if (ab.type() != AbilityType.ATTACK) {
                continue;
            }

            final int dist = TacticsUtility.distFromHit(me.position(), target, ab);
            if (dist < currentBestDist) {
                currentBestDist = dist;
            }
        }

        int minHitDistance = currentBestDist;
        for (final Move move : Move.values()) {
            final var posToEvaluate = TacticsUtility.applyMove(me.position(), move);

            if (!TacticsUtility.isValidPos(posToEvaluate)) {
                continue;
            }

            for (final Ability ab : subsetLoadout) {
                if (ab.type() != AbilityType.ATTACK) {
                    continue;
                }

                final int dist = TacticsUtility.distFromHit(posToEvaluate, target, ab);
                if (dist < minHitDistance) {
                    minHitDistance = dist;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    /**
     * Try to cast an heal ability.
     * 
     * @param me caster
     * @param loadout of caster
     * @return input
     */
    public static Optional<Input> tryToHeal(final EntityView me, final List<Ability> loadout) {

        for (final Ability ab: loadout) {
            if (ab.type() == AbilityType.ATTACK) {
                continue;
            }
            final int idx = loadout.indexOf(ab);
            if (me.cooldownAbilities().get(idx) == 0) {
                return Optional.of(Attack.values()[idx]);
            }
        }
        return Optional.empty();
    }

    /**
     * Try to escape from the target.
     * 
     * @param me caster
     * @param target position
     * @return move
     */
    public static Optional<Input> fleeFromTarget(final EntityView me, final EntityView target) {

        final var retreatMoves = TacticsUtility.getMovesToRetreat(me.position(), target.position());
        final Random rand = new Random();

        if (!retreatMoves.isEmpty()) {
            return Optional.of(retreatMoves.get(rand.nextInt(0, retreatMoves.size())));
        }

       final List<Move> retreatMovesStepTwo = new ArrayList<>();
        for (final Move move : Move.values()) {
            final boolean validMove = !TacticsUtility.getMovesToRetreat(
                TacticsUtility.applyMove(me.position(), move), target.position()
            ).isEmpty();
            if (validMove) {
                retreatMovesStepTwo.add(move);
            }
        }

        if (!retreatMovesStepTwo.isEmpty()) {
            return Optional.of(retreatMovesStepTwo.get(rand.nextInt(0, retreatMovesStepTwo.size())));
        }

        return Optional.empty();
    }

    /**
     * Try to keep a certain distance from the target is maintained.
     * 
     * @param me caster
     * @param target position
     * @param minRange min distance to keep
     * @param maxRange max distance to keep
     * @return move
     */
    public static Optional<Input> keepDistance(
        final EntityView me, 
        final EntityView target, 
        final int minRange, 
        final int maxRange
    ) {
        final var moves = TacticsUtility.getMovesToMaintainRange(me.position(), target.position(), minRange, maxRange);
        if (!moves.isEmpty()) {
            final Random rand = new Random();
            return Optional.of(moves.get(rand.nextInt(0, moves.size())));
        }
        return Optional.empty();
    }
}
