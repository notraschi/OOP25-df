package it.unibo.df.dto;

/**
 * Represents an Ability, it is used to forward abiliy info to the view (in ArsenalState).
 *
 * @param name display name of the ability
 * @param id unique identifier of the ability
 * @param casterHpDelta caster hp variation
 * @param targetHpDelta target hp variation
 * @param cooldown cooldown time in ticks
 */
public record AbilityView(
    String name,
    int id,
    int casterHpDelta,
    int targetHpDelta,
    int cooldown
) {

}
