package it.unibo.df.gs;

import java.util.List;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;

/**
 * Represents an entity in the game with position, health, and abilities.
 */
public class Entity{
    
    private Vec2D position;
    private int hp;
    private final int maxHp;
    private final List<Ability> loadout;

    /**
     * Constructs an Entity with the specified position, health, and abilities.
     * @param position
     * @param hp
     * @param loadout
     */
    
    public Entity(final Vec2D position, final int hp, final List<Ability> loadout) {
        this.position = position;
        this.hp = hp;
        this.maxHp = hp;
        this.loadout = loadout;
    }
}
