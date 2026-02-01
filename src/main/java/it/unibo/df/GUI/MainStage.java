package it.unibo.df.GUI;

import java.util.List;

import it.unibo.df.controller.Controller;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Equip;
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
    private GameBoard board = new GameBoard(
        List.of("← \nleft","→ \nright",
        "↑ \nup",
        "↓ \ndown",
        "I \ninventory",
        "Z \nability 1",
        "X \nability 2",
        "C \nability 3",
        "Q \nquit",
        "SPACE \npause")
    );
    private AbilityMenu menu = new AbilityMenu();
    private Timeline timeline;



    @Override
    public void start(Stage stage){
        addKeysListeners(stage);
        
        menu.refresh((ArsenalState)controller.tick());
        
        controller.handle(new Equip(1));
        controller.handle(new Equip(2));
        controller.handle(new Equip(3));
        
        board.refreshAbility((ArsenalState)controller.tick());


        controller.toBattle();
        board.refresh((CombatState)controller.tick());

        timeline = new Timeline(
            new KeyFrame(Duration.millis(500), e->tick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        stage.setScene(menu.getScene());
        visualChange(stage);
        stage.setMaximized(true);
        double min = (Double.min(
            Screen.getPrimary().getBounds().getHeight(),
            Screen.getPrimary().getBounds().getWidth()))/2;
        stage.setMinHeight(min);
        stage.setMinWidth(min);

        stage.show();

    }

    private void tick(){
        board.refresh((CombatState)controller.tick());
    }

    private void visualChange(Stage stage){
        if (stage.getScene().equals(menu.getScene()) ){
            //controller.toBattle();

            timeline.play();
            //board.
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
                controller.handle(stage.getScene().equals(menu.getScene())?null:Move.DOWN);
            }else if (event.getCode() == KeyCode.UP) {
                controller.handle(stage.getScene().equals(menu.getScene())?null:Move.UP);
            }else if (event.getCode() == KeyCode.RIGHT) {
                controller.handle(stage.getScene().equals(menu.getScene())?null:Move.RIGHT);
            }else if (event.getCode() == KeyCode.LEFT) {
                controller.handle(stage.getScene().equals(menu.getScene())?null:Move.LEFT);
            }else if (event.getCode() == KeyCode.I){
                visualChange(stage);
            }else if (event.getCode() == KeyCode.SPACE){
                System.out.print("pause");
            }else if (event.getCode() == KeyCode.Z){
                controller.handle(stage.getScene().equals(menu.getScene())?new Equip(1):Attack.ABILITY1);
            }else if (event.getCode() == KeyCode.X){
                controller.handle(stage.getScene().equals(menu.getScene())?new Equip(1):Attack.ABILITY2);
            }else if (event.getCode() == KeyCode.C){
                controller.handle(stage.getScene().equals(menu.getScene())?new Equip(1):Attack.ABILITY3);
            }else if (event.getCode() == KeyCode.DIGIT1){
                controller.handle(Move.DOWN);
            }else if (event.getCode() == KeyCode.DIGIT2){
                controller.handle(Move.DOWN);
            }

        });
    }

    public static void entry(String[] args) {
        launch(args);
    }
}
    

