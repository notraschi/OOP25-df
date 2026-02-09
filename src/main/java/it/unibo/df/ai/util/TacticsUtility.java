package it.unibo.df.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

public final class TacticsUtility {
    
    private final static int BOARD_SIZE = 10;

    private TacticsUtility() { }

    //dist considering only the directon (up, down, left, right)
    public static int manhattanDist(final Vec2D a, final Vec2D b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    //dist considering all direction
    public static int chebyshevDist(final Vec2D a, final Vec2D b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    public static boolean isAdjacent(final Vec2D a, final Vec2D b) { //to controll 8 pos use chebyshev
        return manhattanDist(a,b) == 1; 
    }

    public static double normalizeDist(int value) {
        return value / (double) BOARD_SIZE; //cast to double
    }

    //pressure
    public static List<Move> getMovesToApproach(final Vec2D start, final Vec2D target) {
        List<Move> validMoves = new ArrayList<>();
        int currentDist = manhattanDist(start, target);
        for (Move move : Move.values()) {
            Vec2D futurePos = applyMove(start, move);
            if (isValidPos(futurePos) && !isAdjacent(start, target) && manhattanDist(futurePos, target) < currentDist) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    //escape
    public static List<Move> getMovesToRetreat(final Vec2D start, final Vec2D target) {
        List<Move> movesToIncreareDist = new ArrayList<>();
        int currentDist = manhattanDist(start, target); 
        for (Move move : Move.values()) {
            Vec2D futurePos = applyMove(start, move);
            int futureDist = manhattanDist(futurePos, target);
            if (isValidPos(futurePos) && futureDist < BOARD_SIZE && futureDist > currentDist ) { //maximum distance to retreat
                movesToIncreareDist.add(move);
            }
        }
        return movesToIncreareDist;
    }

    //stabilize
    public static List<Move> getMovesToMaintainRange(final Vec2D start, final Vec2D target, int minRange, int maxRange) {
        List<Move> movesToManteinDist = new ArrayList<>();
        int currentDist = manhattanDist(start, target);

        if (currentDist > maxRange) {
            return getMovesToApproach(start, target);
        }
    
        if (currentDist < minRange) {
            return getMovesToRetreat(start, target);
        }

        for (Move move : Move.values()) {
            Vec2D futurePos = applyMove(start, move);
            int futureDist = manhattanDist(futurePos, target);
            
            if (isValidPos(futurePos) && futureDist >= minRange && futureDist <= maxRange) {
                movesToManteinDist.add(move);
            }
        }
        return movesToManteinDist;
    }

    //
    private static boolean isValidPos(final Vec2D a) {
        return a.x() >= 0 && a.x() < BOARD_SIZE
            && a.y() >= 0 && a.y() < BOARD_SIZE;
    }

    //
    public static Vec2D applyMove(Vec2D pos, Move move) {
        return switch (move) {
            case UP -> new Vec2D(pos.x(), pos.y() - 1);
            case DOWN -> new Vec2D(pos.x(), pos.y() + 1);
            case LEFT -> new Vec2D(pos.x() - 1, pos.y());
            case RIGHT -> new Vec2D(pos.x() + 1, pos.y());
        };
    }

    //
    public static boolean canHit(Vec2D caster, Vec2D target, Ability ability) {
        return ability.effect()
            .apply(caster)
            .map(area -> area.contains(target))
            .orElse(false);
    }

    //metodo che valuta la distanza del target all'area del colpo, attenzione ad usarlo quando player viene colpito
    public static int distFromHit(Vec2D caster, Vec2D target, Ability ability) {
        return ability.effect()
            .apply(caster)
            .map(area -> area.stream()
                .map(cell -> manhattanDist(cell, target))
                .min(Integer::compareTo)
                .orElse(-1))
            .orElse(-1);
    }

    //prendo le abilita in base al tipo
    public static List<Integer> abilityByType(List<Ability> loadout, AbilityType type) {
        return Stream.iterate(0, x -> x+1)
            .limit(loadout.size())
            .filter(idx -> loadout.get(idx).type().equals(type))
            .toList();
    }
}

/**
 * + metodo set mosse migliore x targettarlo
 * + metodo set mosse migliore x andare lontano(safe)
 * + metodo set mosse x restare in un range, se sono nel range magari sto fermo
 * + metodo controllo se siamo adiacenti
 * + metodo controllo posizione valida
 * + normalizzare distanza 
 * 
 */