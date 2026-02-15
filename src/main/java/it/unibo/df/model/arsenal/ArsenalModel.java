package it.unibo.df.model.arsenal;


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.model.abilities.Ability;

/**
 * Manages the player's arsenal, loadout, and ability combinations.
 */
public class ArsenalModel {
    private final Map<Integer, Ability> arsenal;
    private final List<Ability> loadout;
    private final AbilityCombiner combiner;

    /**
     * Creates a new arsenal model starting from unlocked abilities.
     *
     * @param unlocked the abilities currently unlocked
     */
    public ArsenalModel(final Map<Integer, Ability> unlocked) {
        arsenal = new HashMap<>(unlocked);
        loadout = new LinkedList<>();
        combiner = DefaultCombinations.create();
    }

    /**
     * Equips an ability by id if possible.
     *
     * @param id the ability id
     * @return true if equipped, false otherwise
     */
    public boolean equip(final int id) {
        if (arsenal.get(id) == null || loadout.size() > 2 || isEquipped(id)) {
            return false;
        }
        loadout.add(arsenal.get(id));
        return true;
    }

    private boolean isEquipped(final int id) {
        return loadout.stream().anyMatch(a -> a.id() == id);
    }

    /**
     * Unequips an ability by id if present in the loadout.
     *
     * @param id the ability id
     * @return true if unequipped, false otherwise
     */
    public boolean unequip(final int id) {
        if (arsenal.get(id) == null || !isEquipped(id)) {
            return false;
        }
        loadout.remove(arsenal.get(id));
        return true;
    }

    /**
     * Combines two abilities and replaces them with the resulting one.
     *
     * @param id1 first ability id
     * @param id2 second ability id
     * @return the combined ability view, if any
     */
    public Optional<AbilityView> combine(final int id1, final int id2) {
        if (arsenal.get(id1) == null || arsenal.get(id2) == null) {
            return Optional.empty();
        } else if (isEquipped(id1) || isEquipped(id2)) {
            return Optional.empty();
        }

        final var result = combiner.combine(arsenal.get(id1), arsenal.get(id2));
        result.ifPresent(res -> {
            arsenal.put(res.id(), res);
            arsenal.remove(id1);
            arsenal.remove(id2);
        });
        return result.map(Ability::asView);
    }

    /**
     * Returns the currently equipped abilities.
     *
     * @return the loadout
     */
    public List<Ability> getLoadout() {
        return List.copyOf(loadout);
    }

    /**
     * Returns all abilities in the arsenal.
     *
     * @return the arsenal
     */
    public List<Ability> getArsenal() {
        return Collections.unmodifiableList(arsenal.entrySet().stream().map(Entry::getValue).toList());
    }
}
