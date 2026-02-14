package it.unibo.df.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unibo.df.dto.AbilityView;
import it.unibo.df.gs.ArsenalState;
import it.unibo.df.input.ArsenalInput;
import it.unibo.df.input.Combine;
import it.unibo.df.input.Equip;
import it.unibo.df.input.Input;
import it.unibo.df.input.Unequip;
import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.arsenal.ArsenalModel;

/**
 * arsenal state.
 */
public final class ArsenalController implements ControllerState {

    private final ArsenalModel model;
    private ArsenalStateBuilder builder;

    public ArsenalController(final Map<Integer, Ability> arsenal) {
        model = new ArsenalModel(arsenal);
        builder = new ArsenalStateBuilder();
        arsenal.values().stream().map(Ability::asView).forEach(builder::addUnlock);
    }

    @Override
    public boolean handle(final Input input) {
        return switch (input) {
            case ArsenalInput in -> 
                switch (in) {
                    case Equip equip -> handleEquip(equip);
                    case Unequip unequip -> handleUnequip(unequip);
                    case Combine combine -> handleCombine(combine);
                };
            default -> false;
        };
    }

    private boolean handleEquip(final Equip input) {
        final var result = model.equip(input.id());
        if (result) {
            builder.setEquip(input.id());
        }
        return result;
    }

    private boolean handleUnequip(final Unequip input) {
        final var result = model.unequip(input.id());
        if (result) {
            builder.setUnequip(input.id());
        }
        return result;
    }

    private boolean handleCombine(final Combine input) {
        final var result = model.combine(input.id1(), input.id2());
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
    public ArsenalState tick(final long deltaTime) {
        final var state = builder.build();
        builder = new ArsenalStateBuilder();
        return state;
    }

    private static final class ArsenalStateBuilder {
        private final List<AbilityView> unlock = new LinkedList<>();
        private final List<Integer> lost = new LinkedList<>();
        private Optional<Integer> equip = Optional.empty();
        private Optional<Integer> unequip = Optional.empty();

        ArsenalStateBuilder addUnlock(final AbilityView a) {
            unlock.add(a);
            return this;
        }

        ArsenalStateBuilder addLost(final int id) {
            lost.add(id);
            return this;
        }

        void setEquip(final int id) {
            equip = Optional.of(id);
        }

        void setUnequip(final int id) {
            unequip = Optional.of(id);
        }

        ArsenalState build() {
            return new ArsenalState(List.copyOf(unlock), List.copyOf(lost), equip, unequip);
        }
    }
}
