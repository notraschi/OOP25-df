package it.unibo.df.dto;

/**
 * represents an Ability, it is used to forward abiliy info to the view (in ArsenalState)
 */
public record AbilityView ( // TODO: for now it has its own file...
    String name,
    int id,
    int casterHpDelta,
    int targetHpDelta,
    int cooldown
) {}
