package it.unibo.df.view;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.ArsenalState;
import static it.unibo.df.view.PaneFormatter.formatColumns;
import static it.unibo.df.view.PaneFormatter.formatRows;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

/**
 */
public class AbilityMenu {
    private final int MAX_SIZE_PERC = 100;
    private final int INVENTORY_WIDTH_PERC = 80;
    private final int MIXER_ABILITY_SIZE = 2;
    private final int INVENTORY_WIDTH = 5;
    private final int INVENTORY_HEIGHT = 8;
    private final int KEYS_AREA_ROWS = 2;
    private final int  loadoutSize;
    private GridPane inventaryArea;
    private GridPane equipment;
    private GridPane combineArea;
    private final List<String> keys;
    private final Map<Integer, AbilityView> unlocked = new LinkedHashMap<>();
    private final List<Integer> lost = new LinkedList<>();
    private final List<Integer> equipped = new LinkedList<>();
    private final List<Integer> combiner = new LinkedList<>();
    private Label descriptionLabel;
    private final ToggleGroup group = new ToggleGroup();
    private Scene menu;

    /**
     * @param keys
     */
    public AbilityMenu(final List<String> keys, int loadoutSize) {
        this.loadoutSize = loadoutSize;
        this.keys = List.copyOf(keys);
        setupAbilityMenuScene();
    }

    private void setupAbilityMenuScene() {
        final GridPane menuArea = new GridPane();
        formatColumns(menuArea, 1, MAX_SIZE_PERC);
        formatRows(menuArea, 1, INVENTORY_WIDTH_PERC);
        formatRows(menuArea, 1, MAX_SIZE_PERC - INVENTORY_WIDTH_PERC);
        menuArea.add(fillUpperArea(), 0, 0);
        menuArea.add(fillLowerArea(), 0, 1);
        SceneResizer resizer = new SceneResizer(menuArea, Double.valueOf(INVENTORY_WIDTH_PERC) / Double.valueOf(MAX_SIZE_PERC), MAX_SIZE_PERC / MAX_SIZE_PERC);
        menu = new Scene(resizer.getBorderPane());
        menu.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
    }

    private GridPane fillUpperArea() {
        final GridPane area = new GridPane();
        formatColumns(area, 1, MAX_SIZE_PERC / 2);
        formatColumns(area, 2, MAX_SIZE_PERC / 4);
        formatRows(area, 1, MAX_SIZE_PERC);
        area.add(fillInventaryArea(), 0, 0);
        area.add(fillEquipmentArea(), 1, 0);
        area.add(fillMixerArea(), 2, 0);
        return area;
    }

    private GridPane fillMixerArea() {
        combineArea = new GridPane();
        formatColumns(combineArea, 1, MAX_SIZE_PERC);
        formatRows(combineArea, MIXER_ABILITY_SIZE, MAX_SIZE_PERC / MIXER_ABILITY_SIZE);
        for (int i = 0; i < MIXER_ABILITY_SIZE; i++){
            final Label lbl = new Label("");
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            combineArea.add(lbl, 0, i);
        }
        return combineArea;

    }

    private GridPane fillInventaryArea() {
        inventaryArea = new GridPane();
        formatColumns(inventaryArea, INVENTORY_WIDTH, MAX_SIZE_PERC / INVENTORY_WIDTH);
        formatRows(inventaryArea, INVENTORY_HEIGHT, MAX_SIZE_PERC / INVENTORY_HEIGHT);
        for (int i = 0; i < INVENTORY_WIDTH; i++) {
            for (int j = 0; j < INVENTORY_HEIGHT; j++) {
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
        formatColumns(equipment, 1, MAX_SIZE_PERC);
        formatRows(equipment, loadoutSize, MAX_SIZE_PERC / loadoutSize);
        for (int j = 0; j < loadoutSize; j++) {
            final Label lbl = new Label();
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            equipment.add(lbl, 0, j);
        }
        return equipment;
    }

    private GridPane fillLowerArea() {
        final GridPane area = new GridPane();
        formatColumns(area, 2, MAX_SIZE_PERC / 2);
        formatRows(area, 1, MAX_SIZE_PERC);
        area.add(fillDescriptionArea(), 0, 0);
        area.add(fillKeysArea(), 1, 0);
        return area;
    }

    private Label fillDescriptionArea() {
        descriptionLabel = new Label("");
        descriptionLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return descriptionLabel;
    }

    private GridPane fillKeysArea() {
        final GridPane area = new GridPane();
        final Iterator<String> key = keys.iterator();
        formatColumns(area, keys.size() / KEYS_AREA_ROWS, MAX_SIZE_PERC / (keys.size() / KEYS_AREA_ROWS));
        formatRows(area, KEYS_AREA_ROWS, MAX_SIZE_PERC / KEYS_AREA_ROWS);
        for (int i = 0; i < KEYS_AREA_ROWS; i++) {
            for (int j = 0; j < keys.size() / KEYS_AREA_ROWS; j++) {
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
        if (combiner.size() >= MIXER_ABILITY_SIZE) {
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

    public void refreshDescription(int id) {
        final AbilityView ab = unlocked.get(id);
        descriptionLabel.setText(
            "NAME: " + ab.name() 
            + "\nHEAL: " + ab.casterHpDelta() 
            + "\nDAMAGE: " + ab.targetHpDelta()
        );
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
