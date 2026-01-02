package it.unibo.df.model.combat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;

/**
 * gamemodel.
 */
public class CombatModel {
    private Entity player;
    private Map<Integer, Entity> enemies;
    private int boardSize;

    /**
     * moves an entity.
     * 
     * @param entityId id of the enemy (if one)
     * @param delta how much to move
     */
    public void move(Optional<Integer> entityId, Vec2D delta) {
        Entity target = entityId.map(enemies::get).orElse(player);
        target.move(delta);
    }

    /**
     * casts an ability.
     * 
     * @param entityId id of the enemy (if one)
     * @param ability ability to cast
     */
    public void cast(Optional<Integer> entityId, int ability) {
        if (entityId.isEmpty()) {
            applyAbiliy(player, enemies.values().stream(), player.loadout.get(ability));
        } else {
            var enemy = enemies.get(entityId.get());
            applyAbiliy(enemy, Stream.of(player), enemy.loadout.get(ability));
        }
    }

    /**
     * applies ability and computes side effects.
     */
    private void applyAbiliy(Entity caster, Stream<Entity> targets, Ability ab) {
        var cells = ab.effect().apply(caster.position);
        if (cells.isPresent()) {
            targets.filter(t -> cells.get().contains(t.position))
                .forEach(t -> {
                    // t.takeDmg(ab.targetHpDelta()); 
                    // caster.gainHp(ab.casterHpDelta());
                });
        }
    }

    /**
     * Represents an entity in the game with position, health, and abilities.
     */
    private static class Entity {
        private Vec2D position;
        private int hp;
        private final int maxHp;
        private final List<Ability> loadout;

        /**
         * Constructs an Entity with the specified position, health, and abilities.
         * 
         * @param position starting position
         * @param hp default hp 
         * @param loadout starting loadout
         */        
        Entity(final Vec2D position, final int hp, final List<Ability> loadout) {
            this.position = position;
            this.hp = hp;
            this.maxHp = hp;
            this.loadout = loadout;
        }

        /**
         * moves.
         * 
         * @param delta contatins deltaX and deltaY
         * @return if it moved
         */
        boolean move(Vec2D delta) { //TODO
            return true;
        }
    }
}