package it.unibo.df.configurations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.df.controller.Progress;

/**
 * game configuration parameters.
 *
 * @param numberOfEnemies number of enemies for a combat
 * @param progress the games progress
 */
@SuppressFBWarnings(
    value = "EI",
    justification = "shares mutable Progress for testing purposes"
)
public record GameConfig(
    int numberOfEnemies,
    Progress progress
) {
    /**
     * @return default configuration
     */
    public static GameConfig defaultConfig() {
        return new GameConfig(2, new Progress());
    }

    /**
     * @return testing configuration
     */
    public static GameConfig testingConfig() {
        final var progress = new Progress();
        progress.reset();
        return new GameConfig(0, progress);
    }

    /**
     * @return testing configuration but enable unlocking abilities
     */
    public static GameConfig testingWithEnemiesConfig() {
        final var progress = new Progress();
        progress.reset();
        return new GameConfig(2, progress);
    }
}
