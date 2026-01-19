package it.unibo.df.controller;

import java.util.ArrayList;
import java.util.List;

import it.unibo.df.gs.GameState;
import it.unibo.df.input.ArsenalInput;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityRegistry;

/**
 * arsenal state.
 */
public final class ArsenalController implements ControllerState {
	// private final ArsenalState gameState = new ArsenalState();
	private final AbilityRegistry arsenal;
	private final List<Ability> loadout;

	public ArsenalController() {
		arsenal = new AbilityRegistry();
		loadout = new ArrayList<>();
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

	private boolean handleEquip(Equip input) { // TODO: fix this lone return true
		loadout.add(arsenal.get(input.id()));
		return true;
	}

    private boolean handleCombine(Combine combine) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public List<Ability> currentLoadout() {
		return List.copyOf(loadout);
	}

	@Override
	public GameState tick() {
		return null;
	}
}
