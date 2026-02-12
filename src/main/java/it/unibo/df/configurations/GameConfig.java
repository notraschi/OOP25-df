package it.unibo.df.configurations;

public record GameConfig(
    int numberOfEnemies
) {
    public static GameConfig defaultConfig() {
        return new GameConfig(2);
    }

    public static GameConfig testingConfig() {
        return new GameConfig(0);
    }
}
