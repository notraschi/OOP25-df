package it.unibo.df.model.abilities;

import java.util.Optional;
import java.util.Set;

/**
 * Represents the effect produced by an ability once applied.
 *
 * @param targetHpDelta hp variation applied to the target
 * @param casterHpDelta hp variation applied to the caster
 * @param cells optional set of affected cells
 */

public record AbilityEffect(
        int targetHpDelta,
        int casterHpDelta,
        Optional<Set<Vec2D>> cells
) { }


