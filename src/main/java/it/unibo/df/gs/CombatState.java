package it.unibo.df.gs;

/**
 * represents the game's state while in combat.
 */
public final class CombatState implements GameState {
    private String testString = "yo we fighting!!";

    public String test() {
        return testString;
    }

    public void setTest(String s) {
        testString = s;
    }
}
