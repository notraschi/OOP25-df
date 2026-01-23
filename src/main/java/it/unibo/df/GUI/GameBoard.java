package it.unibo.df.GUI;

import javax.swing.GrayFilter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class GameBoard extends Application{
    private int boardSize = 10;
    private String title = "Il nostro gioco";

    @Override
    public void start(Stage stage){
        stage.setTitle(title);
        GridPane mainPanel = new GridPane();
        formatColumns(mainPanel,1,100);
        formatRows(mainPanel,1,80);
        formatRows(mainPanel,1,20);

        GridPane playArea = new GridPane();
        formatColumns(playArea, 10, 10);
        formatRows(playArea, 10, 10);

        mainPanel.add(fillPlayArea(playArea), 0, 0); 

        mainPanel.add(fillLowBar(), 0, 1);

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
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
        for (int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                Label lbl = new Label(i+" "+j);
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                grid.add(lbl, i, j);
            }
        }
        return grid;
    }

    private GridPane fillAbilityArea(){
        GridPane area = new GridPane();
        formatColumns(area, 3, 33);
        formatRows(area, 1, 70);
        formatRows(area, 1, 30);
        area.add(new Label("Ability 1"), 0, 0);
        area.add(new Label("Ability 2"), 1, 0);
        area.add(new Label("Ability 2"), 2, 0);
        return area;
    }
    
    private GridPane fillLowBar(){
        GridPane lowBar = new GridPane();
        formatColumns(lowBar, 3, 33);
        formatRows(lowBar, 1, 100);
        lowBar.add(fillAbilityArea(), 0, 0);
        lowBar.add(fillAbilityArea(), 1, 0);
        lowBar.add(fillAbilityArea(), 2, 0);
        return lowBar;
    }














    public static void entry(String[] args) {
        launch(args);
    }









    
/*
    private GridPane createCommandArea(){
        GridPane grid = new GridPane();
        ColumnConstraints cc = new ColumnConstraints();
        RowConstraints rc = new RowConstraints();
        cc.setPercentWidth(33);
        rc.setPercentHeight(100);
        grid.getColumnConstraints().add(cc);
        grid.getColumnConstraints().add(cc);
        grid.getColumnConstraints().add(cc);
        grid.getRowConstraints().add(rc);
        grid.add(fillAreaAbility(createAbilityArea()), 0, 0);
        grid.add(new ProgressBar(1.0),1,0);
        grid.add(new Label ("illustrazioni su tasti ecc.."),2,0);
        return grid;
    }

    private GridPane fillAreaAbility(GridPane grid){
        for (int i =0; i < 3; i++ ){
            Button btn = new Button("Ability "+i);
            btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);  
            grid.add(btn, i, 0);
        }
        return grid;
    }

    private GridPane createAbilityArea(){
        GridPane grid = new GridPane();
        for (int i=0; i < 3; i++){
            ColumnConstraints cc = new ColumnConstraints();
            RowConstraints rc = new RowConstraints();
            cc.setPercentWidth(33);
            rc.setPercentHeight(33);
            grid.getColumnConstraints().add(cc);
            grid.getRowConstraints().add(rc);
        }
        return grid; 
    }*/
}
