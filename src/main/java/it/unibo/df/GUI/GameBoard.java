package it.unibo.df.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameBoard extends Application{
    //private int boardSize = 10;
    private String title = "Il nostro gioco";

    @Override
    public void start(Stage stage){
        stage.setTitle(title);
        GridPane mainPanel = new GridPane();
        formatColumns(mainPanel,1,100);
        formatRows(mainPanel,1,80);
        formatRows(mainPanel,1,20);
        GridPane playArea = new GridPane();
        formatColumns(playArea, 20, 100/20);
        formatRows(playArea, 10, 10);
        mainPanel.add(fillPlayArea(playArea), 0, 0); 
        mainPanel.add(fillLowBar(), 0, 1);
        Scene scene = new Scene(mainPanel);
        scene.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinWidth(400);
        stage.setMinHeight(700);
        stage.show();
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
                if (i==0&&j==0){
                    cell.setBackground(new Background(openImageAsBackground("/images/wizard.png")));
                }else if (i== grid.getColumnCount()-1 && j== grid.getRowCount()-1){
                    cell.setBackground(new Background(openImageAsBackground("/images/barbarian.png")));
                }
                grid.add(cell, i, j);
            }
        }
        return grid;
    }

    private BackgroundImage openImageAsBackground(String filaname){
        return new BackgroundImage(
            new Image(filaname),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                100, 100, true, true, false, true
            )
        );
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

    public static void entry(String[] args) {
        launch(args);
    }
}
