package it.unibo.df.model.combat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;

/**
 * gamemodel.
 */
public class CombatModel {
    private final Entity player;
    private final Map<Integer, Entity> enemies;
    private int boardSize;
    private int nextEnemyId = 0;

    public CombatModel(List<Ability> playerLoadout) {
        player = new Entity(new Vec2D(0, 0), 100, playerLoadout);
        // TODO: add enemies' loadout
        enemies = new LinkedHashMap<>();
        // TODO: fix boardsize
        boardSize = 10;
    }

    /**
     * add an entity enemie.
     * 
     * @param entityId id of the enemy (if one)
     * @param hp hp of the enemy
     * @param enemyLoadout loadout of enemy
     * @return the id of the entity
     */
    public int addEnemy(EnemyDefinition enemy) {
        nextEnemyId++;
        enemies.put(nextEnemyId, new Entity(enemy.position(), enemy.hp(), enemy.loadout()));
        return nextEnemyId;
    }

    /**
     * moves an entity.
     * 
     * @param entityId id of the enemy (if one)
     * @param delta how much to move
     */
    public boolean move(Optional<Integer> entityId, Vec2D delta) {
        Entity target = entityId.map(enemies::get).orElse(player);
        return target.move(delta, boardSize);
    }

    /**
     * casts an ability.
     * 
     * @param entityId id of the enemy (if one)
     * @param ability ability to cast
     * @return affected cells (none for healing abilities)
     */
    public Optional<Set<Vec2D>> cast(Optional<Integer> entityId, int ability) {
        if (entityId.isEmpty()) {
            return applyAbiliy(player, enemies.values().stream(), player.loadout.get(ability));
        } else {
            var enemy = enemies.get(entityId.get());
            return applyAbiliy(enemy, Stream.of(player), enemy.loadout.get(ability));
        }
    }

    /**
     * applies ability and computes side effects.
     * 
     * @param caster the caster
     * @param targets the targets
     * @param ab the ability cast
     * @return affected cells
     */
    private Optional<Set<Vec2D>> applyAbiliy(Entity caster, Stream<Entity> targets, Ability ab) {
        var cells = ab.effect().apply(caster.position);
        if (cells.isPresent()) {
        targets
            .filter(t -> cells.get().contains(t.position))
            .forEach(t -> {
                t.takeDmg(ab.targetHpDelta());
                caster.gainHp(ab.casterHpDelta());
            });

        } else if (ab.type() == AbilityType.HEAL) {
            caster.gainHp(ab.casterHpDelta());
        }
        return cells;
    }

    // /**
    //  * getter for player pos.
    //  * 
    //  * @return player's position
    //  */
    // // public Vec2D playerPos() {
    // //     return player.position;
    // // }

    // /**
    //  * getter for enemies pos.
    //  * 
    //  * @return enemies' position
    //  */
    // // public Set<Vec2D> enemyPos() {
    // //     return enemies.values().stream().map(e -> e.position).collect(Collectors.toSet());
    // // }

    /**
     * create a player data view.
     * 
     * @return player's view
     */
    public EntityView playerView() {
        return new EntityView(
            player.maxHp,
            player.hp,
            player.position
        );
    }

    /**
     * create a enemies data views.
     * 
     * @return enemies views
     */
    public Map<Integer, EntityView> enemyView() {
        return enemies.entrySet().stream().collect(
            Collectors.toMap(
                e -> e.getKey(),
                e -> {
                    var v = e.getValue();
                    return new EntityView(
                        v.maxHp,
                        v.hp,
                        v.position
                    );
                }
            )
        );
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
         * Applies damage to the entity.
         *
         * @param dmg damage amount (positive number)
         */
        void takeDmg(final int dmg) {
            this.hp = Math.max(0, this.hp - dmg);
        }

        /**
         * Heals the entity.
         *
         * @param heal healing amount (positive number)
         */
        void gainHp(final int heal) {
            this.hp = Math.min(this.maxHp, this.hp + heal);
        }

        /**
         * moves.
         * 
         * @param delta contatins deltaX and deltaY
         * @return if it moved
         */
        boolean move(Vec2D delta, int bound) {
            final int newX = this.position.x() + delta.x();
            final int newY = this.position.y() + delta.y();
            
            if (newX < 0 || newX >= bound || newY < 0 || newY >= bound) {
                return false;
            }
            
            this.position = new Vec2D(newX, newY);
            return true;
        }
    }
}