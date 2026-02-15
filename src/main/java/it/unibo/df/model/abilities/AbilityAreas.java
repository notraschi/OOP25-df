package it.unibo.df.model.abilities;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x() + 1, p.y()),
            new Vec2D(p.x() - 1, p.y()),
            new Vec2D(p.x(), p.y() + 1),
            new Vec2D(p.x(), p.y() - 1)
        ).collect(Collectors.toSet()));
    }

    /**
     * Arrow-shaped area to the left and right of the caster.
     *
     * @return arrow area
     */
    public static AbilityFn arrow() {
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x() + 3, p.y()),
            new Vec2D(p.x() + 3, p.y() - 1),
            new Vec2D(p.x() + 3, p.y() + 1),
            new Vec2D(p.x() + 4, p.y()),
            new Vec2D(p.x() - 3, p.y()),
            new Vec2D(p.x() - 3, p.y() - 1),
            new Vec2D(p.x() - 3, p.y() + 1),
            new Vec2D(p.x() - 4, p.y())
        ).collect(Collectors.toSet()));
    }

    /**
     * Wide fan-shaped area up to 2 tiles from the caster.
     *
     * @return wide arrow area
     */
    public static AbilityFn arrowWide() {
        return p -> Optional.of(
            IntStream.rangeClosed(0, 2)
                .boxed()
                .flatMap(fwd -> {
                    final int maxLat = 2 - fwd;
                    return IntStream.rangeClosed(-maxLat, maxLat)
                        .filter(lat -> !(fwd == 0 && lat == 0))
                        .boxed()
                        .flatMap(lat -> Stream.of(
                            new Vec2D(p.x() + fwd, p.y() + lat),
                            new Vec2D(p.x() - fwd, p.y() + lat)
                        ));
                })
                .collect(Collectors.toSet())
        );
    }

    /**
     * Diagonal cross area around the caster.
     *
     * @return diagonal cross area
     */
    public static AbilityFn diagX() {
        return p -> Optional.of(
            IntStream.rangeClosed(1, 3)
                .boxed()
                .flatMap(i -> Stream.of(
                    new Vec2D(p.x() + i, p.y() + i),
                    new Vec2D(p.x() - i, p.y() + i),
                    new Vec2D(p.x() + i, p.y() - i),
                    new Vec2D(p.x() - i, p.y() - i)
                ))
                .collect(Collectors.toSet())
        );
    }

    /**
     * Pattern 1 to the right/left of the caster.
     * 
     * @return pattern area
     */
    public static AbilityFn pattern1() {
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x() + 1, p.y()),
            new Vec2D(p.x() + 1, p.y() - 1),
            new Vec2D(p.x() + 1, p.y() + 1),
            new Vec2D(p.x() + 2, p.y() - 2),
            new Vec2D(p.x() + 2, p.y() + 2),
            new Vec2D(p.x() - 1, p.y()),
            new Vec2D(p.x() - 1, p.y() - 1),
            new Vec2D(p.x() - 1, p.y() + 1),
            new Vec2D(p.x() - 2, p.y() - 2),
            new Vec2D(p.x() - 2, p.y() + 2)
        ).collect(Collectors.toSet()));
    }

    /**
     * Pattern 2 to the left/right of the caster.
     *
     * @return pattern area
     */
    public static AbilityFn pattern2() {
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x(), p.y() - 2),
            new Vec2D(p.x() - 1, p.y() - 1),
            new Vec2D(p.x() + 1, p.y() - 1),
            new Vec2D(p.x() - 2, p.y()),
            new Vec2D(p.x() + 2, p.y()),
            new Vec2D(p.x() - 1, p.y() + 1),
            new Vec2D(p.x() + 1, p.y() + 1),
            new Vec2D(p.x(), p.y() + 2)
        ).collect(Collectors.toSet()));
    }

    /**
     * Pattern 3 around the caster.
     *
     * @return pattern area
     */
    public static AbilityFn pattern3() {
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x() - 1, p.y() - 1),
            new Vec2D(p.x() + 1, p.y() - 1),
            new Vec2D(p.x() - 1, p.y() + 1),
            new Vec2D(p.x() + 1, p.y() + 1)
        ).collect(Collectors.toSet()));
    }

    /**
     * Pattern 4 below the caster.
     *
     * @return pattern area
     */
    public static AbilityFn pattern4() {
        return p -> Optional.of(Stream.of(
            new Vec2D(p.x() - 2, p.y() + 1),
            new Vec2D(p.x() - 2, p.y() + 2),
            new Vec2D(p.x() - 1, p.y()),
            new Vec2D(p.x() - 1, p.y() + 3),
            new Vec2D(p.x(), p.y() - 1),
            new Vec2D(p.x(), p.y() + 3),
            new Vec2D(p.x() + 1, p.y()),
            new Vec2D(p.x() + 1, p.y() + 3),
            new Vec2D(p.x() + 2, p.y() + 1),
            new Vec2D(p.x() + 2, p.y() + 2)
        ).collect(Collectors.toSet()));
    }

    /**
     * Horizontal line area from the caster.
     *
     * @return line area
     */
    public static AbilityFn lineHorizontal() {
        return p -> Optional.of(
            IntStream.rangeClosed(1, LINE_RANGE)
                .boxed()
                .flatMap(i -> Stream.of(
                    new Vec2D(p.x() + i, p.y()),
                    new Vec2D(p.x() - i, p.y())
                ))
                .collect(Collectors.toSet())
        );
    }

    /**
     * Vertical line area from the caster.
     *
     * @return line area
     */
    public static AbilityFn lineVertical() {
        return p -> Optional.of(
            IntStream.rangeClosed(1, LINE_RANGE)
                .boxed()
                .flatMap(i -> Stream.of(
                    new Vec2D(p.x(), p.y() + i),
                    new Vec2D(p.x(), p.y() - i)
                ))
                .collect(Collectors.toSet())
        );
    }

    /**
     * Columns area in front of the caster.
     *
     * @return columns area
     */
    public static AbilityFn columns3() {
        return p -> Optional.of(
            IntStream.rangeClosed(1, 2)
                .boxed()
                .flatMap(fwd -> IntStream.rangeClosed(-1, 1)
                    .boxed()
                    .flatMap(lat -> Stream.of(
                        new Vec2D(p.x() + fwd, p.y() + lat),
                        new Vec2D(p.x() - fwd, p.y() + lat)
                    )))
                .collect(Collectors.toSet())
        );
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
            case "ARROW" -> arrow();
            case "ARROWWIDE" -> arrowWide();
            case "DIAGX" -> diagX();
            case "P1" -> pattern1();
            case "P2" -> pattern2();
            case "P3" -> pattern3();
            case "P4" -> pattern4();
            case "LINE_H" -> lineHorizontal();
            case "LINE_V" -> lineVertical();
            case "COLUMNS3" -> columns3();
            default -> throw new IllegalArgumentException("Unknown area: " + areaName);
        };
    }
}
