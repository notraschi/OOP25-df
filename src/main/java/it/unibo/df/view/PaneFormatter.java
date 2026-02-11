package it.unibo.df.view;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class PaneFormatter {

    public static void formatColumns(final GridPane grid, final int size, final double perc) {
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(perc);
        for (int i = 0; i < size; i++) {
            grid.getColumnConstraints().add(cc);
        }
    }

    public static void formatRows(final GridPane grid, final int size, final double perc) {
        final RowConstraints cr = new RowConstraints();
        cr.setPercentHeight(perc);
        for (int i = 0; i < size; i++) {
            grid.getRowConstraints().add(cr);
        }
    }
}