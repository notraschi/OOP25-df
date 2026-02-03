package it.unibo.df.model.abilities;

import java.util.Set;
import java.util.Optional;
import java.util.HashSet;

/**
 * Represents areas affected by abilities.
 */
public final class AbilityAreas {
    private AbilityAreas() { }

    /**
     * No affected cells.
     *
     * @return function with no cells
     */
    public static AbilityFn none() {
        return p -> Optional.empty();
    }

    /**
     * Only caster cell.
     *
     * @return function with caster cell
     */
    public static AbilityFn self() {
            return p -> Optional.of(Set.of(p));
    }

    /**
     * Four adjacent cells (no diagonal).
     *
     * @return function with adjacent area
     */
    public static AbilityFn adjacent4() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() + 1, p.y()));
            area.add(new Vec2D(p.x() - 1, p.y()));
            area.add(new Vec2D(p.x(), p.y() + 1));
            area.add(new Vec2D(p.x(), p.y() - 1));
            return Optional.of(area);
        };
    }

    /**
     * Converts an area name to an area function.
     * 
     * @param areaName the name of the area
     * @return area function
     */
    public static AbilityFn fromString(final String areaName) {
        return switch (areaName) {
            case "NONE" -> none();
            case "SELF" -> self();
            case "ADJ4" -> adjacent4();
            default -> throw new IllegalArgumentException("Unknown area: " + areaName);
        };
    }
}
