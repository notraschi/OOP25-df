package it.unibo.df;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.df.controller.Controller;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityType;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.CombatModel;
import it.unibo.df.model.combat.EnemyDefinition;

final class ControllerUnlockAbilitiesTest {
    private Controller controller;
    private CombatModel model; 

    
    void setup() {
        var defaultLoadout = List.of(
            new Ability(1, "", true, 1, AbilityType.HEAL, 5, 0, pos -> Optional.empty()),
            new Ability(2, "", true, 1, AbilityType.HEAL, 5, 0, pos -> Optional.empty()),
            new Ability(3, "", true,1, AbilityType.HEAL, 5, 0, pos -> Optional.empty())
        );
        model = new CombatModel(defaultLoadout);
        controller = new Controller();
        assertTrue(model.enemyView().isEmpty());
        model.addEnemy(new EnemyDefinition(new Vec2D(0,0),100,defaultLoadout,List.of(),null));
    }

    @Test
    void completeTest() throws NoSuchFieldException, IllegalAccessException{
        var enemyField = model.getClass().getDeclaredField("enemies");
        enemyField.setAccessible(true);
        var enemiesList = enemyField.get(model);
        if (enemiesList instanceof Map<?, ?> enemies){
            var enemy = enemies.values().iterator().next();
            var hpField = enemy.getClass().getDeclaredField("hp");
            hpField.setAccessible(true);
            hpField.setInt(enemy, 50);
        }
        var enemyId = model.enemyView().keySet().iterator().next();
        assertEquals(50,model.enemyView().get(enemyId).hp());
        controller.toArsenal();
        ArsenalState as = (ArsenalState)controller.tick(0);
        assertEquals(3, as.unlocked());
    }
}
