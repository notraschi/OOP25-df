package it.unibo.df.GUI;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.ArsenalState;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 */
public class AbilityMenu {
    private GridPane inventaryArea;
    private GridPane equipment;
    private GridPane combineArea;
    private List<String> keys;
    private final Map<Integer, AbilityView> unlocked = new LinkedHashMap<>();
    private final List<Integer> lost = new LinkedList<>();
    private final List<Integer> equipped = new LinkedList<>();
    private final List<Integer> combiner = new LinkedList<>();
    private final ToggleGroup group = new ToggleGroup();
    private Scene menu;

    /**
     * @param keys
     */
    public AbilityMenu(final List<String> keys) {
        this.keys = List.copyOf(keys);
        setupAbilityMenuScene();
    }

    private void setupAbilityMenuScene() {
        final GridPane menuArea = new GridPane();
        formatColumns(menuArea, 1, 100);
        formatRows(menuArea, 1, 80);
        formatRows(menuArea, 1, 20);
        menuArea.add(fillUpperArea(), 0, 0);
        menuArea.add(fillLowerArea(), 0, 1);
        menu = new Scene(menuArea);
        menu.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
    }

    private void formatColumns(final GridPane grid, final int size, final double perc) {
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(perc);
        for (int i = 0; i < size; i++) {
            grid.getColumnConstraints().add(cc);
        }
    }

    private void formatRows(final GridPane grid, final int size, final double perc) {
        final RowConstraints cr = new RowConstraints();
        cr.setPercentHeight(perc);
        for (int i = 0; i < size; i++) {
            grid.getRowConstraints().add(cr);
        }
    }

    private GridPane fillUpperArea() {
        final GridPane area = new GridPane();
        formatColumns(area, 1, 50);
        formatColumns(area, 2, 25);
        formatRows(area, 1, 100);
        area.add(fillInventaryArea(), 0, 0);
        area.add(fillEquipmentArea(), 1, 0);
        area.add(fillMixerArea(), 2, 0);
        return area;
    }

    private GridPane fillMixerArea() {
        combineArea = new GridPane();
        formatColumns(combineArea, 1, 100);
        formatRows(combineArea, 2, 50);
        for (int i = 0; i < 2; i++){
            final Label lbl = new Label("");
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            combineArea.add(lbl, 0, i);
        }
        return combineArea;

    }

    private GridPane fillInventaryArea() {
        inventaryArea = new GridPane();
        formatColumns(inventaryArea, 5, 20);
        formatRows(inventaryArea, 5, 20);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final ToggleButton btn = new ToggleButton();
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                inventaryArea.add(btn, i, j);
                btn.setToggleGroup(group);
            }
        }
        return inventaryArea; 
    }

    private GridPane fillEquipmentArea() {
        equipment = new GridPane();
        formatColumns(equipment, 1, 100);
        formatRows(equipment, 3, 33);
        for (int j = 0; j < 3; j++) {
            final Label lbl = new Label();
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            equipment.add(lbl, 0, j);
        }
        return equipment;
    }

    private GridPane fillLowerArea() {
        final GridPane area = new GridPane();
        formatColumns(area, 2, 50);
        formatRows(area, 1, 100);
        area.add(fillDescriptionArea(), 0, 0);
        area.add(fillKeysArea(), 1, 0);
        return area;
    }

    private Label fillDescriptionArea() {
        final Label lbl = new Label("This area is for descriptions");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    private GridPane fillKeysArea() {
        final GridPane area = new GridPane();
        final Iterator<String> key = keys.iterator();
        formatColumns(area, 3, 33);
        formatRows(area, 2, 50);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                final Label lbl = new Label(key.hasNext() ? key.next() : "");
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                area.add(lbl, j, i);
            }
        }
        return area;
    }

    /**
	 * refresh the inventary page and write the equipment, unlocked an locked moves.
	 * 
     * @param gs 
	 */
    public void set(final ArsenalState gs) {
        for (final var e : gs.unlocked()) {
            unlocked.put(e.id(), e);
        }
        lost.addAll(gs.lost());
        equipped.addAll(gs.equipped());      
    }

    /**
	 * refresh the inventary page and write the equipment, unlocked an locked moves.
     * 
     * @param gs
     */
    public void refresh(final ArsenalState gs) {
        set(gs);
        final Iterator<AbilityView> unlockIt = unlocked.entrySet().stream()
            .map(e -> e.getValue())
            .iterator();
        final Iterator<Integer> lostIt = lost.iterator();
        final Iterator<Integer> equipIt = equipped.iterator();
        for (final var e : inventaryArea.getChildren()) {
            if (e instanceof ToggleButton button) {
                button.setText(
                    unlockIt.hasNext() 
                    ? unlockIt.next().name() : lostIt.hasNext() 
                    ? unlocked.get(lostIt.next()).name() : ""
                );
            }
        }
        for (final var e : equipment.getChildren()) {
            if (e instanceof Label label) {
                label.setText(equipIt.hasNext() ? unlocked.get(equipIt.next()).name() : "");
            }
        }
    }

    /**
     */
    public void cleanEquipped() {
        equipped.clear();
    }

    /**
     * @param name
     * @return key or 0
     */
    public int getId(final String name) {
        for (final var e : unlocked.entrySet()) {
            if (e.getValue().name().equals(name)) {
                return e.getKey();
            }
        }
        return 0;
    }

    /**
     * @param name
     */
    public void addAbilityToCombine(final String name) {
        if (combiner.size() >= 2) {
            combiner.removeFirst();
        }
        for (final var e : unlocked.entrySet()) {
            if (e.getValue().name().equals(name)) {
                combiner.add(e.getKey());
            }
        }
    }

    /**
     */
    public void refreshCombine() {
        final Iterator<Integer> combineIt = combiner.iterator();
        for (final var e : combineArea.getChildren()) {
            if (e instanceof Label lbl) {
                lbl.setText(combineIt.hasNext() ? unlocked.get(combineIt.next()).name() : "");
            }
        }
    }

    /**
     * @return group
     */
    public ToggleGroup getGroup() {
        return group;
    }

    /**
     * @return equipment 
     */
    public List<AbilityView> getEquipped() {
        return equipped.stream().map(e -> unlocked.get(e)).toList();
    }

    /**
     * @return Scene
     */
    public Scene getScene() {
        return menu;
    }
}
