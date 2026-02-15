package it.unibo.df.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Create a larger container for a resizable object with lateral bands.
 */
public class SceneResizer {
    private final BorderPane borderPane = new BorderPane();
    private final StackPane externalWindowPane = new StackPane();
    private final GridPane contentPane = new GridPane();

    /**
     * Create a larger container for a resizable object with lateral bands. 
     * 
     * @param centerPane the main content
     * @param wMul to resize not necessary in a square
     * @param hMul to resize not necessary in a square
     */
    public SceneResizer(final GridPane centerPane, final double wMul, final double hMul) {
        PaneFormatter.formatColumns(contentPane, 1, 100);
        PaneFormatter.formatRows(contentPane, 1, 100);
        contentPane.add(centerPane, 0, 0);
        contentPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        externalWindowPane.getChildren().add(contentPane);
        borderPane.setCenter(externalWindowPane);
        final ChangeListener<Number> resizebility = (obs, oldValue, newValue) -> {
            final double size = Double.min(externalWindowPane.getWidth(), externalWindowPane.getHeight());
            contentPane.setPrefSize(size * wMul, size * hMul);
        };
        externalWindowPane.widthProperty().addListener(resizebility);
        externalWindowPane.heightProperty().addListener(resizebility);
    }

    /**
     * @return BorderPane, the external container
     */
    @SuppressFBWarnings(
        value = "EI", 
        justification = "the pane must necessarily be this one"
    )
    public BorderPane getBorderPane() {
        return borderPane;
    }
}
