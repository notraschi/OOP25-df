package it.unibo.df.GUI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.unibo.df.gs.AbilityView;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.CombatState;
import it.unibo.df.model.abilities.Vec2D;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class GameBoard {
    private int boardSize = 10;
    private GridPane playArea;
    private GridPane abilityArea;
    private List<String> keys;
    private Scene board;
    private ProgressBar lifeBar;
    
    public GameBoard(List<String> keys){
        this.keys=List.copyOf(keys);
        setupBoardScene();
    }

    private void setupBoardScene(){
        BorderPane borderPane = new BorderPane();
        StackPane externalWindowPane = new StackPane();
        GridPane centerPane = new GridPane();
        playArea = new GridPane();

        centerPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        formatColumns(centerPane,1,100);
        formatRows(centerPane,1,80);
        formatRows(centerPane,1,20);
        
        formatColumns(playArea, this.boardSize, 100/this.boardSize);
        formatRows(playArea,  this.boardSize, 100/this.boardSize);
                
        centerPane.add(fillPlayArea(playArea), 0, 0); 
        centerPane.add(fillLowBar(), 0, 1);

        externalWindowPane.getChildren().add(centerPane);
        borderPane.setCenter(externalWindowPane);

        ChangeListener<Number> resizebility = (obs, oldValue, newValue) -> {
            double size = Double.min(externalWindowPane.getWidth(), externalWindowPane.getHeight());
            centerPane.setPrefSize(size, size);
        };

        externalWindowPane.widthProperty().addListener(resizebility);
        externalWindowPane.heightProperty().addListener(resizebility);

        board = new Scene(borderPane);
        board.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
        
    }

    private void formatColumns(GridPane grid, int size, double perc){
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(perc);
        for (int i = 0; i<size; i++){
            grid.getColumnConstraints().add(cc);
        }
    }

    private void formatRows(GridPane grid, int size, double perc){
        RowConstraints cr = new RowConstraints();
        cr.setPercentHeight(perc);
        for (int i = 0; i<size; i++){
            grid.getRowConstraints().add(cr);
        }
    }

    private GridPane fillPlayArea(GridPane grid){
        for (int i = 0; i < grid.getColumnCount(); i++){
            for (int j = 0; j < grid.getRowCount(); j++){
                StackPane cell = new StackPane();
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                grid.add(cell, i, j);
            }
        }
        return grid;
    }

    private GridPane fillAbilityArea(){
        abilityArea = new GridPane();
        formatColumns(abilityArea, 3, 33);
        formatRows(abilityArea, 1, 70);
        formatRows(abilityArea, 1, 30);
        for (int i = 0; i < 3; i++){
            Label lbl = new Label("Ability"+(i+1));
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            abilityArea.add(lbl, i, 0);
        }   
        return abilityArea;
    }

    private GridPane fillLifeBarArea(){
        lifeBar = new ProgressBar(1.0);
        GridPane area = new GridPane();
        formatColumns(area,1, 100);
        formatRows(area, 1, 100);
        lifeBar.setMaxWidth(Double.MAX_VALUE);
        //lifeBar.setMaxHeight(Double.MAX_VALUE);
        area.add(lifeBar, 0, 0);
        return area;
    }
    
    private GridPane fillKeysArea(){
        GridPane area = new GridPane();
        int index=0;
        formatColumns(area, 4, 25);
        formatRows(area, 2,50);
        for (int i = 0; i < 2 ; i++){
            for (int j = 0; j<4; j++){
                Label lbl = new Label(keys.get(index));
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                area.add(lbl, j, i);
                index++;
            }
        }
        return area;
    }

    private GridPane fillLowBar(){
        GridPane lowBar = new GridPane();
        formatColumns(lowBar, 3, 33);
        formatRows(lowBar, 1, 100);
        lowBar.add(fillAbilityArea(), 0, 0);
        lowBar.add(fillLifeBarArea(), 1, 0);
        lowBar.add(fillKeysArea(), 2, 0);
        return lowBar;
    }

    private void refreshMap(CombatState gs, Set<Vec2D> effects){
        Integer indexOfList= 0;
        List<Vec2D> enemyPosition=List.copyOf(gs.enemies().entrySet().stream().map(e->e.getValue().position()).toList());
        for (int i = 0; i < this.boardSize; i++){
            for (int j = 0; j < this.boardSize; j++){
                indexOfList=Integer.parseInt(String.valueOf(i)+String.valueOf(j));
                if (gs.player().position().equals(new Vec2D(i,j) )){
                    playArea.getChildren().get(indexOfList).getStyleClass().add("casellaplayer");
                }else if (enemyPosition.contains(new Vec2D(i,j))){
                    playArea.getChildren().get(indexOfList).getStyleClass().add("casellaenemy");
                }else if (effects.contains(new Vec2D(i,j))){
                    playArea.getChildren().get(indexOfList).getStyleClass().add("caselladanno");
                }else{
                    playArea.getChildren().get(indexOfList).getStyleClass().clear();
                }
            }
        }
    }

    private void refreshLife(CombatState gs){
        lifeBar.setProgress(gs.player().hpRatio());
    }
    
    public void refreshAbility(ArsenalState gs){
        Iterator<AbilityView> ability = gs.equipped().iterator();
        for (var e : abilityArea.getChildren()){
            if (e instanceof Label content){
                content.setText(ability.next().name());
            }
        }
    }
    /**
	 * refresh the game board, to move enemy player and color where an ability hit
	 * 
	 * @param a combat state
	 */
    public void refresh(CombatState gs){
        Set<Vec2D> eff= new HashSet<>();
        for (var e : gs.effects()){
            eff.addAll(e);
        }
        refreshMap(gs, eff);
        refreshLife(gs);
    
    }
    /**
	 * 
	 * @return a scene of the board
	 */
    public Scene getScene(){
        return board;
    }

}
