package it.unibo.df.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class AbilityMenu extends Application{

    @Override
    public void start (Stage stage){
        GridPane menuArea = new GridPane();
        formatColumns(menuArea, 1, 100);
        formatRows(menuArea, 1, 80);
        formatRows(menuArea, 1, 20);
        menuArea.add(fillUpperArea(), 0, 0);
        menuArea.add(fillLowerArea(), 0, 1);

        Scene scene = new Scene(menuArea);
        scene.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
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

    private GridPane fillUpperArea(){
        GridPane area = new GridPane();
        formatColumns(area, 2, 50);
        formatRows(area, 1, 100);
        area.add(fillInventaryArea(), 0, 0);
        area.add(fillMixerArea(), 1, 0);
        return area;
    }

    private GridPane fillInventaryArea(){
        GridPane area = new GridPane();
        formatColumns(area, 5, 20);
        formatRows(area, 5, 20);
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Label lbl = new Label("Ability n "+(i+j));
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                area.add(lbl, i, j);
            }
        }
        return area; 
    }

    private GridPane fillMixerArea(){
        GridPane area = new GridPane();
        formatColumns(area, 2, 50);
        formatRows(area, 1, 100);
        for (int j = 0; j < 2; j++){
            Label lbl = new Label("this area is for mixer and your equipment");
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            area.add(lbl, j, 0);
        }
        return area;
    }

    private GridPane fillLowerArea(){
        GridPane area = new GridPane();
        formatColumns(area, 2, 50);
        formatRows(area, 1, 100);
        area.add(fillDescriptionArea(), 0, 0);
        area.add(fillBindingsArea(), 1, 0);
        return area;
    }

    private Label fillDescriptionArea(){
        Label lbl = new Label("This area is for descriptions");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    private Label fillBindingsArea(){
        Label lbl = new Label("This area is for Bindings");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    public static void entry(String[] args) {
        launch(args);
    }



}
