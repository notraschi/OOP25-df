package it.unibo.df.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameBoard extends Application{

    @Override
    public void start(Stage stage){
        stage.setTitle("Il gioco incredibile");
        Scene scene = new Scene(null, 100, 100, Color.AQUA);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
