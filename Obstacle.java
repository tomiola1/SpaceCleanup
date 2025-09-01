package model;

import engine.Assets;
import java.awt.*;

public class Obstacle extends Entity {
    public int w = 60, h = 24;
    public Obstacle(int x, int y, int w, int h) { this.pos.set(x, y); this.w = w; this.h = h; this.radius = Math.max(w,h)/2; }
    @Override public void update(double dt, int worldW, int worldH) { }
    @Override public void render(Graphics2D g) { Assets.drawObstacle(g, (int)pos.x, (int)pos.y, w, h); }
}
