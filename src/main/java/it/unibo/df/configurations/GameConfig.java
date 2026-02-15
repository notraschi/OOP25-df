package it.unibo.df.configurations;

/**
 * game configuration parameters.
 *
 * @param numberOfEnemies number of enemies for a combat
 */
public record GameConfig(
    int numberOfEnemies
) {
    /**
     * @return default configuration
     */
    public static GameConfig defaultConfig() {
        return new GameConfig(2);
    }

    /**
     * @return testing configuration
     */
    public static GameConfig testingConfig() {
        return new GameConfig(0);
    }
}
