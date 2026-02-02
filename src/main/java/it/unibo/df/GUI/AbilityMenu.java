package it.unibo.df.GUI;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.ArsenalState;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


public class AbilityMenu {
    private GridPane inventaryArea;
    private GridPane equipment;
    private GridPane combineArea;
    private List<String> keys;
    private List<AbilityView> unlocked = new LinkedList<>();
    private List<AbilityView> lost = new LinkedList<>();
    private List<AbilityView> equipped = new LinkedList<>();
    private List<AbilityView> combiner = new LinkedList<>();
    private ToggleGroup group = new ToggleGroup();
    private Scene menu;

    public AbilityMenu(List<String> keys){
        this.keys = List.copyOf(keys);
        setupAbilityMenuScene();
    }

    private void  setupAbilityMenuScene(){
        GridPane menuArea = new GridPane();
        formatColumns(menuArea, 1, 100);
        formatRows(menuArea, 1, 80);
        formatRows(menuArea, 1, 20);
        menuArea.add(fillUpperArea(), 0, 0);
        menuArea.add(fillLowerArea(), 0, 1);

        menu = new Scene(menuArea);
        menu.getStylesheets().add(getClass().getResource("/css/boardStyle.css").toExternalForm());
    }

    private void formatColumns(GridPane grid, int size, double perc){
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(perc);
        for (int i = 0; i<size; i++){
            grid.getColumnConstraints().add(cc);
        }
    }

    private void formatRows(GridPane grid, int size, double perc){
        RowConstraints cr = new RowConstraints();
        cr.setPercentHeight(perc);
        for (int i = 0; i<size; i++){
            grid.getRowConstraints().add(cr);
        }
    }

    private GridPane fillUpperArea(){
        GridPane area = new GridPane();
        formatColumns(area, 1, 50);
        formatColumns(area, 2, 25);
        formatRows(area, 1, 100);
        area.add(fillInventaryArea(), 0, 0);
        area.add(fillEquipmentArea(), 1, 0);
        area.add(fillMixerArea(), 2, 0);
        return area;
    }
    
    private GridPane fillMixerArea(){
        combineArea = new GridPane();
        formatColumns(combineArea, 1, 100);
        formatRows(combineArea, 2, 50);
        for (int i = 0; i < 2; i++){
            Label lbl = new Label("");
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            combineArea.add(lbl, 0, i);
        }
        return combineArea;

    }
    
    private GridPane fillInventaryArea(){
        inventaryArea = new GridPane();
        formatColumns(inventaryArea, 5, 20);
        formatRows(inventaryArea, 5, 20);
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                ToggleButton btn = new ToggleButton();
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                inventaryArea.add(btn, i, j);
                btn.setToggleGroup(group);
            }
        }
        return inventaryArea; 
    }

    private GridPane fillEquipmentArea(){
        equipment = new GridPane();
        formatColumns(equipment, 1, 100);
        formatRows(equipment, 3, 33);
        for (int j = 0; j < 3; j++){
            Label lbl = new Label();
            lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            equipment.add(lbl, 0, j);
        }
        return equipment;
    }

    private GridPane fillLowerArea(){
        GridPane area = new GridPane();
        formatColumns(area, 2, 50);
        formatRows(area, 1, 100);
        area.add(fillDescriptionArea(), 0, 0);
        area.add(fillKeysArea(), 1, 0);
        return area;
    }

    private Label fillDescriptionArea(){
        Label lbl = new Label("This area is for descriptions");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    private GridPane fillKeysArea(){
        GridPane area = new GridPane();
        Iterator<String> key = keys.iterator();
        formatColumns(area, 3, 33);
        formatRows(area, 2, 50);
        
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 3;j++){
                Label lbl = new Label(key.hasNext()?key.next():"");
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                area.add(lbl, j, i);
            }
        }
        
        return area;
    }

    /**
	 * refresh the inventary page and write the equipment, unlocked an locked moves
	 * 
	 * @param an arsenal state 
	 */
    public void set(ArsenalState gs){
        unlocked.addAll(gs.unlocked());
        lost.addAll(gs.lost());
        unlocked.removeAll(lost);
        equipped.addAll(gs.equipped());        
    }

    public void refresh(ArsenalState gs){
        set(gs);
        Iterator<AbilityView> ability1 = unlocked.iterator();
        Iterator<AbilityView> ability2 = lost.iterator();
        Iterator<AbilityView> ability3 = equipped.iterator();
        for (var e : inventaryArea.getChildren()){
            if (e instanceof ToggleButton button){
                button.setText(ability1.hasNext()?ability1.next().name():ability2.hasNext()?"lost"+ability2.next().name():"");
            }
        }
        for (var e : equipment.getChildren()){
            if (e instanceof Label label){
                label.setText(ability3.hasNext()?ability3.next().name():"");
            }
        }
        

    }

    public int getId(String name){
        for(var e : unlocked){
            if (e.name().equals(name)){
                return e.id();
            }
        }
        return 0;
    }

    public void addAbilityToCombine(String name){
        if (combiner.size()>=2){
            combiner.removeFirst();
        }
        for (var e : unlocked){
            if (e.name().equals(name)){
                combiner.add(e);
            }
        }
        

    }

    public void refreshCombine(){
        Iterator<AbilityView> ability = combiner.iterator();
        for (var e : combineArea.getChildren()){
            if (e instanceof Label lbl){
                lbl.setText(ability.hasNext()?ability.next().name():"");
            }
        }
    }

   public ToggleGroup getGroup(){
        return group;
   }

    /**
	 * 
	 * @return a scene of the inventory
	 */

    public Scene getScene(){
        return menu;
    }
}
