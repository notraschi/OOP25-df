package it.unibo.df.model.abilities;

import it.unibo.df.dto.AbilityView;

/**
 * Immutable description of a game ability.
 *
 * @param id            unique identifier of the ability
 * @param name          display name of the ability
 * @param cooldown      cooldown time in ticks
 * @param type          category of the ability
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
        AbilityFn effect) {

    public AbilityType type() {
        if (casterHpDelta > 0 && targetHpDelta != 0) {
            return AbilityType.LIFESTEAL;
        }
        if (casterHpDelta > 0 && targetHpDelta == 0) {
            return AbilityType.HEAL;
        }
        return AbilityType.ATTACK;
    }

    public AbilityView asView() {
        return new AbilityView(this.name, this.id, this.casterHpDelta, this.targetHpDelta, this.cooldown);
    }

}
