package it.unibo.df.controller;

import java.util.List;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.ArsenalInput;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.arsenal.ArsenalModel;

/**
 * arsenal state.
 */
public final class ArsenalController implements ControllerState {
	private final ArsenalModel model;

	public ArsenalController() {
		model = new ArsenalModel();
	}

	@Override
	public boolean handle(Input input) {
		return switch (input) {
			case ArsenalInput in -> 
				switch (in) {
					case Equip equip -> handleEquip(equip);
					case Combine combine -> handleCombine(combine);
				};
			default -> false;
		};
	}

	private boolean handleEquip(Equip input) {
		return model.equip(input.id());
	}

    private boolean handleCombine(Combine input) {
        return model.combine(input.id1(), input.id2());
    }

	public List<Ability> currentLoadout() {
		return model.getLoadout();
	}

	@Override
	public GameState tick() {
		return model.getState();
	}
}
