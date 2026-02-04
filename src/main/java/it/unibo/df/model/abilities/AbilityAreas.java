package it.unibo.df.model.abilities;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    public static AbilityFn arrowRight() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() + 3, p.y()));
            area.add(new Vec2D(p.x() + 3, p.y() - 1));
            area.add(new Vec2D(p.x() + 3, p.y() + 1));
            area.add(new Vec2D(p.x() + 4, p.y()));
            return Optional.of(area);
        };
    }

    public static AbilityFn arrowWideUp() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int fwd = 0; fwd <= 2; fwd++) {
                final int maxLat = 2 - fwd;
                for (int lat = -maxLat; lat <= maxLat; lat++) {
                    if (fwd == 0 && lat == 0) {
                        continue;
                    }
                    area.add(new Vec2D(p.x() + lat, p.y() - fwd));
                }
            }
            return Optional.of(area);
        };
    }

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

    public static AbilityFn p1Right() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() + 1, p.y()));
            area.add(new Vec2D(p.x() + 1, p.y() - 1));
            area.add(new Vec2D(p.x() + 1, p.y() + 1));
            area.add(new Vec2D(p.x() + 2, p.y() - 2));
            area.add(new Vec2D(p.x() + 2, p.y() + 2));
            return Optional.of(area);
        };
    }

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

    public static AbilityFn p3Up() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            area.add(new Vec2D(p.x() - 1, p.y() - 1));
            return Optional.of(area);
        };
    }

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

    public static AbilityFn lineRight() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int i = 1; i <= 9; i++) {
                area.add(new Vec2D(p.x() + i, p.y()));
            }
            return Optional.of(area);
        };
    }

    public static AbilityFn columns3Down() {
        return p -> {
            final Set<Vec2D> area = new HashSet<>();
            for (int fwd = 1; fwd <= 2; fwd++) {
                for (int lat = -1; lat <= 1; lat++) {
                    area.add(new Vec2D(p.x() + lat, p.y() + fwd));
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
            case "COLUMNS3_D" -> columns3Down();
            default -> throw new IllegalArgumentException("Unknown area: " + areaName);
        };
    }
}
