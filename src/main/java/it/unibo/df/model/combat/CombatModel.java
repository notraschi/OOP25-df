package it.unibo.df.model.combat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.df.configurations.Constants;
import it.unibo.df.dto.EntityView;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.special.SpecialAbility;
import it.unibo.df.utility.Cooldown;
import it.unibo.df.utility.Vec2D;

/**
 * gamemodel.
 */
public class CombatModel {

    private static final int PLAYER_HP = 500;

    private final Entity player;
    private final Map<Integer, EnemyEntity> enemies;
    private int nextEnemyId;
    private Optional<SpecialAbility<?>> disrupt;

    /**
     * Constructor.
     * 
     * @param playerLoadout chosen in asernal
     */
    public CombatModel(final List<Ability> playerLoadout) {
        player = new Entity(new Vec2D(0, 0), PLAYER_HP, playerLoadout);
        enemies = new LinkedHashMap<>();
        disrupt = Optional.empty();
    }

    /**
     * add an entity enemy.
     * 
     * @param enemy record whit all information to make a new enemy
     * @return the id of the entity
     */
    public int addEnemy(final EnemyDefinition enemy) {
        nextEnemyId++;
        enemies.put(
            nextEnemyId,
            new EnemyEntity(enemy.position(), enemy.hp(), enemy.loadout(), enemy.special())
        );
        return nextEnemyId;
    }

    /**
     * moves an entity.
     * 
     * @param entityId id of the enemy (if one)
     * @param delta how much to move
     * @return true if he moved
     */
    public boolean move(final Optional<Integer> entityId, final Vec2D delta) {
        final var mover = entityId.map(enemies::get).map(ee -> (Entity) ee).orElse(player);
        final var targetPos = entityId.isPresent()
            ? mover.calculateMove(delta)
            : mover.calculateMove(
                applyDisruption(delta).orElse(new Vec2D(0, 0))
            );
        final var tmp = mover.validMove(targetPos, Constants.BOARD_SIZE) && canMove(targetPos);
        if (tmp) {
            mover.move(targetPos);
        }
        return tmp;
    }

    /**
     * check nobody in this position.
     * 
     * @param targetPos to check that no one is on it
     * @return if the targetPos is allowed
     */
    private boolean canMove(final Vec2D targetPos) {
        return !enemies.values().stream()
            .anyMatch(e -> e.getPosition().equals(targetPos) && e.getHp() > 0) 
            && !player.position.equals(targetPos);
    }

    /**
     * casts a normal ability.
     * 
     * @param entityId id of the enemy (if one)
     * @param ability ability to cast
     * @return affected cells (none for healing abilities)
     */
    public Optional<Set<Vec2D>> cast(final Optional<Integer> entityId, final int ability) {
        if (entityId.isEmpty()) {
            return applyDisruption(ability)
                .filter(ab -> !player.cooldowns.get(ab).isActive())
                .flatMap(ab -> {
                    player.cooldowns.get(ab).begin();
                    return applyAbiliy(player, enemies.values().stream().map(ee -> (Entity) ee), player.loadout.get(ab));
                });
        } else {
            final var enemy = enemies.get(entityId.get());
            // cooldown check
            if (enemy.getCooldowns().get(ability).isActive()) {
                return Optional.empty();
            }
            enemy.getCooldowns().get(ability).begin();
            return applyAbiliy(enemy, Stream.of(player), enemy.getLoadout().get(ability));
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
    private Optional<Set<Vec2D>> applyAbiliy(final Entity caster, final Stream<Entity> targets, final Ability ab) {
        final var cells = ab.effect().apply(caster.position);
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
    public void castSpecial(final int entityId) {
        final var enemy = enemies.get(entityId);
        if (enemy == null) {
            throw new IllegalArgumentException("not a valid enemy");
        }
        disrupt = Optional.of(enemy.special);
        disrupt.get().timer().begin();
    }

    /**
     * applies special ability (disrupt).
     * 
     * @param input the input to disrupt
     * @param <T> input type
     * @return an optional containing the original input if no disruption was applied,
     *         otherwise an optional containing the new input, or an empty optional, 
     *         according todisruption policy
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<T> applyDisruption(final T input) {
        // guard
        if (disrupt.isEmpty() || !disrupt.get().canHandle(input)) {
            return Optional.of(input);
        }
        // safe cast because of the guard earlier
        final var casted = (SpecialAbility<T>) disrupt.get();
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
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().asView()));
    }

    /**
     * returns currently active special ability (disruptor).
     * 
     * @return currently active special ability (disruptor).
     */
    public boolean isDisruptActive() {
        return disrupt.isPresent();
    }

    /**
     * makes the time pass in the model.
     * updates cooldowns.
     * 
     * @param deltaTime the time that has passed since last tick, milliseconds
     */
    public void tick(final long deltaTime) {
        player.cooldowns.forEach(c -> c.update(deltaTime));
        player.movementCooldown.update(deltaTime);
        enemies.values().forEach(e -> {
            e.getCooldowns().forEach(c -> c.update(deltaTime));
            e.getMovementCooldown().update(deltaTime);
        });
        disrupt.ifPresent(ab -> ab.timer().update(deltaTime));
        // remove disrupt if timer is done
        if (disrupt.isPresent() && !disrupt.get().timer().isActive()) {
            disrupt = Optional.empty();
        }
    }

    /**
     * checks if enemy is still alive.
     * 
     * @param id the id of the enemy
     * @return a boolean, true if hp > 0
     */
    public boolean isEnemyAlive(final int id) {
        if (!enemies.containsKey(id)) {
            throw new IllegalArgumentException("unknown id, this enemy does not exist");
        }
        return enemies.get(id).getHp() > 0;
    }

    /**
     * gets number of dead enemies.
     * 
     * @return amount of killed enemies
     */
    public int getKilledEnemies() {
        return (int) enemies.values().stream().filter(e -> e.getHp() == 0).count();
    }

    /**
     * Represents an entity in the game with position, health, and abilities.
     */
    private static class Entity {
        private static final int MOVEMENT_COOLDOWN_TIME = 175;

        private final int maxHp;
        private final List<Ability> loadout;
        private final List<Cooldown> cooldowns;
        private final Cooldown movementCooldown;

        private Vec2D position;
        private int hp;

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
            final List<Ability> loadout
        ) {
            this.position = position;
            this.hp = hp;
            this.maxHp = hp;
            this.loadout = loadout;
            this.cooldowns = loadout.stream()
                .map(a -> new Cooldown(a.cooldown() * 1000))
                .toList();
            this.movementCooldown = new Cooldown(MOVEMENT_COOLDOWN_TIME);
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
         * calculate the new position.
         * 
         * @param delta contatins deltaX and deltaY
         * @return the new position
         */
        Vec2D calculateMove(final Vec2D delta) {
            final int newX = this.position.x() + delta.x();
            final int newY = this.position.y() + delta.y();
            return new Vec2D(newX, newY);
        }

        /**
         * validate the movement.
         * 
         * @param pos position to move to
         * @param bound board size
         * @return true if the position is valid and the cooldown is inactive
         */
        boolean validMove(final Vec2D pos, final int bound) {
            return !(movementCooldown.isActive()
                || pos.x() < 0 || pos.x() >= bound || pos.y() < 0 || pos.y() >= bound);
        }

        /**
         * moves entity.
         * 
         * @param pos the new position
         */
        void move(final Vec2D pos) {
            this.position = pos;
            movementCooldown.begin();
        }

        /**
         * gets hp.
         * 
         * @return entity's current hp
         */
        int getHp() {
            return hp;
        }

        /**
         * gets loadout.
         * 
         * @return entity's loadout
         */
        List<Ability> getLoadout() {
            return loadout;
        }

        /**
         * gets position.
         * 
         * @return entity's current position
         */
        Vec2D getPosition() {
            return position;
        }

        /**
         * gets cooldowns.
         * 
         * @return entity's cooldowns
         */
        List<Cooldown> getCooldowns() {
            return cooldowns;
        }

        /**
         * gets movement cooldown.
         * 
         * @return entity's movement cooldown
         */
        Cooldown getMovementCooldown() {
            return movementCooldown;
        }

        /**
         * converts an Entity to a dto object.
         * 
         * @return the dto in question
         */
        EntityView asView() {
            return new EntityView(
                maxHp,
                hp,
                position,
                cooldowns.stream().map(Cooldown::getRemaining).toList(),
                (int) movementCooldown.getRemaining()
            );
        }
    }

    /**
     * Represents an enemy in the game with position, health, and abilities.
     */
    private static final class EnemyEntity extends Entity {
        private final SpecialAbility<?> special;

        /**
         * Constructs an EnemyEntity with the specified position, health, and abilities.
         * 
         * @param position starting position
         * @param hp default hp 
         * @param loadout starting loadout
         * @param special ability only for enemies
         */
        EnemyEntity(
            final Vec2D position,
            final int hp,
            final List<Ability> loadout,
            final SpecialAbility<?> special
        ) {
            super(position, hp, loadout);
            this.special = special;
        }
    }
}
