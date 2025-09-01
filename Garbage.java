package model;

import engine.Assets;
import java.awt.*;

public class Garbage extends Entity {
    public Garbage(int r) { this.radius = r; }
    @Override public void update(double dt, int worldW, int worldH) { }
    @Override public void render(Graphics2D g) { Assets.drawGarbage(g, pos.x, pos.y, radius); }
}
