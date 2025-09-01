package model;

import math.Vector2D;
import java.awt.*;

public abstract class Entity {
    public Vector2D pos = new Vector2D();
    public Vector2D vel = new Vector2D();
    public double angle = 0;
    public int radius = 12;
    public boolean alive = true;

    public abstract void update(double dt, int worldW, int worldH);
    public abstract void render(Graphics2D g);
}
