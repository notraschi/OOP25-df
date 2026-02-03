package it.unibo.df.GUI;

import java.util.List;
import java.util.Optional;
import it.unibo.df.controller.Controller;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.CombatState;
import it.unibo.df.input.Attack;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Move;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainStage extends Application{
    private Controller controller = new Controller();
    private GameBoard board = new GameBoard(
        List.of(
            "← \nleft",
            "→ \nright",
            "↑ \nup",
            "↓ \ndown",
            "I \ninventory",
            "Z \nability 1",
            "X \nability 2",
            "C \nability 3",
            "Q \nquit",
            "SPACE \npause"
        )
    );
    private AbilityMenu menu = new AbilityMenu(
        List.of(
            "ENTER \nto combine abilities",
            "Z \nto add to loadout",
            "I \nBack to play",
            "1 \nselect in mixer",
            "Q \nquit"
        )
    );
    private Timeline timeline;
    private Stage stage;



    @Override
    public void start(Stage s){
        stage = s;
        addKeysListenersToBoard(board.getScene());
        addKeysListenersToMenu(menu.getScene());
        addKeysListenersToStage();


        controller.handle(new Equip(1));
        controller.handle(new Equip(2));
        menu.refresh((ArsenalState)controller.tick());
        

        timeline = new Timeline(
            new KeyFrame(Duration.millis(500), e->tick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        stage.setScene(menu.getScene());
        visualChange();
        board.refresh((CombatState)controller.tick());
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

    private void visualChange(){
        if (stage.getScene().equals(menu.getScene()) ){
            timeline.play();
            controller.toBattle();
            board.refreshAbility(menu.getEquipped());
            stage.setScene(board.getScene());
        }else if (stage.getScene().equals(board.getScene())){
            timeline.stop();
            controller.toArsenal();
            stage.setScene(menu.getScene());
        }
    }

    private void pause(){
        Alert alert = new Alert(Alert.AlertType.NONE);
        ButtonType resume= new ButtonType("resume");
        ButtonType quit = new ButtonType("quit");
        alert.setTitle("pause");
        alert.setContentText("Game Paused");
        alert.getButtonTypes().setAll(resume,quit);
        if (stage.getScene().equals(board.getScene())){
            timeline.pause();
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == resume){
                timeline.play();
            }else if (result.isPresent() && result.get() == quit){
                stage.close();
            }
        }
        
    }

    private void addKeysListenersToBoard(Scene boardScene){
        boardScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()){
                case KeyCode.DOWN -> controller.handle(Move.DOWN);
                case KeyCode.UP -> controller.handle(Move.UP);
                case KeyCode.RIGHT -> controller.handle(Move.RIGHT);
                case KeyCode.LEFT -> controller.handle(Move.LEFT);
                case KeyCode.SPACE -> pause();
                case KeyCode.Z -> controller.handle(Attack.ABILITY1);
                case KeyCode.X -> controller.handle(Attack.ABILITY2);
                case KeyCode.C -> controller.handle(Attack.ABILITY3);
                default -> {}
            }
        });
    }
    
    private void addKeysListenersToStage(){
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event ->{
            switch(event.getCode()){
                case KeyCode.Q -> stage.close();
                case KeyCode.I -> visualChange();
                default -> {}
            }
        });
    }

    private void addKeysListenersToMenu(Scene menuScene){
        menuScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (menu.getGroup().getSelectedToggle() != null){
                ToggleButton btn = (ToggleButton)menu.getGroup().getSelectedToggle();
                switch(event.getCode()){
                    case KeyCode.Z -> {
                        controller.handle(new Equip(menu.getId(btn.getText())));
                        menu.refresh((ArsenalState)controller.tick());
                    }
                    case KeyCode.DIGIT1 -> {
                        menu.addAbilityToCombine(btn.getText());
                        menu.refreshCombine();
                    }
                    case KeyCode.ENTER -> {

                    }
                    default -> {}
                }
            }
        });
    }
    
    public static void entry(String[] args) {
        launch(args);
    }
}
    

