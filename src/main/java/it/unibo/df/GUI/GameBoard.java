package it.unibo.df.GUI;

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
    private Vec2D playerPos = new Vec2D(0,0);
    private Scene board;
    //private Vec2D enemyPos = new Vec2D(9,9);
    
    public GameBoard(){
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
                if (playerPos.x()==i&&j==playerPos.y()){
                    cell.getStyleClass().add("casellaplayer");
                }else if (i== grid.getColumnCount()-1 && j== grid.getRowCount()-1){
                    cell.getStyleClass().add("casellaenemy");
                }
                grid.add(cell, i, j);
            }
        }
        return grid;
    }

    private GridPane fillAbilityArea(){
        GridPane area = new GridPane();
        
        formatColumns(area, 3, 33);
        formatRows(area, 1, 70);
        formatRows(area, 1, 30);
        for (int i = 0; i < 3; i++){
            Label lbl = new Label("Ability"+(i+1));
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            area.add(lbl, i, 0);
        }   
        return area;
    }

    private GridPane fillLifeBarArea(){
        ProgressBar lifeBar = new ProgressBar(1.0);
        GridPane area = new GridPane();
        formatColumns(area,1, 100);
        formatRows(area, 1, 100);
        lifeBar.setMaxWidth(Double.MAX_VALUE);
        //lifeBar.setMaxHeight(Double.MAX_VALUE);
        area.add(lifeBar, 0, 0);
        return area;
    }
    
    private GridPane fillBindingArea(){
        GridPane area = new GridPane();
        formatColumns(area, 1, 100);
        formatRows(area, 1,100);
        Label lbl = new Label("qui ci vanno i pulsanti");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        area.add(lbl, 0, 0);
        return area;
    }

    private GridPane fillLowBar(){
        GridPane lowBar = new GridPane();
        formatColumns(lowBar, 3, 33);
        formatRows(lowBar, 1, 100);
        lowBar.add(fillAbilityArea(), 0, 0);
        lowBar.add(fillLifeBarArea(), 1, 0);
        lowBar.add(fillBindingArea(), 2, 0);
        return lowBar;
    }

    public void reset(Vec2D playerNextMove/*, Set<Vec2D> enemyNextMove*/){
        Integer index= 0;
        for (int i = 0; i < this.boardSize; i++){
            for (int j = 0; j < this.boardSize; j++){
                index=Integer.parseInt(String.valueOf(i)+String.valueOf(j));
                if (playerPos.x()+playerNextMove.x()==i&&j==playerPos.y()+playerNextMove.y()){
                    playArea.getChildren().get(index).getStyleClass().add("casellaplayer");
                /*}else if (enemyPos.x()+enemyNextMove.x()==i && j==enemyPos.y()+enemyNextMove.y()){
                    playArea.getChildren().get(index).getStyleClass().add("casellaenemy");
                */}else{
                    playArea.getChildren().get(index).getStyleClass().clear();
                }
            }
        }
    }

    public Scene getBoardScene(){
        return board;
    }

}
