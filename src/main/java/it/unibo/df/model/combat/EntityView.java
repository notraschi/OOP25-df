package it.unibo.df.model.combat;

import it.unibo.df.model.abilities.Vec2D;

public record EntityView(
    int hpMax,
    int hp,
    Vec2D position
) { }
