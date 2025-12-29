package it.unibo.df.gs;

/**
 * represents the game's state.
 */
public sealed interface GameState permits CombatState, ArsenalState {}
