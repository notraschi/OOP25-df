package it.unibo.df;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.CombatModel;
import it.unibo.df.model.combat.EnemyDefinition;
import it.unibo.df.model.special.SpecialAbilities;

/**
 * Test class for AbilityRegistry.
 */
final class CombatModelTest {
    private CombatModel model;

    void setup(SpecialAbilities sa) {
        var defaultLoadout = List.of(
            new Ability(1, "", 1, AbilityType.HEAL, 5, 0, pos -> Optional.empty()),
            new Ability(2, "", 1, AbilityType.HEAL, 5, 0, pos -> Optional.empty()),
            new Ability(3, "", 1, AbilityType.HEAL, 5, 0, pos -> Optional.empty())
        );
        // filling the loadout with garbage
        model = new CombatModel(defaultLoadout);
        // basic test
        assertTrue(model.enemyView().isEmpty());
        assertEquals(new Vec2D(0, 0), model.playerView().position());
        // adding an enemy that can deny the movement
        model.addEnemy(
            new EnemyDefinition(
                new Vec2D(0, 0), 100, defaultLoadout, List.of(), sa
            )
        );
        assertTrue(model.enemyView().size() == 1);
    }

    @Test
    void specialAbiltyDenyMovementTest() {
        setup(SpecialAbilities.DENY_MOVEMENT);

        // moving player
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(1, 0), model.playerView().position());

        // artificially casting the special ability
        model.castSpecial(1);
        assertEquals(SpecialAbilityView.DENY_MOVEMENT, model.getDisrupt());
        // now movement should be denied
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(1, 0), model.playerView().position()); // old location

        // let have some time pass
        model.tick(2000);
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(1, 1), model.playerView().position()); // now he can move
    }

    @Test
    void specialAbiltyInvertMovementTest() {
        setup(SpecialAbilities.INVERT_MOVEMENT);

        // moving player
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(1, 0), model.playerView().position());

        // artificially casting the special ability
        model.castSpecial(1);
        assertEquals(SpecialAbilityView.INVERT_MOVEMENT, model.getDisrupt());

        // now movement should be inverted
        model.tick(100); // expire movement cooldown
        model.move(Optional.empty(), new Vec2D(1, 0));
        assertEquals(new Vec2D(0, 0), model.playerView().position()); // old location

        // let have some time pass
        model.tick(6000);
        model.move(Optional.empty(), new Vec2D(0, 1));
        assertEquals(new Vec2D(0, 1), model.playerView().position()); // now he can move
    }

    @Test
    void specialAbiltyDenyAttackTest() throws NoSuchFieldException, IllegalAccessException {
        setup(SpecialAbilities.DENY_ATTACK);
        var playerField = model.getClass().getDeclaredField("player");
        playerField.setAccessible(true);
        var player = playerField.get(model);

        var hpField = player.getClass().getDeclaredField("hp");
        hpField.setAccessible(true);
        hpField.setInt(player, 50);
        // dont mind the reflection to have the player start with 50 hp

        // player casts a heal
        model.cast(Optional.empty(), 1);
        assertEquals(55, model.playerView().hp());

        // artificially casting the special ability
        model.castSpecial(1);
        assertEquals(SpecialAbilityView.DENY_ATTACK, model.getDisrupt());

        // now attack shouldnt work
        model.cast(Optional.empty(), 1);
        assertEquals(55, model.playerView().hp());

        // let have some time pass
        model.tick(5000);
        model.cast(Optional.empty(), 1);
        assertEquals(60, model.playerView().hp());
    }
}
