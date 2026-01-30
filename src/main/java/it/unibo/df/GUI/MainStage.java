package it.unibo.df.GUI;

import it.unibo.df.controller.Controller;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Move;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainStage extends Application{
    private Controller controller = new Controller();
    private GameBoard board = new GameBoard();
    private AbilityMenu menu = new AbilityMenu();
    private Timeline timeline;



    @Override
    public void start(Stage stage){
        stage.setScene(board.getBoardScene());
        addKeysListeners(stage);
        controller.toBattle();

        timeline = new Timeline(
            new KeyFrame(Duration.millis(500), e->tick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        timeline.play();
        
        stage.setMaximized(true);
        double min = (Double.min(
            Screen.getPrimary().getBounds().getHeight(),
            Screen.getPrimary().getBounds().getWidth()))/2;

        stage.setMinHeight(min);
        stage.setMinWidth(min);
        stage.show();

    }

    private void tick(){
        var gs = (CombatState)controller.tick();
        board.reset(gs.playerPos());
    }

    private void visualChange(Stage stage){
        if (stage.getScene().equals(menu.getScene()) ){
            //controller.toBattle();
            timeline.play();
            stage.setScene(board.getBoardScene());
        }else if (stage.getScene().equals(board.getBoardScene())){
            timeline.stop();
            stage.setScene(menu.getScene());
        }
    }

    private void addKeysListeners(Stage stage){
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.Q) {
                stage.close();
            }else if (event.getCode() == KeyCode.DOWN) {
                controller.handle(Move.DOWN);
            }else if (event.getCode() == KeyCode.UP) {
                controller.handle(Move.UP);
            }else if (event.getCode() == KeyCode.RIGHT) {
                controller.handle(Move.RIGHT);
            }else if (event.getCode() == KeyCode.LEFT) {
                controller.handle(Move.LEFT);
            }else if (event.getCode() == KeyCode.I){
                visualChange(stage);
            }

        });
    }

    public static void entry(String[] args) {
        launch(args);
    }
}
    

