package it.unibo.df.model.abilities;

/**
 * Factory for abilities.
 */
public final class AbilityFactory {
    private AbilityFactory() { }
    /**
     * Creates a generic ability.
     *
     * @param id id
     * @param name name
     * @param cooldown cooldown
     * @param type type
     * @param casterHpDelta caster delta
     * @param targetHpDelta target delta
     * @param area area function
     * @return ability
     */
    public static Ability generic(
        final int id,
        final String name,
        final int cooldown,
        final AbilityType type,
        final int casterHpDelta,
        final int targetHpDelta,
        final AbilityFn area
    ) {
        return new Ability(
            id,
            name,
            cooldown,
            type,
            casterHpDelta,
            targetHpDelta,
            area
        );
    }
}

