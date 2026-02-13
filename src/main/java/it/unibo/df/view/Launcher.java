package it.unibo.df.view;

/**
 * Starter method of the app.
 */
public final class Launcher {

    private Launcher() {
        throw new UnsupportedOperationException("Cannot inizialize this class");
    }

    /**
     * @param args arguments from launching
     */
    public static void main(final String[] args) {
        MainStage.entry(args);
    }
}
