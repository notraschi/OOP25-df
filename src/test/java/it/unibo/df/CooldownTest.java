package it.unibo.df;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import it.unibo.df.model.combat.Cooldown;

public class CooldownTest {
    @Test
    void remainingTimeTest() throws InterruptedException {
        Cooldown cooldown = new Cooldown();
        cooldown.setCooldown(5, TimeUnit.SECONDS);
        Thread.sleep(TimeUnit.MILLISECONDS.convert(4, TimeUnit.SECONDS));
        assertEquals(1,cooldown.getRemainingTime(TimeUnit.SECONDS));
    }

    @Test
    void isExpiredTest() throws InterruptedException {
        Cooldown cooldown = new Cooldown();
        cooldown.setCooldown(5, TimeUnit.SECONDS);
        assertFalse(cooldown.isExpire());
        Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
        assertFalse(cooldown.isExpire());

        Thread.sleep(TimeUnit.MILLISECONDS.convert(4, TimeUnit.SECONDS));
        assertTrue(cooldown.isExpire());

        cooldown.setCooldown(5, TimeUnit.SECONDS);
        assertFalse(cooldown.isExpire());
        Thread.sleep(TimeUnit.MILLISECONDS.convert(5001, TimeUnit.MILLISECONDS));
        assertTrue(cooldown.isExpire());
    }

    @Test
    void canUse() throws InterruptedException {
        List<Integer> ability = new ArrayList<>(List.of(2,3,4));
        List<Cooldown> cooldownAbility = new ArrayList<>(Collections.nCopies(3, new Cooldown()));
        Set<Integer> jail = new HashSet<>();

        while(true) {
            if (cooldownAbility.get(1).isExpire()) {
                System.out.println(ability.get(1));
                cooldownAbility.get(1).setCooldown(ability.get(1), TimeUnit.SECONDS);
            }

            if(!jail.contains(ability.get(1))) {
                System.out.println("boom");
                Runnable t = () -> {
                    jail.add(ability.get(1));
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println("no");
                    }
                    jail.remove(ability.get(1));
                };
                new Thread(t).start(); 
            }
        }
    }
}