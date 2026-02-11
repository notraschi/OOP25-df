package it.unibo.df.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class SceneResizer {
    final BorderPane borderPane = new BorderPane();
    final StackPane externalWindowPane = new StackPane();
    final GridPane contentPane = new GridPane();
    

    public SceneResizer(GridPane centerPane, double wMul, double hMul) {
        PaneFormatter.formatColumns(contentPane, 1, 100);
        PaneFormatter.formatRows(contentPane, 1, 100);
        contentPane.add(centerPane, 0, 0);
        contentPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        externalWindowPane.getChildren().add(contentPane);
        borderPane.setCenter(externalWindowPane);
        
        final ChangeListener<Number> resizebility = (obs, oldValue, newValue) -> {
            final double size = Double.min(externalWindowPane.getWidth(), externalWindowPane.getHeight());
            contentPane.setPrefSize(size*wMul , size*hMul);
        };
        externalWindowPane.widthProperty().addListener(resizebility);
        externalWindowPane.heightProperty().addListener(resizebility);
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    
}