package model;

import engine.Assets;
import java.awt.*;

public class Ship extends Entity {
    public int lives = 3;
    public int score = 0;
    public boolean shieldOn = false;
    public double shield = 100; // 0..100

    public void thrust(double ax, double ay) {
        vel.add(ax, ay).limit(240);
    }

    public void toggleShield() {
        if (shield > 1) shieldOn = !shieldOn;
    }

    @Override
    public void update(double dt, int worldW, int worldH) {
        vel.mul(0.99);
        pos.add(vel.copy().mul(dt));
        if (pos.x < 0) pos.x += worldW; if (pos.x > worldW) pos.x -= worldW;
        if (pos.y < 0) pos.y += worldH; if (pos.y > worldH) pos.y -= worldH;
        if (shieldOn) shield = Math.max(0, shield - 10 * dt);
        else shield = Math.min(100, shield + 8 * dt);
        if (shield <= 0) shieldOn = false;
    }

    public void hit() {
        if (shieldOn && shield > 0) return;
        lives--;
        vel.set(0,0);
    }

    @Override
    public void render(Graphics2D g) {
        Assets.drawShip(g, pos.x, pos.y, angle, shieldOn);
    }
}
