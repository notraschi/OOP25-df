package it.unibo.df.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.CombatState;
import it.unibo.df.model.abilities.Vec2D;
import static it.unibo.df.view.PaneFormatter.formatColumns;
import static it.unibo.df.view.PaneFormatter.formatRows;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
/**
 * 
 */
public class GameBoard {
    private final int MAX_SIZE_PERC = 100;
    private final int BOARD_SIZE_PERC = 80;
    private final int KEYS_AREA_ROWS = 2;
    private final int boardSize = 10;
    private final int loadoutSize ;
    private final StackPane[][] playAreaMat;
    private GridPane playArea;
    private GridPane abilityArea;
    private final List<String> keys;
    private ProgressBar lifeBar;
    private Scene board;

    /**
     * @param keys
     */
    public GameBoard(final List<String> keys, final int loadoutSize) {
        playAreaMat = new StackPane[boardSize][boardSize];
        this.loadoutSize = loadoutSize;
        this.keys = List.copyOf(keys);
        setupBoardScene();
    }

    private void setupBoardScene() {
        final GridPane centerPane = new GridPane();

        playArea = new GridPane();
        formatColumns(centerPane, 1, MAX_SIZE_PERC);
        formatRows(centerPane, 1, BOARD_SIZE_PERC);
        formatRows(centerPane, 1, MAX_SIZE_PERC - BOARD_SIZE_PERC);

        formatColumns(playArea, this.boardSize, MAX_SIZE_PERC / this.boardSize);
        formatRows(playArea,  this.boardSize, MAX_SIZE_PERC / this.boardSize);

        centerPane.add(fillPlayArea(playArea), 0, 0); 
        centerPane.add(fillLowBar(), 0, 1);

        SceneResizer resizer = new SceneResizer(centerPane, Double.valueOf(BOARD_SIZE_PERC) / Double.valueOf(MAX_SIZE_PERC), MAX_SIZE_PERC / MAX_SIZE_PERC);
        board = new Scene(resizer.getBorderPane());

        board.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
    }

    private GridPane fillPlayArea(final GridPane grid) {
        for (int i = 0; i < grid.getColumnCount(); i++) {
            for (int j = 0; j < grid.getRowCount(); j++) {
                final StackPane cell = new StackPane();
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                grid.add(cell, i, j);
                playAreaMat[i][j] = cell;
            }
        }
        return grid;
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
        lifeBar = new ProgressBar();
        final GridPane area = new GridPane();
        formatColumns(area,1, MAX_SIZE_PERC);
        formatRows(area, 1, MAX_SIZE_PERC);
        lifeBar.setMaxWidth(Double.MAX_VALUE);
        area.add(lifeBar, 0, 0);
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

    private GridPane fillLowBar() {
        final GridPane lowBar = new GridPane();
        final int nColumns = 3;
        formatColumns(lowBar, nColumns, MAX_SIZE_PERC / nColumns);
        formatRows(lowBar, 1, MAX_SIZE_PERC);
        lowBar.add(fillAbilityArea(), 0, 0);
        lowBar.add(fillLifeBarArea(), 1, 0);
        lowBar.add(fillKeysArea(), 2, 0);
        return lowBar;
    }

    private void refreshMap(final CombatState gs, final Set<Vec2D> effects) {
        final List<Vec2D> enemyPosition = List.copyOf(
            gs.enemies().entrySet().stream()
            .map(e -> e.getValue().position())
            .toList()
        );
        //gs.matchStatus()
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (gs.player().position().equals(new Vec2D(i, j))) {
                    playAreaMat[i][j].getStyleClass().add("casellaplayer");
                } else if (enemyPosition.contains(new Vec2D(i, j))) {
                    playAreaMat[i][j].getStyleClass().add("casellaenemy");
                } else if (effects.contains(new Vec2D(i, j))) {
                    playAreaMat[i][j].getStyleClass().add("caselladanno");
                } else {
                    playAreaMat[i][j].getStyleClass().clear();
                }

            }
        }
    }

    private void refreshLife(final CombatState gs) {
        lifeBar.setProgress(gs.player().hpRatio());
    }

    /**
    * @param equipment
    */
    public void refreshAbility(final List<AbilityView> equipment){
        final Iterator<AbilityView> equipIt = equipment.iterator();
        for (final var e : abilityArea.getChildren()) {
            if (e instanceof Label content) {
                content.setText(equipIt.hasNext() ? equipIt.next().name() : "");
            }
        }
    }

    /**
	* refresh the game board, to move enemy player and color where an ability hit.
	* @param gs
	*/
    public void refresh(final CombatState gs) {
        final Set<Vec2D> eff = new HashSet<>();
        for (final var e : gs.effects()) {
            eff.addAll(e);
        }
        refreshMap(gs, eff);
        refreshLife(gs);
    }

    /**
	* @return a scene of the board
	*/
    public Scene getScene() {
        return board;
    }

}
