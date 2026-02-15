package it.unibo.df.input;

/**
 * Command used to combine two abilities.
 *
 * @param id1 the identifier of the first ability
 * @param id2 the identifier of the second ability
 */
public record Combine(int id1, int id2) implements ArsenalInput {
}
