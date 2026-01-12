package it.unibo.df.model.abilities.combine;

/**
 * Represents a pair of ability identifiers used as a key in combination maps.
 * The pair is sorted so that (a, b) equals (b, a).
 *
 * @param firstId first ability id (sorted)
 * @param secondId second ability id (sorted)
 */


public record AbilityPair(int firstId, int secondId) {

    /**
     * Creates a sorted pair (order-independent).
     *
     * @param a first id
     * @param b second id
     * @return sorted pair
     */
    public static AbilityPair of(final int a, final int b) {
        return a <= b ? new AbilityPair(a, b) : new AbilityPair(b, a);
    }  
}

