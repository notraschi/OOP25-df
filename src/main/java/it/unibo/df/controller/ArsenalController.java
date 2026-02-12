package it.unibo.df.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unibo.df.dto.AbilityView;
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
	private ArsenalStateBuilder builder;

	public ArsenalController(Map<Integer, Ability> arsenal) {
		model = new ArsenalModel(arsenal);
		builder = new ArsenalStateBuilder();
		arsenal.values().stream().map(Ability::asView).forEach(builder::addUnlock);
	}

	@Override
	public boolean handle(Input input) {
		return switch (input) {
			case ArsenalInput in -> 
				switch (in) {
					case Equip equip -> handleEquip(equip);
					// case Unequip unequip -> handleUnequip(unequip)
					case Combine combine -> handleCombine(combine);
				};
			default -> false;
		};
	}

	private boolean handleEquip(Equip input) {
		var result = model.equip(input.id());
		if (result) {
			builder.setEquip(input.id());
		}
		return result;
	}

	private boolean handleUnequip(/*Unequip input*/) {
		return false;
	}

    private boolean handleCombine(Combine input) {
        var result = model.combine(input.id1(), input.id2());
		result.ifPresent(unlocked -> {
			builder.addUnlock(unlocked)
				.addLost(input.id1())
				.addLost(input.id2());
		});
		return result.isPresent();
    }

	public List<Ability> currentLoadout() {
		return model.getLoadout();
	}

	@Override
	public GameState tick(long deltaTime) {
		var state = builder.build();
		builder = new ArsenalStateBuilder();
		return state;
	}

	private static class ArsenalStateBuilder {
		List<AbilityView> unlock = new LinkedList<>();
		List<Integer> lost = new LinkedList<>();
		Optional<Integer> equip = Optional.empty();
		Optional<Integer> unequip = Optional.empty();

		ArsenalStateBuilder addUnlock(AbilityView a) {
			unlock.add(a);
			return this;
		}

		ArsenalStateBuilder addLost(int id) {
			lost.add(id);
			return this;
		}

		void setEquip(int id) {
			equip = Optional.of(id);
		}

		void setUnequip(int id) {
			unequip = Optional.of(id);
		}

		ArsenalState build() {
			return new ArsenalState(List.copyOf(unlock), List.copyOf(lost), equip, unequip);
		}
	}
}
