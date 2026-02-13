package it.unibo.df.view;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * Used to create Constraints for GridPanes.
 */
public final class PaneFormatter {

    private PaneFormatter() {
        throw new UnsupportedOperationException("Cannot inizialize this class");
    }

    /**
     * Add constraints to a GridPane.
     * 
     * @param grid the GridPane of content
     * @param size number of Columns for the pane
     * @param perc percentage size of the pane
     */
    public static void formatColumns(final GridPane grid, final int size, final double perc) {
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(perc);
        for (int i = 0; i < size; i++) {
            grid.getColumnConstraints().add(cc);
        }
    }

    /**
     * Add constraints to a GridPane.
     * 
     * @param grid the GridPane of content
     * @param size number of Rows for the pane
     * @param perc percentage size of the pane
     */
    public static void formatRows(final GridPane grid, final int size, final double perc) {
        final RowConstraints cr = new RowConstraints();
        cr.setPercentHeight(perc);
        for (int i = 0; i < size; i++) {
            grid.getRowConstraints().add(cr);
        }
    }
}
