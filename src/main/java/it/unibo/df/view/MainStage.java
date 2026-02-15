package it.unibo.df.view;

import java.util.List;
import java.util.Optional;

import it.unibo.df.configurations.GameConfig;
import it.unibo.df.controller.Controller;
import it.unibo.df.dto.CombatStatus;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Move;
import it.unibo.df.input.Unequip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 */
public class MainStage extends Application {
    private static final int TICK = 7;
    private static final int LOADOUT_SIZE = 3;
    private final double minScreenSize = (Double.min(
        Screen.getPrimary().getBounds().getHeight(),
        Screen.getPrimary().getBounds().getWidth()
    )) / 2;

    private final GameBoard board = new GameBoard(
        List.of(
            "← \nleft",
            "→ \nright",
            "↑ \nup",
            "↓ \ndown",
            "Z \nability 1",
            "X \nability 2",
            "C \nability 3",
            "Q \nquit",
            "SPACE \npause"
        ),
        LOADOUT_SIZE
    );
    private final AbilityMenu menu = new AbilityMenu(
        List.of(
            "ENTER \nto combine abilities",
            "Z \nto add to loadout",
            "D \nto see description",
            "X \nto unequip",
            "I \nBack to play",
            "1 \nselect in mixer",
            "2 \nselect in mixer",
            "Q \nquit"
        ),
        LOADOUT_SIZE
    );
    private final Controller controller = new Controller(GameConfig.defaultConfig());
    private Timeline timeline;
    private Stage stage;

    /**
     * Start method to display the stage to the user.
     * 
     * @param s the stage to show
     */
    @Override
    public void start(final Stage s) {
        stage = s;
        addKeysListenersToBoard(board.getScene());
        addKeysListenersToMenu(menu.getScene());
        addKeysListenersToStage();

        menu.refresh((ArsenalState) controller.tick(TICK));
        stage.setScene(menu.getScene());

        timeline = new Timeline(
            new KeyFrame(Duration.millis(TICK), e -> tick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        stage.setMinHeight(minScreenSize);
        stage.setMinWidth(minScreenSize);
        stage.show();
        stage.setOnCloseRequest(e -> {
            e.consume();
            quitAlert();
        });
    }

    private void matchEnd(final String matchResult) {
        final Alert alert = new Alert(Alert.AlertType.NONE);
        final ButtonType ok = new ButtonType("OK");
        alert.setTitle("END of BATTLE");
        alert.setContentText("YOU" + matchResult + "THE GAME");
        alert.getButtonTypes().setAll(ok);
        alert.showAndWait();
    }

    private void tick() {
        final var cs = (CombatState) controller.tick(TICK);
        board.refresh(cs, TICK);
        switch (cs.matchStatus()) {
            case CombatStatus.WON -> {
                timeline.pause();
                Platform.runLater(() -> {
                    matchEnd(" WON ");
                    visualChange();
                });
            }
            case CombatStatus.LOST -> {
                timeline.pause();
                Platform.runLater(() -> {
                    matchEnd(" LOST ");
                    visualChange();
                });
            }
            default -> { }
        }
    }

    private void addKeysListenersToBoard(final Scene boardScene) {
        boardScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case KeyCode.DOWN -> controller.handle(Move.DOWN);
                case KeyCode.UP -> controller.handle(Move.UP);
                case KeyCode.RIGHT -> controller.handle(Move.RIGHT);
                case KeyCode.LEFT -> controller.handle(Move.LEFT);
                case KeyCode.SPACE -> pauseAlert();
                case KeyCode.Z -> controller.handle(Attack.ABILITY1);
                case KeyCode.X -> controller.handle(Attack.ABILITY2);
                case KeyCode.C -> controller.handle(Attack.ABILITY3);
                default -> { }
            }
        });
    }

    private void addKeysListenersToStage() {
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case KeyCode.Q -> quitAlert();
                default -> { }
            }
        });
    }

    private void addKeysListenersToMenu(final Scene menuScene) {
        menuScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (menu.getGroup().getSelectedToggle() != null) {
                final ToggleButton btn = (ToggleButton) menu.getGroup().getSelectedToggle();
                switch (event.getCode()) {
                    case KeyCode.Z -> {
                        controller.handle(new Equip(menu.getId(btn.getText())));
                        menu.refresh((ArsenalState) controller.tick(0));
                    }
                    case KeyCode.X -> {
                        controller.handle(new Unequip(menu.getId(btn.getText())));
                        menu.unequip(menu.getId(btn.getText()));
                        menu.refresh((ArsenalState) controller.tick(0));
                    }
                    case KeyCode.D -> {
                        menu.refreshDescription(menu.getId(btn.getText()));
                    }
                    case KeyCode.DIGIT1 -> {
                        menu.addAbilityToCombine(menu.getId(btn.getText()));
                        menu.refreshCombine();
                    }
                    case KeyCode.DIGIT2 -> {
                        menu.removeFromCombine(menu.getId(btn.getText()));
                        menu.refreshCombine();
                    }
                    case KeyCode.ENTER -> {
                        if (menu.getCombiner().size() > 1) {
                            controller.handle(new Combine(menu.getCombiner().get(0), menu.getCombiner().get(1)));
                            menu.refresh((ArsenalState) controller.tick(TICK));
                            menu.clearCombiner();
                        }
                    }
                    case KeyCode.I -> visualChange();
                    default -> { }
                }
            }
        });
    }

    private void pauseAlert() {
        final Alert alert = new Alert(Alert.AlertType.NONE);
        final ButtonType resume = new ButtonType("RESUME");
        final ButtonType quit = new ButtonType("QUIT & SAVE");
        alert.setTitle("PAUSE");
        alert.setContentText("Game Paused");
        alert.getButtonTypes().setAll(resume, quit);
        timeline.pause();
        final Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == resume) {
            timeline.play();
        } else if (result.isPresent() && result.get() == quit) {
            quit();
        }
    }

    private void quitAlert() {
        final Alert alert = new Alert(Alert.AlertType.NONE);
        final ButtonType resume = new ButtonType("RESUME");
        final ButtonType reset = new ButtonType("RESET & QUIT");
        final ButtonType quit = new ButtonType("QUIT & SAVE");
        alert.setTitle("QUIT THE GAME?");
        alert.setContentText("");
        alert.getButtonTypes().setAll(resume, reset, quit);
        timeline.pause();
        final Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == resume && stage.getScene().equals(board.getScene())) {
            timeline.play();
        } else if (result.isPresent() && result.get() == reset) {
            controller.resetProgress();
            quit();
        } else if (result.isPresent() && result.get() == quit) {
            quit();
        }
    }

    private void visualChange() {
        if (stage.getScene().equals(menu.getScene()) && menu.getEquipped().size() == LOADOUT_SIZE) {
            timeline.play();
            controller.toBattle();
            board.refreshAbilities(menu.getEquipped());
            stage.setScene(board.getScene());
        } else if (stage.getScene().equals(board.getScene())) {
            menu.clearMenus();
            timeline.pause();
            controller.toArsenal();
            menu.refresh((ArsenalState) controller.tick(TICK));
            stage.setScene(menu.getScene());
        }
    }

    private void quit() {
        controller.saveOnClose();
        stage.close();
    }

    /**
     * The entry point of the program.
     * 
     * @param args arguments from launching
     */
    public static void entry(final String[] args) {
        launch(args);
    }
}
