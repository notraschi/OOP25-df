package it.unibo.df.ai.util;

import it.unibo.df.model.abilities.Vec2D;

public class TacticsUtils {
    
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

    private boolean validPos(final Vec2D a) {
        return a.x() >= 0 && a.x() < BOARD_SIZE
            && a.y() >= 0 && a.y() < BOARD_SIZE;
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