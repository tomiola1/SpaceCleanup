package world;

import engine.Collision;
import engine.Input;
import model.*;
import util.Config;
import util.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Level {
    // Viewport (screen) size
    private final int viewW, viewH;
    // World size (larger than screen)
    private final int worldW, worldH;

    private final Ship ship = new Ship();
    private final List<Entity> garbage = new ArrayList<>();
    private final List<Entity> asteroids = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private int level = 1;
    private double time = 0; // seconds elapsed on level
    private double timeLeft; // countdown if timer enabled

    private boolean gameOver = false;
    private boolean submittedScore = false;
    private final HighScoreManager scores = new HighScoreManager();

    // camera top-left in world coords
    private double camX = 0, camY = 0;

    public Level(int screenW, int screenH) {
        this.viewW = screenW; this.viewH = screenH;
        this.worldW = screenW * Math.max(1, Config.WORLD_MULTIPLIER);
        this.worldH = screenH * Math.max(1, Config.WORLD_MULTIPLIER);
        resetLevel();
    }

    private void resetLevel() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        ship.pos.set(worldW/2.0, worldH/2.0);
        ship.vel.set(0,0);
        ship.angle = 0;
        garbage.clear(); asteroids.clear(); obstacles.clear(); bullets.clear();
        // Spawn garbage: collected when smaller than ship (ship uses radius=12 here)
        for (int i=0;i<6 + level;i++) {
            Garbage g = new Garbage(rnd.nextInt(6, 12));
            g.pos.set(rnd.nextDouble(worldW), rnd.nextDouble(worldH));
            garbage.add(g);
        }
        // Spawn asteroids
        for (int i=0;i<3 + level;i++) asteroids.add(Asteroid.random(worldW, worldH));
        // Spawn obstacles
        for (int i=0;i<2;i++) {
            int x = rnd.nextInt(40, worldW-180), y = rnd.nextInt(40, worldH-120);
            obstacles.add(new Obstacle(x, y, rnd.nextInt(80,160), rnd.nextInt(16,36)));
        }
        time = 0;
        timeLeft = Config.LEVEL_TIME_SECONDS;
        gameOver = false;
        submittedScore = false;
    }

    public void update(Input input) {
        double dt = 1.0/60.0;
        if (gameOver) {
            // Press ENTER to start a new run
            if (input.isDown(KeyEvent.VK_ENTER)) newRun();
            return;
        }

        time += dt;
        if (Config.ENABLE_TIMER) {
            timeLeft -= dt;
            if (timeLeft <= 0) {
                // time penalty: lose a life and reset level
                ship.hit();
                if (ship.lives <= 0) {
                    triggerGameOver();
                    return;
                } else {
                    resetLevel();
                }
            }
        }

        // Input → ship control
        double accel = input.isDown(KeyEvent.VK_SHIFT) ? 180 : 120;
        double ax=0, ay=0;
        if (input.isDown(KeyEvent.VK_A) || input.isDown(KeyEvent.VK_LEFT))  ax -= accel*dt;
        if (input.isDown(KeyEvent.VK_D) || input.isDown(KeyEvent.VK_RIGHT)) ax += accel*dt;
        if (input.isDown(KeyEvent.VK_W) || input.isDown(KeyEvent.VK_UP))    ay -= accel*dt;
        if (input.isDown(KeyEvent.VK_S) || input.isDown(KeyEvent.VK_DOWN))  ay += accel*dt;
        if (ax!=0 || ay!=0) {
            ship.thrust(ax, ay);
            ship.angle = Math.atan2(ay, ax);
        }
        if (input.isDown(KeyEvent.VK_SPACE)) {
            shoot();
        }
        if (input.isDown(KeyEvent.VK_E)) {
            ship.toggleShield();
        }

        // Update entities
        ship.update(dt, worldW, worldH);
        for (var a : asteroids) a.update(dt, worldW, worldH);
        for (var b : bullets) b.update(dt, worldW, worldH);

        // Collisions: ship vs asteroids
        for (var a : asteroids) {
            if (!a.alive) continue;
            if (Collision.circleCircle(ship.pos.x, ship.pos.y, ship.radius, a.pos.x, a.pos.y, a.radius)) {
                ship.hit();
                a.alive = false; // destroy on hit for simplicity
                if (ship.lives <= 0) { triggerGameOver(); return; }
            }
        }
        asteroids.removeIf(e -> !e.alive);

        // Collisions: ship vs obstacles (simple knockback)
        for (var o : obstacles) {
            if (Collision.circleAABB(ship.pos.x, ship.pos.y, ship.radius, o.pos.x, o.pos.y, o.w, o.h)) {
                ship.hit();
                ship.pos.add(-ship.vel.x*0.2, -ship.vel.y*0.2);
                if (ship.lives <= 0) { triggerGameOver(); return; }
            }
        }

        // Pickups: ship collects garbage only if smaller than ship radius
        for (var g : garbage) {
            if (!g.alive) continue;
            if (g.radius < ship.radius && Collision.circleCircle(ship.pos.x, ship.pos.y, ship.radius, g.pos.x, g.pos.y, g.radius)) {
                g.alive = false;
                ship.score += 10;
            }
        }
        garbage.removeIf(e -> !e.alive);

        // Bullets vs asteroids (optional combat)
        for (var b : bullets) {
            for (var a : asteroids) {
                if (Collision.circleCircle(b.pos.x, b.pos.y, 3, a.pos.x, a.pos.y, a.radius)) {
                    b.alive = false; a.alive = false; ship.score += 5;
                }
            }
        }
        bullets.removeIf(e -> !e.alive);
        asteroids.removeIf(e -> !e.alive);

        // Camera: center on ship, clamp to world
        camX = Math.max(0, Math.min(ship.pos.x - viewW/2.0, worldW - viewW));
        camY = Math.max(0, Math.min(ship.pos.y - viewH/2.0, worldH - viewH));

        // Level complete
        if (garbage.isEmpty()) nextLevel();
    }

    private void triggerGameOver() {
        gameOver = true;
        if (!submittedScore) {
            submittedScore = true;
            SwingUtilities.invokeLater(() -> {
                String name = JOptionPane.showInputDialog(null,
                        "Game Over! Your score: " + ship.score + "\nEnter your name for highscores:",
                        "Submit Score", JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.isBlank()) {
                    try { scores.submit(name.trim(), ship.score); } catch (IOException ignored) {}
                }
                try {
                    var top = scores.top();
                    StringBuilder sb = new StringBuilder("High Scores\n\n");
                    for (int i=0; i<Math.min(5, top.size()); i++) sb.append((i+1)).append(". ").append(top.get(i)).append("\n");
                    sb.append("\nPress ENTER to start a new run.");
                    JOptionPane.showMessageDialog(null, sb.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ignored) {}
            });
        }
    }

    private void newRun() {
        ship.lives = 3; ship.score = 0; level = 1; resetLevel();
    }

    private void nextLevel() {
        level++;
        ship.lives = Math.min(5, ship.lives + 1);
        resetLevel();
    }

    private void shoot() {
        if (bullets.size() > 12) return;
        Bullet b = new Bullet();
        b.pos.set(ship.pos.x, ship.pos.y);
        double s = 280;
        b.vel.set(Math.cos(ship.angle)*s, Math.sin(ship.angle)*s);
        bullets.add(b);
    }

    public void render(Graphics2D g) {
        var oldTx = g.getTransform();
        g.translate(-camX, -camY);

        g.setColor(new Color(40,40,60));
        g.drawRect(0, 0, worldW-1, worldH-1);

        for (var o : obstacles) o.render(g);
        for (var e : garbage) e.render(g);
        for (var a : asteroids) a.render(g);
        for (var b : bullets) b.render(g);
        ship.render(g);

        g.setTransform(oldTx);

        g.setColor(Color.white);
        String timerText = Config.ENABLE_TIMER ? ("  Time: " + Math.max(0, (int)Math.ceil(timeLeft)) + "s") : "";
        g.drawString("Level: " + level + "  Score: " + ship.score + "  Lives: " + ship.lives +
                "  Shield: " + (int)ship.shield + "%" + timerText, 12, 20);
        if (!gameOver)
            g.drawString("WASD/Arrows move, SPACE shoot, E shield", 12, 36);
        else
            g.drawString("GAME OVER — Press ENTER to restart", 12, 36);

    }
}
