package it.unibo.df.model.abilities;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import it.unibo.df.utility.Vec2D;

/**
 * Represents areas affected by abilities.
 */
public final class AbilityAreas {
    private static final int LINE_RANGE = 9;

    private AbilityAreas() {
    }

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
     * Arrow-shaped area to the left and right of the caster.
     *
     * @return arrow area
     */
    public static AbilityFn arrowRight() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            // right arrow
            area.add(new Vec2D(p.x() + 3, p.y()));
            area.add(new Vec2D(p.x() + 3, p.y() - 1));
            area.add(new Vec2D(p.x() + 3, p.y() + 1));
            area.add(new Vec2D(p.x() + 4, p.y()));
            // left arrow
            area.add(new Vec2D(p.x() - 3, p.y()));
            area.add(new Vec2D(p.x() - 3, p.y() - 1));
            area.add(new Vec2D(p.x() - 3, p.y() + 1));
            area.add(new Vec2D(p.x() - 4, p.y()));
            return Optional.of(area);
        };
    }

    /**
     * Wide fan-shaped area up to 2 tiles from the caster.
     *
     * @return wide arrow area
     */
    public static AbilityFn arrowWideUp() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int fwd = 0; fwd <= 2; fwd++) {
                final int maxLat = 2 - fwd;
                for (int lat = -maxLat; lat <= maxLat; lat++) {
                    if (fwd == 0 && lat == 0) {
                        continue;
                    }
                    // right fan
                    area.add(new Vec2D(p.x() + fwd, p.y() + lat));
                    // left fan
                    area.add(new Vec2D(p.x() - fwd, p.y() + lat));
                }
            }
            return Optional.of(area);
        };
    }

    /**
     * Diagonal cross area around the caster.
     *
     * @return diagonal cross area
     */
    public static AbilityFn diagX() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int i = 1; i <= 3; i++) {
                area.add(new Vec2D(p.x() + i, p.y() + i));
                area.add(new Vec2D(p.x() - i, p.y() + i));
                area.add(new Vec2D(p.x() + i, p.y() - i));
                area.add(new Vec2D(p.x() - i, p.y() - i));
            }
            return Optional.of(area);
        };
    }

    /**
     * Pattern 1 to the right/left of the caster.
     *
     * @return pattern area
     */
    public static AbilityFn p1Right() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            // right
            area.add(new Vec2D(p.x() + 1, p.y()));
            area.add(new Vec2D(p.x() + 1, p.y() - 1));
            area.add(new Vec2D(p.x() + 1, p.y() + 1));
            area.add(new Vec2D(p.x() + 2, p.y() - 2));
            area.add(new Vec2D(p.x() + 2, p.y() + 2));
            // left
            area.add(new Vec2D(p.x() - 1, p.y()));
            area.add(new Vec2D(p.x() - 1, p.y() - 1));
            area.add(new Vec2D(p.x() - 1, p.y() + 1));
            area.add(new Vec2D(p.x() - 2, p.y() - 2));
            area.add(new Vec2D(p.x() - 2, p.y() + 2));
            return Optional.of(area);
        };
    }

    /**
     * Pattern 2 to the left/right of the caster.
     *
     * @return pattern area
     */
    public static AbilityFn p2Left() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x(), p.y() - 2));
            area.add(new Vec2D(p.x() - 1, p.y() - 1));
            area.add(new Vec2D(p.x() + 1, p.y() - 1));
            area.add(new Vec2D(p.x() - 2, p.y()));
            area.add(new Vec2D(p.x() + 2, p.y()));
            area.add(new Vec2D(p.x() - 1, p.y() + 1));
            area.add(new Vec2D(p.x() + 1, p.y() + 1));
            area.add(new Vec2D(p.x(), p.y() + 2));
            return Optional.of(area);
        };
    }

    /**
     * Pattern 3 around the caster.
     *
     * @return pattern area
     */
    public static AbilityFn p3Up() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() - 1, p.y() - 1));
            area.add(new Vec2D(p.x() + 1, p.y() - 1));
            area.add(new Vec2D(p.x() - 1, p.y() + 1));
            area.add(new Vec2D(p.x() + 1, p.y() + 1));
            return Optional.of(area);
        };
    }

    /**
     * Pattern 4 below the caster.
     *
     * @return pattern area
     */
    public static AbilityFn p4Down() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() - 2, p.y() + 1));
            area.add(new Vec2D(p.x() - 2, p.y() + 2));
            area.add(new Vec2D(p.x() - 1, p.y()));
            area.add(new Vec2D(p.x() - 1, p.y() + 3));
            area.add(new Vec2D(p.x(), p.y() - 1));
            area.add(new Vec2D(p.x(), p.y() + 3));
            area.add(new Vec2D(p.x() + 1, p.y()));
            area.add(new Vec2D(p.x() + 1, p.y() + 3));
            area.add(new Vec2D(p.x() + 2, p.y() + 1));
            area.add(new Vec2D(p.x() + 2, p.y() + 2));
            return Optional.of(area);
        };
    }

    /**
     * Horizontal line area from the caster.
     *
     * @return line area
     */
    public static AbilityFn lineRight() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int i = 1; i <= LINE_RANGE; i++) {
                area.add(new Vec2D(p.x() + i, p.y()));
                area.add(new Vec2D(p.x() - i, p.y()));
            }
            return Optional.of(area);
        };
    }

    /**
     * Vertical line area from the caster.
     *
     * @return line area
     */
    public static AbilityFn lineVertical() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int i = 1; i <= LINE_RANGE; i++) {
                area.add(new Vec2D(p.x(), p.y() + i));
                area.add(new Vec2D(p.x(), p.y() - i));
            }
            return Optional.of(area);
        };
    }

    /**
     * Columns area in front of the caster.
     *
     * @return columns area
     */
    public static AbilityFn columns3Down() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int fwd = 1; fwd <= 2; fwd++) {
                for (int lat = -1; lat <= 1; lat++) {
                    area.add(new Vec2D(p.x() + fwd, p.y() + lat));
                    area.add(new Vec2D(p.x() - fwd, p.y() + lat));
                }
            }
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
            case "ARROW_R" -> arrowRight();
            case "ARROWWIDE_U" -> arrowWideUp();
            case "DIAGX" -> diagX();
            case "P1_R" -> p1Right();
            case "P2_L" -> p2Left();
            case "P3_U" -> p3Up();
            case "P4_D" -> p4Down();
            case "LINE_R" -> lineRight();
            case "LINE_V" -> lineVertical();
            case "COLUMNS3_D" -> columns3Down();
            default -> throw new IllegalArgumentException("Unknown area: " + areaName);
        };
    }
}
