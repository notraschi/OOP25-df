package it.unibo.df.input;

/**
 * Interface for inputs valid in the arsenal phase.
 */
public sealed interface ArsenalInput extends Input permits Equip, Combine, Unequip {
}
