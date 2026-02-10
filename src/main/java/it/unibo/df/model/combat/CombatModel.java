package it.unibo.df.model.combat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.df.dto.EntityView;
import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.special.SpecialAbilities;
import it.unibo.df.model.special.SpecialAbility;

/**
 * gamemodel.
 */
public class CombatModel {
    private final Entity player;
    private final Map<Integer, Entity> enemies;
    private final int boardSize;
    private int nextEnemyId = 0;
    private Optional<SpecialAbilities> disrupt;

    public CombatModel(List<Ability> playerLoadout) {
        player = new Entity(new Vec2D(0, 0), 100, playerLoadout, Optional.empty());
        enemies = new LinkedHashMap<>();
        // TODO: fix boardsize
        boardSize = 10;
        disrupt = Optional.empty();
    }

    /**
     * add an entity enemy.
     * 
     * @param entityId id of the enemy (if one)
     * @param hp hp of the enemy
     * @param enemyLoadout loadout of enemy
     * @return the id of the entity
     */
    public int addEnemy(EnemyDefinition enemy) {
        nextEnemyId++;
        enemies.put(
            nextEnemyId,
            new Entity(enemy.position(), enemy.hp(), enemy.loadout(), Optional.of(enemy.special()))
        );
        return nextEnemyId;
    }

    /**
     * moves an entity.
     * 
     * @param entityId id of the enemy (if one)
     * @param delta how much to move
     */
    public boolean move(Optional<Integer> entityId, Vec2D delta) {
        return entityId.map(enemies::get)
            .map(e -> e.move(delta, boardSize))
            .orElseGet(
                () -> player.move(
                    applyDisruption(delta).orElse(new Vec2D(0, 0)), boardSize
                )
            );
    }

    /**
     * casts a normal ability.
     * 
     * @param entityId id of the enemy (if one)
     * @param ability ability to cast
     * @return affected cells (none for healing abilities)
     */
    public Optional<Set<Vec2D>> cast(Optional<Integer> entityId, int ability) {
        if (entityId.isEmpty()) {
            return applyDisruption(ability)
                .filter(ab -> !player.cooldowns.get(ab).isActive())
                .flatMap(ab -> {
                    player.cooldowns.get(ab).begin();
                    return applyAbiliy(player, enemies.values().stream(), player.loadout.get(ab));
                });
        } else {
            var enemy = enemies.get(entityId.get());
            // cooldown check
            if (enemy.cooldowns.get(ability).isActive()) {
                return Optional.empty();
            }
            enemy.cooldowns.get(ability).begin();
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
        } else {
            caster.gainHp(ab.casterHpDelta());
        }
        return cells;
    }

    /**
     * casts a special ability, sets it as the active one (removes previews).
     * 
     * @param entityId id of the enemy
     */
    public void castSpecial(int entityId) {
        var enemy = enemies.get(entityId);
        enemy.special.ifPresentOrElse(s -> {
                disrupt = Optional.of(s);
                disrupt.get().ability.timer().begin();
            },
            () -> {
                throw new IllegalStateException("someone made an enemy without a special");
            }
        );
    }

    /**
     * applies special ability (disrupt).
     * 
     * @param input the input to disrupt
     * @return an optional containing the original input if no disruption was applied,
     * otherwise an optional containing the new input, or an empty optional, according to
     * disruption policy
     */
    private <T> Optional<T> applyDisruption(T input) {
        // guard
        if (disrupt.isEmpty() || !disrupt.get().ability.canHandle(input)) {
            return Optional.of(input);
        }
        // safe cast because of the guard earlier (i hate this)
        var casted = (SpecialAbility<T>) disrupt.get().ability;
        return casted.trasform(input);
    }

    /**
     * create a player data view.
     * 
     * @return player's view
     */
    public EntityView playerView() {
        return player.asView();
    }

    /**
     * create a enemies data views.
     * 
     * @return enemies views
     */
    public Map<Integer, EntityView> enemyView() {
        return enemies.entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().asView()));
    }

    /**
     * returns currently active special ability (disruptor).
     * 
     * @return currently active special ability (disruptor).
     */
    public SpecialAbilityView getDisrupt() {
        return SpecialAbilities.asView(disrupt);
    }

    /**
     * makes the time pass in the model.
     * updates cooldowns.
     * 
     * @param deltaTime the time that has passed since last tick, milliseconds
     */
    public void tick(long deltaTime) {
        player.cooldowns.forEach(c -> c.update(deltaTime));
        enemies.values().forEach(e -> e.cooldowns.forEach(c -> c.update(deltaTime)));
        disrupt.ifPresent(ab -> ab.ability.timer().update(deltaTime));
        // remove disrupt if timer is done
        if (disrupt.isPresent() && !disrupt.get().ability.timer().isActive()) {
            disrupt = Optional.empty();
        }
    }
    public long getKilledEnemies() {
        return enemies.values().stream().filter(e -> e.hp == 0).count();
    }
    /**
     * Represents an entity in the game with position, health, and abilities.
     */
    private static class Entity {
        private Vec2D position;
        private int hp;
        private final int maxHp;
        private final List<Ability> loadout;
        private final List<Cooldown> cooldowns;
        private final Optional<SpecialAbilities> special;

        /**
         * Constructs an Entity with the specified position, health, and abilities.
         * 
         * @param position starting position
         * @param hp default hp 
         * @param loadout starting loadout
         */        
        Entity(
            final Vec2D position,
            final int hp,
            final List<Ability> loadout,
            final Optional<SpecialAbilities> special
        ) {
            this.position = position;
            this.hp = hp;
            this.maxHp = hp;
            this.loadout = loadout;
            this.cooldowns = loadout.stream()
                .map(a -> new Cooldown(a.cooldown() * 1000))
                .toList();
            this.special = special;
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

        EntityView asView() {
            return new EntityView(maxHp, hp, position, cooldowns.stream().map(c -> (int) c.getRemaining()).toList(),0); //SISTEMARE COOLDOWN MOVE
        }
    }

   
}