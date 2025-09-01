package model;

import java.awt.*;

public class PowerUp extends Entity {
    public enum Type { SPEED, SHIELD }
    public Type type = Type.SPEED;
    @Override public void update(double dt, int worldW, int worldH) { }
    @Override public void render(Graphics2D g) {
        g.setColor(Color.yellow);
        g.fillOval((int)(pos.x-8), (int)(pos.y-8), 16, 16);
        g.setColor(Color.black);
        g.drawString(type==Type.SPEED?"S":"H", (int)pos.x-3, (int)pos.y+4);
    }
}
