package it.unibo.df.input;

/**
 * Interface for inputs valid during combat.
 */
public sealed interface CombatInput extends Input permits Move, Attack {
}
