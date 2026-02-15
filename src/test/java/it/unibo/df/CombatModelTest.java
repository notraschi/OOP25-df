package it.unibo.df;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.combat.CombatModel;
import it.unibo.df.model.combat.EnemyDefinition;
import it.unibo.df.model.special.SpecialAbility;
import it.unibo.df.model.special.SpecialAbilityFactory;
import it.unibo.df.utility.Vec2D;

/**
 * Test class for AbilityRegistry.
 */
final class CombatModelTest {
    private static final int HEAL_DELTA = 5;
    private static final int EDGE_POS = 9;
    private static final int DENY_MOVEMENT_MS = 2000;
    private static final int MOVE_COOLDOWN_MS = 175;
    private static final int INVERT_DURATION_MS = 6000;
    private static final int DENY_ATTACK_MS = 5000;
    private static final int START_HP = 50;
    private static final int HP_AFTER_FIRST_HEAL = 55;
    private static final int HP_AFTER_SECOND_HEAL = 60;

    private CombatModel model;

    @BeforeEach
    void setUp() {
        final var defaultLoadout = List.of(
            new Ability(1, "", 1, HEAL_DELTA, 0, pos -> Optional.empty()),
            new Ability(2, "", 1, HEAL_DELTA, 0, pos -> Optional.empty()),
            new Ability(3, "", 1, HEAL_DELTA, 0, pos -> Optional.empty())
        );
        model = new CombatModel(defaultLoadout);
    }

    void setupWithSpecial(final SpecialAbility<?> sa) {
        model.addEnemy(
            new EnemyDefinition(
                new Vec2D(EDGE_POS, EDGE_POS), 100, List.of(), List.of(), sa
            )
        );
    }

    @Test
    void specialAbiltyDenyMovementTest() {
        setupWithSpecial(SpecialAbilityFactory.denyMovement());

        // moving player
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(1, 0), model.playerView().position());

        // artificially casting the special ability
        model.castSpecial(1);
        assertTrue(model.isDisruptActive());
        // now movement should be denied
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(1, 0), model.playerView().position()); // old location

        // let have some time pass
        model.tick(DENY_MOVEMENT_MS);
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(1, 1), model.playerView().position()); // now he can move
    }

    @Test
    void specialAbiltyInvertMovementTest() {
        setupWithSpecial(SpecialAbilityFactory.invertMovement());

        // moving player
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(1, 0), model.playerView().position());

        // artificially casting the special ability
        model.castSpecial(1);
        assertTrue(model.isDisruptActive());

        // now movement should be inverted
        model.tick(MOVE_COOLDOWN_MS); // expire movement cooldown
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(0, 0), model.playerView().position()); // old location

        // let have some time pass
        model.tick(INVERT_DURATION_MS);
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(0, 1), model.playerView().position()); // now he can move
    }

    @Test
    void specialAbiltyDenyAttackTest() throws NoSuchFieldException, IllegalAccessException {
        setupWithSpecial(SpecialAbilityFactory.denyAttack());
        final var playerField = model.getClass().getDeclaredField("player");
        playerField.setAccessible(true); // NOPMD: entity is a private nested class
        final var player = playerField.get(model);

        final var hpField = player.getClass().getDeclaredField("hp");
        hpField.setAccessible(true); // NOPMD: entity is a private nested class
        hpField.setInt(player, START_HP);
        // dont mind the reflection to have the player start with 50 hp

        // player casts a heal
        model.cast(Optional.empty(), 1);
        assertEquals(HP_AFTER_FIRST_HEAL, model.playerView().hp());

        // artificially casting the special ability
        model.castSpecial(1);
        assertTrue(model.isDisruptActive());

        // now attack shouldnt work
        model.cast(Optional.empty(), 1);
        assertEquals(HP_AFTER_FIRST_HEAL, model.playerView().hp());

        // let have some time pass
        model.tick(DENY_ATTACK_MS);
        model.cast(Optional.empty(), 1);
        assertEquals(HP_AFTER_SECOND_HEAL, model.playerView().hp());
    }
}
