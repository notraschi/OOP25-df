package it.unibo.df.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.df.configurations.Constants;
import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.CombatState;
import it.unibo.df.utility.Cooldown;
import it.unibo.df.utility.Vec2D;

import static it.unibo.df.view.PaneFormatter.formatColumns;
import static it.unibo.df.view.PaneFormatter.formatRows;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * The Scene of the area where the user play.
 */
public class GameBoard {
    private static final int MAX_SIZE_PERC = 100;
    private static final int BOARD_SIZE_PERC = 80;
    private static final int KEYS_AREA_ROWS = 2;
    private static final int ENEMY_NUMBER = 2;
    private static final int EFFECT_DISPLAY_DURATION = 300;
    private final int loadoutSize;
    private GraphicsContext graphicsContext;
    private Canvas playArea;
    private GridPane abilityArea;
    private final List<String> keys;
    private ProgressBar lifeBar;
    private List<AbilityView> equipped;
    private final List<ProgressBar> enemyBars = new LinkedList<>();
    private final List<ActiveEffect> activeEffects = new LinkedList<>();
    private Scene board;

    /**
     * Setup the board and creates the view.
     * 
     * @param keys List of command to show to the user
     * @param loadoutSize size of ability can be equipped
     */
    public GameBoard(final List<String> keys, final int loadoutSize) {
        this.loadoutSize = loadoutSize;
        this.keys = List.copyOf(keys);
        setupBoardScene();
    }

    private void setupBoardScene() {
        final GridPane centerPane = new GridPane();
        centerPane.getStyleClass().add("board");
        final StackPane canvasWrapper = new StackPane();
        canvasWrapper.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playArea = new Canvas();
        graphicsContext = playArea.getGraphicsContext2D();
        playArea.setManaged(false);
        playArea.widthProperty().bind(canvasWrapper.widthProperty());
        playArea.heightProperty().bind(canvasWrapper.heightProperty());
        canvasWrapper.getChildren().add(playArea);
        formatColumns(centerPane, 1, MAX_SIZE_PERC);
        formatRows(centerPane, 1, BOARD_SIZE_PERC);
        formatRows(centerPane, 1, MAX_SIZE_PERC - BOARD_SIZE_PERC);
        centerPane.add(canvasWrapper, 0, 0); 
        centerPane.add(fillLowBarArea(), 0, 1);
        final SceneResizer resizer = new SceneResizer(
            centerPane,
            (double) BOARD_SIZE_PERC / MAX_SIZE_PERC,
            MAX_SIZE_PERC / MAX_SIZE_PERC
        );
        board = new Scene(resizer.getBorderPane());
        board.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
    }

    private GridPane fillAbilityArea() {
        abilityArea = new GridPane();
        formatColumns(abilityArea, loadoutSize, MAX_SIZE_PERC / loadoutSize);
        formatRows(abilityArea, 1, MAX_SIZE_PERC);
        for (int i = 0; i < loadoutSize; i++) {
            final Label lbl = new Label();
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            abilityArea.add(lbl, i, 0);
        }
        return abilityArea;
    }

    private GridPane fillLifeBarArea() {
        final GridPane area = new GridPane();
        formatColumns(area, 1, MAX_SIZE_PERC);
        formatRows(area, ENEMY_NUMBER + 1, MAX_SIZE_PERC);
        lifeBar = new ProgressBar(1.0);
        lifeBar.setMaxWidth(Double.MAX_VALUE);
        lifeBar.getStyleClass().add("playerLifeBar");
        area.add(lifeBar, 0, 0);
        for (int i = 1; i <= ENEMY_NUMBER; i++) {
            final ProgressBar enemyBar = new ProgressBar(1.0);
            enemyBar.setMaxWidth(Double.MAX_VALUE);
            enemyBar.getStyleClass().add("enemyLifeBar");
            area.add(enemyBar, 0, i);
            enemyBars.add(enemyBar);
        }
        return area;
    }

    private GridPane fillKeysArea() {
        final GridPane area = new GridPane();
        final Iterator<String> keysIt = keys.iterator();
        formatRows(area, KEYS_AREA_ROWS, MAX_SIZE_PERC / KEYS_AREA_ROWS);
        formatColumns(area, keys.size() / KEYS_AREA_ROWS, MAX_SIZE_PERC / (keys.size() / KEYS_AREA_ROWS));
        for (int i = 0; i < KEYS_AREA_ROWS; i++) {
            for (int j = 0; j < keys.size() / KEYS_AREA_ROWS; j++) {
                final Label lbl = new Label(keysIt.hasNext() ? keysIt.next() : "");
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                area.add(lbl, j, i);
            }
        }
        return area;
    }

    private GridPane fillLowBarArea() {
        final GridPane lowBar = new GridPane();
        final int nColumns = 3;
        formatColumns(lowBar, nColumns, MAX_SIZE_PERC / nColumns);
        formatRows(lowBar, 1, MAX_SIZE_PERC);
        lowBar.add(fillAbilityArea(), 0, 0);
        lowBar.add(fillLifeBarArea(), 1, 0);
        lowBar.add(fillKeysArea(), 2, 0);
        return lowBar;
    }

    private void refreshMap(final CombatState gs) {
        final double cellSize = playArea.getHeight() / Constants.BOARD_SIZE;
        graphicsContext.clearRect(0, 0, playArea.getWidth(), playArea.getHeight());
        graphicsContext.setFill(Color.GRAY);
        gs.enemies().values().stream()
            .filter(en -> en.hp() == 0)
            .forEach(e -> graphicsContext.fillRect(
                e.position().x() * cellSize,
                e.position().y() * cellSize,
                cellSize,
                cellSize
            )
        );
        graphicsContext.setFill(Color.RED);
        gs.enemies().values().stream()
            .filter(en -> en.hp() != 0)
            .forEach(e -> graphicsContext.fillRect(
                e.position().x() * cellSize,
                e.position().y() * cellSize,
                cellSize,
                cellSize
            )
        );
        graphicsContext.setFill(gs.isDisruptActive() ? Color.PURPLE : Color.GREEN);
        graphicsContext.fillRect(
            gs.player().position().x() * cellSize, 
            gs.player().position().y() * cellSize, 
            cellSize, 
            cellSize
        );
        graphicsContext.setStroke(Color.BLUE);
        activeEffects.stream()
            .map(ActiveEffect::effect)
            .forEach(e -> e.forEach(cell -> graphicsContext.strokeRect(
                cell.x() * cellSize,
                cell.y() * cellSize,
                cellSize,
                cellSize
            ))
        );

    }
 
    private void refreshLife(final CombatState gs) {
        lifeBar.setProgress(gs.player().hpRatio());
        final Iterator<ProgressBar> enemyBarsIt = enemyBars.iterator();
        for (final var e : gs.enemies().values()) {
            enemyBarsIt.next().setProgress(e.hpRatio());
        }
    }

    private void refreshCooldown(final CombatState gs) {
        final Iterator<Long> abColIt = gs.player().cooldownAbilities().iterator();
        final Iterator<AbilityView> abIt = equipped.iterator();
        for (final var ab : abilityArea.getChildren()) {
            if (ab instanceof Label lbl && abColIt.hasNext()) {
                final double ratio = (double) abColIt.next() / abIt.next().cooldown();
                lbl.setStyle(
                    "-fx-background-color: linear-gradient(to top, " 
                    + "gray " + (ratio * 100) + "%, "
                    + "white " + (ratio * 100) + "%);"
                    + "-fx-border-color: black;"
                );
            }
        }
    }

    /**
     * refresh the ability area to add the equipment to it.
     * 
     * @param equipment list of abilities equipped
     */
    public void refreshAbilities(final List<AbilityView> equipment) {
        equipped = equipment;
        final Iterator<AbilityView> equipIt = equipment.iterator();
        for (final var e : abilityArea.getChildren()) {
            if (e instanceof Label content) {
                content.setText(equipIt.hasNext() ? equipIt.next().name() : "");
            }
        }
    }

    /**
     * refresh the game board, to move enemy player and color where an ability hit.
     * 
     * @param gs combat state
     * @param deltaTime time passed from last tick
     */
    public void refresh(final CombatState gs, final long deltaTime) {
        activeEffects.forEach(ae -> ae.cooldown.update(deltaTime));
        activeEffects.removeIf(ae -> !ae.cooldown.isActive());
        activeEffects.addAll(
            gs.effectsOnBoard().stream()
                .map(eff -> new ActiveEffect(new Cooldown(EFFECT_DISPLAY_DURATION), eff))
                .peek(ae -> ae.cooldown.begin())
                .collect(Collectors.toSet())
        );
        refreshMap(gs);
        refreshLife(gs);
        refreshCooldown(gs);
    }

    /**
     * @return a scene of the board
     */
    public Scene getScene() {
        return board;
    }

    private record ActiveEffect(Cooldown cooldown, Set<Vec2D> effect) { }
}
