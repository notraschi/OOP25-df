package it.unibo.df.model.arsenal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.model.abilities.Ability;

public class ArsenalModel {
    private final Map<Integer, Ability> arsenal;
	private final List<Ability> loadout;
	private final AbilityCombiner combiner;

    public ArsenalModel(Map<Integer, Ability> unlocked) {
        arsenal = new HashMap<>(unlocked);
		loadout = new LinkedList<>();
		combiner = DefaultCombinations.create();
    }

    public boolean equip(int id) {
        if (arsenal.get(id) == null || loadout.size() > 2 || isEquipped(id)) {
            return false;
        }
        loadout.add(arsenal.get(id));
		return true;
    }

    private boolean isEquipped(int id) {
        return loadout.stream().anyMatch(a -> a.id() == id);
    }

    public boolean unequip(int id) {
        if (arsenal.get(id) == null || !isEquipped(id)) {
            return false;
        }
        loadout.remove(arsenal.get(id));
        return true;
    }

    public Optional<AbilityView> combine(int id1, int id2) {
        if (arsenal.get(id1) == null || arsenal.get(id2) == null) {
            return Optional.empty();
        } else if (isEquipped(id1) || isEquipped(id2)) {
            return Optional.empty();
        }

		var result = combiner.combine(arsenal.get(id1), arsenal.get(id2));
        result.ifPresent(res -> {
            arsenal.put(res.id(), res);
            arsenal.remove(id1);
            arsenal.remove(id2);
        });
		return result.map(Ability::asView);
    }

    public List<Ability> getLoadout() {
        return List.copyOf(loadout);
    }

    public List<Ability> getArsenal() {
        return Collections.unmodifiableList(arsenal.entrySet().stream().map(e -> e.getValue()).toList());
    }
}