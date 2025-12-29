package it.unibo.df.gs;

/**
 * represents the game's state while in the arsenal.
 */
public final class ArsenalState implements GameState {
    private String testString = "arsenal baby";

    public String test() {
        return testString;
    }

    public void setTest(String s) {
        testString = s;
    }
}
