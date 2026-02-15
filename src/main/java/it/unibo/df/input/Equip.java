package it.unibo.df.input;

/**
 * Command used to equip an ability in the loadout.
 *
 * @param id the identifier of the ability to equip
 */
public record Equip(int id) implements ArsenalInput {
}
