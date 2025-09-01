package model;

import java.awt.*;

public class Bullet extends Entity {
    private double life = 1.5; // seconds
    @Override public void update(double dt, int worldW, int worldH) {
        pos.add(vel.copy().mul(dt));
        life -= dt;
        if (life <= 0) alive = false;
        if (pos.x < 0) pos.x += worldW; if (pos.x > worldW) pos.x -= worldW;
        if (pos.y < 0) pos.y += worldH; if (pos.y > worldH) pos.y -= worldH;
    }
    @Override public void render(Graphics2D g) {
        g.setColor(Color.white);
        g.fillOval((int)pos.x-2, (int)pos.y-2, 4, 4);
    }
}
