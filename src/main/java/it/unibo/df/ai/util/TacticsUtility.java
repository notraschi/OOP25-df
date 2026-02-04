package it.unibo.df.ai.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import it.unibo.df.input.Move;
import it.unibo.df.model.abilities.Vec2D;

public class TacticsUtility {
    
    private final static Integer BOARD_SIZE = 10;

    //dist considering only the directon (up, down, left, right)
    public static int manhattanDist(final Vec2D a, final Vec2D b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    //dist considering all direction
    public static int chebyshevDist(final Vec2D a, final Vec2D b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    public static boolean isAdjacent(final Vec2D a, final Vec2D b) {
        return chebyshevDist(a,b) <= 1; 
    }

    public static double normalizeDist(int value) {
        return value / BOARD_SIZE;
    }


    //pressure
    public static List<Move> getMovesToTargetting(final Vec2D start, final Vec2D target) {
        List<Move> validMoves = new ArrayList<>();
        int currentDist = manhattanDist(start, target); 
        for (Move move : Move.values()) {
            Vec2D futurePos = applyMove(start, move);
            if (validPos(futurePos) && manhattanDist(futurePos, target) < currentDist && ! futurePos.equals(target)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }



    private static boolean validPos(final Vec2D a) {
        return a.x() >= 0 && a.x() < BOARD_SIZE
            && a.y() >= 0 && a.y() < BOARD_SIZE;
    }



    public static Vec2D applyMove(Vec2D pos, Move move) {
        return switch (move) {
            case UP -> new Vec2D(pos.x(), pos.y() - 1);
            case DOWN -> new Vec2D(pos.x(), pos.y() + 1);
            case LEFT -> new Vec2D(pos.x() - 1, pos.y());
            case RIGHT -> new Vec2D(pos.x() + 1, pos.y());
            // Il compilatore Java potrebbe richiedere default o coprire tutti i casi se Move è enum
        };
    }
}

/**
 * - metodo set mosse migliore x targettarlo
 * - metodo set mosse migliore x andare lontano(safe)
 * - metodo set mosse x restare in un range, se sono nel range magari sto fermo
 * + metodo controllo se siamo adiacenti
 * + metodo controllo posizione valida
 * + normalizzare distanza 
 * 
 */