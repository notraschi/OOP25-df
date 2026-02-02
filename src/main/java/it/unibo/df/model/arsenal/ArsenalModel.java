package it.unibo.df.model.arsenal;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.df.gs.ArsenalState;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityRegistry;

public class ArsenalModel {
    private final AbilityRegistry arsenal;
	private final List<Ability> loadout;
	private final AbilityCombiner combiner;
	private final ArsenalState state;

    public ArsenalModel() {
        arsenal = new AbilityRegistry();
		loadout = new LinkedList<>();
		combiner = DefaultCombinations.create();
		state = new ArsenalState(
            arsenal.getAll().stream().map(Ability::asView).collect(Collectors.toList()),
            new LinkedList<>(),
            new LinkedList<>()
        );
    }

    public boolean equip(int id) {
        if (arsenal.get(id) == null) return false;

        loadout.add(arsenal.get(id));
		state.equipped().add(arsenal.get(id).asView());
		return true;
    }

    public boolean combine(int id1, int id2) {
        if (arsenal.get(id1) == null || arsenal.get(id2) == null) return false;

		var result = combiner.combine(arsenal.get(id1), arsenal.get(id2));
		result.ifPresent(res -> {
			state.lost().add(arsenal.get(id1).asView());
			state.lost().add(arsenal.get(id2).asView());
			state.unlocked().add(res.asView());
		});
		return result.isPresent();
    }

    public List<Ability> getLoadout() {
        return List.copyOf(loadout);
    }

    public ArsenalState getState() {
        var tmp = ArsenalState.copyOf(state);
        state.clear();
        return tmp;
    }
}