package it.unibo.df.input;

/**
 * Command used to remove an ability from the loadout.
 *
 * @param id the identifier of the ability to remove
 */
public record Unequip(int id) implements ArsenalInput {
}
