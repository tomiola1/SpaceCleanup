package model;

import engine.Assets;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Asteroid extends Entity {
    public Asteroid(int r) { this.radius = r; }

    @Override public void update(double dt, int worldW, int worldH) {
        pos.add(vel.copy().mul(dt));
        if (pos.x < 0) pos.x += worldW; if (pos.x > worldW) pos.x -= worldW;
        if (pos.y < 0) pos.y += worldH; if (pos.y > worldH) pos.y -= worldH;
    }

    @Override public void render(Graphics2D g) { Assets.drawAsteroid(g, pos.x, pos.y, radius); }

    public static Asteroid random(int worldW, int worldH) {
        var rnd = ThreadLocalRandom.current();
        Asteroid a = new Asteroid(rnd.nextInt(10, 28));
        a.pos.set(rnd.nextDouble(worldW), rnd.nextDouble(worldH));
        a.vel.set(rnd.nextDouble(-60, 60), rnd.nextDouble(-60, 60));
        return a;
    }
}
