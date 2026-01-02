package it.unibo.df.model.abilities;

/**
 * Immutable description of a game ability.
 *
 * @param id unique identifier of the ability
 * @param name display name of the ability
 * @param cooldown cooldown time in ticks
 * @param type category of the ability
 * @param hpDelta base hp variation value
 * @param stealAmount amount of hp stolen by lifesteal abilities
 * @param effect function implementing the ability logic
 */
public record Ability(
    int id,
    String name,
    int cooldown,
    AbilityType type,
    int hpDelta,
    int stealAmount,
    AbilityFn effect
) { }



 