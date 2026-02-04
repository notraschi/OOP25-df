package it.unibo.df.ai.util;

import it.unibo.df.model.abilities.Vec2D;

public class TacticsUtils {
    
    //dist considering only the movement option (up, down, left, right)
    public static int manhattanDist(final Vec2D a, final Vec2D b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    //dist considering all direction
    public static int chebyshevDist(final Vec2D a, final Vec2D b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}

/**
 * - metodo set mosse migliore x andargli sopra
 * - metodo set mosse migliore x andare lontano
 * - metodo set mosse x restare in un range
 * - metodo controllo se siamo adiacenti
 * - metodo controllo posizione valida
 * 
 */