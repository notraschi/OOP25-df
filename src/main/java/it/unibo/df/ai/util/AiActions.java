package it.unibo.df.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import it.unibo.df.dto.EntityView;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Input;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.utility.Vec2D;

/**
 *  Utility class used to perform actions in strategies.
 */
public final class AiActions {

    private static final Random RANDOM = new Random();

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
        return IntStream.range(0, loadout.size())
            .filter(i -> me.cooldownAbilities().get(i) == 0)
            .filter(i -> TacticsUtility.canHit(me.position(), target, loadout.get(i)))
            .mapToObj(i -> (Input) Attack.values()[i])
            .findFirst();
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

        final var readyAttacks = IntStream.range(0, loadout.size())
            .filter(i -> loadout.get(i).type() == AbilityType.ATTACK)
            .filter(i -> me.cooldownAbilities().get(i) == 0)
            .mapToObj(loadout::get)
            .toList();

        if (!readyAttacks.isEmpty()) {
            return Optional.ofNullable(moveForBestAimfromLoadout(me, target, readyAttacks));
        }

        final boolean canHitNow = loadout.stream()
            .anyMatch(ab -> TacticsUtility.canHit(me.position(), target, ab));

        if (canHitNow) {
            return Optional.empty();
        }

        return Optional.ofNullable(moveForBestAimfromLoadout(me, target, loadout));
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

        return IntStream.range(0, loadout.size())
            .filter(i -> loadout.get(i).type() != AbilityType.ATTACK)
            .filter(i -> me.cooldownAbilities().get(i) == 0)
            .mapToObj(i -> (Input) Attack.values()[i])
            .findFirst();
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

        if (!retreatMoves.isEmpty()) {
            return Optional.of(retreatMoves.get(RANDOM.nextInt(0, retreatMoves.size())));
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
            return Optional.of(retreatMovesStepTwo.get(RANDOM.nextInt(0, retreatMovesStepTwo.size())));
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
            return Optional.of(moves.get(RANDOM.nextInt(0, moves.size())));
        }
        return Optional.empty();
    }
}
