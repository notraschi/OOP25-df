package it.unibo.df.GUI;

import java.util.LinkedList;
import java.util.List;

import it.unibo.df.gs.AbilityView;
import it.unibo.df.gs.ArsenalState;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


public class AbilityMenu {
    private GridPane inventaryArea;
    private GridPane equipment;
    private Scene menu;

    public AbilityMenu(){
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
        ///area.add(fillMixerArea(), 2, 0);
        return area;
    }

    private GridPane fillInventaryArea(){
        inventaryArea = new GridPane();
        formatColumns(inventaryArea, 5, 20);
        formatRows(inventaryArea, 5, 20);
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Label lbl = new Label();
                lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                inventaryArea.add(lbl, i, j);
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
        area.add(fillBindingsArea(), 1, 0);
        return area;
    }

    private Label fillDescriptionArea(){
        Label lbl = new Label("This area is for descriptions");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    private Label fillBindingsArea(){
        Label lbl = new Label("This area is for Bindings");
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return lbl;
    }

    /**
	 * refresh the inventary page and write the equipment, unlocked an locked moves
	 * 
	 * @param an arsenal state 
	 */
    public void set(ArsenalState gs){
        int index =0;
        List<AbilityView> mosse = new LinkedList<>();
        mosse.addAll(gs.unlocked());
        mosse.addAll(gs.lost().reversed());
        for (var e : inventaryArea.getChildren()){
            if (e instanceof Label label){
                label.setText(mosse.size()<index?mosse.get(index).name():"null");
            }
        }
    }
    public void refresh(ArsenalState gs){
        int index = 0;
        for (var e : equipment.getChildren()){
            if (e instanceof Label content){
                content.setText("EQUIP "+String.valueOf(index)+":\n"+gs.equipped().get(index).name());
            }
            index++;
        }
        
    }
    /**
	 * 
	 * @return a scene of the inventory
	 */

    public Scene getScene(){
        return menu;
    }
}
