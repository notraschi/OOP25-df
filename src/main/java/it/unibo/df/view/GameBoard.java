package it.unibo.df.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.df.configurations.Constants;
import it.unibo.df.dto.AbilityView;
import it.unibo.df.dto.SpecialAbilityView;
import it.unibo.df.gs.CombatState;
import it.unibo.df.model.abilities.Vec2D;
import it.unibo.df.model.combat.Cooldown;
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
    private static final int MAX_SIZE_PERC = 100;
    private static final int BOARD_SIZE_PERC = 80;
    private static final int KEYS_AREA_ROWS = 2;
    private static final int ENEMY_NUMBER = 2;
    private static final int EFFECT_DISPLAY_DURATION = 200;

    private final int loadoutSize;
    private final StackPane[][] playAreaMat;
    private GridPane playArea;
    private GridPane abilityArea;
    private final List<String> keys;
    private ProgressBar lifeBar;
    private List<AbilityView> equipped;
    private final List<ProgressBar> enemyBars = new LinkedList<>();
    private final List<ActiveEffect> activeEffects = new LinkedList<>();
    private Scene board;

    /**
     * @param keys
     */
    public GameBoard(final List<String> keys, final int loadoutSize) {
        playAreaMat = new StackPane[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        this.loadoutSize = loadoutSize;
        this.keys = List.copyOf(keys);
        setupBoardScene();
    }

    private void setupBoardScene() {
        final GridPane centerPane = new GridPane();
        centerPane.getStyleClass().add("board");
        

        playArea = new GridPane();
        playArea.getStyleClass().add("board");
        formatColumns(centerPane, 1, MAX_SIZE_PERC);
        formatRows(centerPane, 1, BOARD_SIZE_PERC);
        formatRows(centerPane, 1, MAX_SIZE_PERC - BOARD_SIZE_PERC);

        formatColumns(playArea, Constants.BOARD_SIZE, MAX_SIZE_PERC / Constants.BOARD_SIZE);
        formatRows(playArea,  Constants.BOARD_SIZE, MAX_SIZE_PERC / Constants.BOARD_SIZE);

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
        final GridPane area = new GridPane();
        formatColumns(area,1, MAX_SIZE_PERC);
        formatRows(area, ENEMY_NUMBER+1, MAX_SIZE_PERC);
        lifeBar = new ProgressBar(1.0);
        lifeBar.setMaxWidth(Double.MAX_VALUE);
        lifeBar.getStyleClass().add("playerLifeBar");
        area.add(lifeBar, 0, 0);
        for (int i = 1; i <= ENEMY_NUMBER; i++){
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

    private void refreshMap(final CombatState gs) {
        final List<Vec2D> enemyPosition = gs.enemies().values().stream()
            .map(enemy -> enemy.position())
            .toList();

        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                playAreaMat[i][j].getStyleClass().clear();
            }
        }
        playAreaMat[gs.player().position().x()][gs.player().position().y()].getStyleClass().add(
            gs.activeDisrupt().equals(SpecialAbilityView.NONE) ? 
            "player" : 
            "playerSpecial"
        );
        
        for (var e : enemyPosition) {
            playAreaMat[e.x()][e.y()].getStyleClass().add("enemy");
        }

        // activeEffects.forEach(set -> set.forEach(cell -> playAreaMat[cell.x()][cell.y()].getStyleClass().add("move")));
        activeEffects.stream().map(ActiveEffect::effect)
            .forEach(set -> set.forEach(cell -> playAreaMat[cell.x()][cell.y()].getStyleClass().add("move")));
    }

    private void refreshLife(final CombatState gs) {
        lifeBar.setProgress(gs.player().hpRatio());
        int index = 0;
        
        for (var e : gs.enemies().values()){
            enemyBars.get(index).setProgress(e.hpRatio());
            index ++;
        }
    }

    private void refreshCooldown(CombatState gs) {
        Iterator<Long> abColIt = gs.player().cooldownAbilities().iterator();
        Iterator<AbilityView> abIt = equipped.iterator();
        for (final var ab : abilityArea.getChildren()) {
            
            if (ab instanceof Label lbl && abColIt.hasNext()){
                double ratio = Math.max(0, Math.min(1, ((double)abColIt.next()) / (abIt.next().cooldown())));
                lbl.setStyle(
                    "-fx-background-color: linear-gradient(to top, " +
                    "gray " + (ratio * 100) + "%, " +
                    "white " + (ratio * 100) + "%);" +
                    "-fx-border-color: black;");
            }
        }
    }

    /**
    * @param equipment
    */
    public void refreshAbility(final List<AbilityView> equipment){
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
	* @param gs
	*/
    public void refresh(final CombatState gs, long deltaTime) {
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

    private record ActiveEffect(Cooldown cooldown, Set<Vec2D> effect) {}
}
