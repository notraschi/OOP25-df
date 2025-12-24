package it.unibo.samplejavafx.mvcexample.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.samplejavafx.mvcexample.controller.DrawNumberController;
import it.unibo.samplejavafx.mvcexample.model.DrawResult;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * Graphical {@link DrawNumberView} implementation.
 */
@SuppressFBWarnings
public final class JFXDrawNumberView implements DrawNumberView {

    private static final String FRAME_NAME = "dot-fighting";
    private static final String QUIT = "Quit";
    private static final String RESET = "Reset";
    private static final String GO = "Go";
    private static final String NEW_GAME = ": a new game starts!";
    private int chunk=0;
    private final DrawNumberController controller;
    private Stage frame;
    private Label message;
    private final Bounds initialBounds;
    private final IntegerProperty max = new SimpleIntegerProperty();
    private final IntegerProperty min = new SimpleIntegerProperty();
    private final IntegerProperty last = new SimpleIntegerProperty();
    private final IntegerProperty remaining = new SimpleIntegerProperty();
    private final Property<DrawResult> lastResult = new SimpleObjectProperty<>();

    /**
     * Initializes a view implementation for a draw number game.
     *
     * @param controller the controller
     * @param initialBounds the initial bounds
     */
    public JFXDrawNumberView(final DrawNumberController controller, final Bounds initialBounds) {
        this.initialBounds = initialBounds;
        this.controller = controller;
        updateState();
    }

    @Override
    public void start() {
        frame = new Stage();
        frame.setTitle(FRAME_NAME);
        if (initialBounds != null) {
            frame.setX(initialBounds.getMinX());
            frame.setY(initialBounds.getMinY());
        }
        /*
        final VBox vbox = new VBox();
        final HBox playControlsLayout = new HBox();
        final TextField theNumber = new TextField();
        final Button go = new Button(GO);
        message = new Label();
        playControlsLayout.getChildren().addAll(theNumber, go, message);

        final HBox gameControlsLayout = new HBox();
        final Button bReset = new Button(RESET);
        final Button bQuit = new Button(QUIT);
        gameControlsLayout.getChildren().addAll(bReset, bQuit);*/
        

        GridPane grid = new GridPane();
        
        grid.setHgap(0);
        grid.setVgap(0);
        for (int r = 0; r <= 6; r+=2) {
            
            for (int c = 0; c <= 6; c+=2) {
                this.setChunk(); 
                for (int i=0; i < 2; i++){
                    for (int y=0; y < 2; y++){
                        Image image = randImage(c+y,r+i);
                        Button square = createButton((String.valueOf(r+i)+" , "+String.valueOf(c+y)), 30,image);
                
                
                        square.setMinSize(150, 150);

                        grid.add(square,c+y,r+i);}
                }
                
            }
        }
     

        final int sceneWidth = 1000;
        final int sceneHeight = 1000;
        final Scene scene = new Scene(grid, sceneWidth, sceneHeight);
        this.frame.setScene(scene);
        this.frame.show();
}



private Button createButton(String text, double size, Image image) {
    Button button = new Button(text);
    BackgroundImage backgroundImage = new BackgroundImage(
        image,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(
            100, 100,
            true, true,
            true, false
        )
    );
    button.setBackground(new Background(backgroundImage));

    return button;
}

private void setChunk(){
    this.chunk = (int) (Math.random() * 3) + 1;
}




    private Image randImage(int col,int row){
        Image image = new Image(
            getClass().getResource("/images/"+this.chunk+".png").toExternalForm()
        );
        return image;
    }


    @Override
    public void numberIncorrect() {
        displayError("Incorrect Number... try again");
    }

    @Override
    public void result(final DrawResult result) {
        last.setValue(result.attempted());
        lastResult.setValue(result);
        updateState();
        switch (result.drawResult()) {
            case YOURS_HIGH, YOURS_LOW:
                plainMessage(result.drawResult().getDescription());
                return;
            case YOU_WON, YOU_LOST:
                plainMessage(result.drawResult().getDescription() + NEW_GAME);
                break;
        }
        controller.resetGame();
        updateState();
    }

    @Override
    public void displayError(final String msg) {
        // MessageDialog.showMessageDialog(frame, "Error", msg);
        message.setText(msg);
    }

    private void plainMessage(final String msg) {
        // MessageDialog.showMessageDialog(frame, title, msg);
        message.setText(msg);
    }

    private void updateState() {
        max.set(controller.getMax());
        min.set(controller.getMin());
        remaining.set(controller.getAttemptsLeft());
    }
}
