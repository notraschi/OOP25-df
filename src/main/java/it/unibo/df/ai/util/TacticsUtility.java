package it.unibo.df.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import it.unibo.df.configurations.Constants;
import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Utility class that provides useful methods for modeling actions.
 */
public final class TacticsUtility {

    private static final int BOARD_SIZE = Constants.BOARD_SIZE;

    private TacticsUtility() { }

    /**
     * Calculate the distance between two points considering only the 4 directions to move.
     * 
     * @param a first point
     * @param b secondo point
     * @return distance from a to b
     */
    public static int manhattanDist(final Vec2D a, final Vec2D b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /**
     * Calculate the distance between two points considering the 8 directions to move.
     * 
     * @param a first point
     * @param b second point
     * @return distance from a to b
     */
    public static int chebyshevDist(final Vec2D a, final Vec2D b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    /**
     * Check if two point is are adjacent.
     * 
     * @param a first point
     * @param b second point
     * @return boolean
     */
    public static boolean isAdjacent(final Vec2D a, final Vec2D b) {
        return manhattanDist(a, b) == 1; 
    }

    /**
     * Calculate the normalization of a distance ranging from 0 to BOARD_SIZE.
     * 
     * @param value input
     * @return the value between 0.0 and 1.0
     */
    public static double normalizeDist(final int value) {
        return value / (double) BOARD_SIZE;
    }

    /**
     * Calculate the normalization of a manhattan dist.
     *
     * @param dist input
     * @return the dist between 0.0 and 1.0
     */
    public static double normalizeManhattanDist(final int dist) {
        final int max = (BOARD_SIZE - 1) * 2;
        return Math.max(0.0, Math.min(1.0, dist / (double) max));
    }

    /**
     * Calculates moves that reduce the distance from start to target.
     * 
     * @param start position
     * @param target position
     * @return list of possible moves
     */
    public static List<Move> getMovesToApproach(final Vec2D start, final Vec2D target) {
        final List<Move> validMoves = new ArrayList<>();
        final int currentDist = manhattanDist(start, target);
        for (final Move move : Move.values()) {
            final Vec2D futurePos = applyMove(start, move);
            if (isValidPos(futurePos) && !isAdjacent(start, target) && manhattanDist(futurePos, target) < currentDist) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Calculates moves that increment the distance from start to target.
     * 
     * @param start position
     * @param target position
     * @return list of possible moves
     */
    public static List<Move> getMovesToRetreat(final Vec2D start, final Vec2D target) {
        final List<Move> movesToIncreareDist = new ArrayList<>();
        final int currentDist = manhattanDist(start, target); 
        for (final Move move : Move.values()) {
            final Vec2D futurePos = applyMove(start, move);
            final int futureDist = manhattanDist(futurePos, target);
            if (isValidPos(futurePos) && futureDist < BOARD_SIZE && futureDist > currentDist) {
                movesToIncreareDist.add(move);
            }
        }
        return movesToIncreareDist;
    }

    /**
     * Calculates the possible moves to keep the distance from the target in a specific range.
     * 
     * @param start position
     * @param target position
     * @param minRange min manhattan distance
     * @param maxRange max manhattan distance
     * @return list of possible move
     */
    public static List<Move> getMovesToMaintainRange(
        final Vec2D start, 
        final Vec2D target, 
        final int minRange, 
        final int maxRange
    ) {
        final List<Move> movesToManteinDist = new ArrayList<>();
        final int currentDist = manhattanDist(start, target);

        if (currentDist > maxRange) {
            return getMovesToApproach(start, target);
        }

        if (currentDist < minRange) {
            return getMovesToRetreat(start, target);
        }

        for (final Move move : Move.values()) {
            final Vec2D futurePos = applyMove(start, move);
            final int futureDist = manhattanDist(futurePos, target);

            if (isValidPos(futurePos) && futureDist >= minRange && futureDist <= maxRange) {
                movesToManteinDist.add(move);
            }
        }
        return movesToManteinDist;
    }

    /**
     * Check if the position is on the board.
     * 
     * @param a position
     * @return boolean
     */
    public static boolean isValidPos(final Vec2D a) {
        return a.x() >= 0 && a.x() < BOARD_SIZE
            && a.y() >= 0 && a.y() < BOARD_SIZE;
    }

    /**
     * Calculate a position from a move.
     * 
     * @param pos current position
     * @param move to apply
     * @return new position
     */
    public static Vec2D applyMove(final Vec2D pos, final Move move) {
        return switch (move) {
            case UP -> new Vec2D(pos.x(), pos.y() - 1);
            case DOWN -> new Vec2D(pos.x(), pos.y() + 1);
            case LEFT -> new Vec2D(pos.x() - 1, pos.y());
            case RIGHT -> new Vec2D(pos.x() + 1, pos.y());
        };
    }

    /**
     * Checks if the ability cast by the caster hits the target.
     * 
     * @param caster position
     * @param target position
     * @param ability to cast from caster
     * @return boolean
     */
    public static boolean canHit(final Vec2D caster, final Vec2D target, final Ability ability) {
        return ability.effect()
            .apply(caster)
            .map(area -> area.contains(target))
            .orElse(false);
    }

    /**
     * Calculates the min Manhattan distance from the ability area to the target.
     * 
     * @param caster position
     * @param target position
     * @param ability to cast from caster
     * @return shortest Manhattan distance
     */
    public static int distFromHit(final Vec2D caster, final Vec2D target, final Ability ability) {
        return ability.effect()
            .apply(caster)
            .map(area -> area.stream()
                .map(cell -> manhattanDist(cell, target))
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE)
            )
            .orElse(Integer.MAX_VALUE);
    }

    /**
     * Takes the abilities of the specified type.
     * 
     * @param loadout list of abilities
     * @param type of ability
     * @return list of ability indexes
     */
    public static List<Integer> abilityByType(final List<Ability> loadout, final AbilityType type) {
        return Stream.iterate(0, x -> x + 1)
            .limit(loadout.size())
            .filter(idx -> loadout.get(idx).type() == type)
            .toList();
    }
}
