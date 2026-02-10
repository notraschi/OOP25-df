package it.unibo.df.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unibo.df.gs.ArsenalState;
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
	private final ArsenalState state;

	public ArsenalController(Map<Integer, Ability> arsenal) {
		model = new ArsenalModel(arsenal);
		state = new ArsenalState(
			arsenal.entrySet().stream().map(e -> e.getValue().asView()).collect(Collectors.toList()),
			new LinkedList<>(),
			new LinkedList<>()
		);
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
		var result = model.equip(input.id());
		if (result) state.equipped().add(input.id());
		return result;
	}

    private boolean handleCombine(Combine input) {
        var result = model.combine(input.id1(), input.id2());
		result.ifPresent(unlocked -> {
			state.lost().add(input.id1());
			state.lost().add(input.id2());
			state.unlocked().add(unlocked);
		});
		return result.isPresent();
    }

	public List<Ability> currentLoadout() {
		return model.getLoadout();
	}

	@Override
	public GameState tick(long deltaTime) {
		var tmp = ArsenalState.copyOf(state);
		state.clear();
		return tmp;
	}
}
