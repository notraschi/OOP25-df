package it.unibo.df.model.abilities;

import java.util.concurrent.TimeUnit;

import it.unibo.df.dto.AbilityView;

/**
 * Immutable description of a game ability.
 *
 * @param id            unique identifier of the ability
 * @param name          display name of the ability
 * @param cooldown      cooldown time in ticks
 * @param casterHpDelta caster hp variation
 * @param targetHpDelta target hp variation
 * @param effect        function implementing the ability logic
 */
public record Ability(
    int id,
    String name,
    int cooldown,
    int casterHpDelta,
    int targetHpDelta,
    AbilityFn effect
) {

    /**
     * Returns the ability type based on its effects.
     *
     * @return the ability type
     */
    public AbilityType type() {
        if (casterHpDelta > 0 && targetHpDelta != 0) {
            return AbilityType.LIFESTEAL;
        }
        if (casterHpDelta > 0 && targetHpDelta == 0) {
            return AbilityType.HEAL;
        }
        return AbilityType.ATTACK;
    }

    /**
     * Returns the ability view.
     */
    public AbilityView asView() {
        return new AbilityView(this.name,
            this.id, this.casterHpDelta,
            this.targetHpDelta,
            (int) TimeUnit.SECONDS.toMillis(this.cooldown)
        );
    }

}
