package it.unibo.df.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unibo.df.gs.ArsenalState;
import it.unibo.df.gs.GameState;
import it.unibo.df.input.ArsenalInput;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Input;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityRegistry;
import it.unibo.df.model.abilities.combine.AbilityCombiner;
import it.unibo.df.model.abilities.combine.DefaultCombinations;

/**
 * arsenal state.
 */
public final class ArsenalController implements ControllerState {
	private final AbilityRegistry arsenal;
	private final List<Ability> loadout;
	private final AbilityCombiner combiner;
	private final ArsenalState state;

	public ArsenalController() {
		arsenal = new AbilityRegistry();
		loadout = new ArrayList<>();
		combiner = DefaultCombinations.create();
		// here i should add all the arsenal in the unlocked part... TODO.
		state = new ArsenalState(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
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
		state.equipped().add(arsenal.get(input.id()).asView());
		return true;
	}

    private boolean handleCombine(Combine combine) { // maybe use the utilities in ArsenalState?
        // TODO: better logic
		var result = combiner.combine(arsenal.get(combine.id1()), arsenal.get(combine.id2()));
		result.ifPresent(r -> {
			state.lost().addAll(
				List.of(
					arsenal.get(combine.id1()).asView(),
					arsenal.get(combine.id2()).asView()
				)
			);
			state.unlocked().add(r.asView());
		});
		return result.isPresent();
    }

	public List<Ability> currentLoadout() {
		return List.copyOf(loadout);
	}

	@Override
	public GameState tick() {
		var stateCopy = ArsenalState.copyOf(state);
		state.lost().clear();
		state.equipped().clear();
		state.unlocked().clear();
		return stateCopy;
	}
}
