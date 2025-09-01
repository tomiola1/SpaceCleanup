package math;

public class Vector2D {
    public double x, y;

    public Vector2D() { this(0, 0); }
    public Vector2D(double x, double y) { this.x = x; this.y = y; }

    // set / copy
    public Vector2D set(double x, double y) { this.x = x; this.y = y; return this; }
    public Vector2D copy() { return new Vector2D(x, y); }

    // add
    public Vector2D add(Vector2D v) { this.x += v.x; this.y += v.y; return this; }
    public Vector2D add(double dx, double dy) { this.x += dx; this.y += dy; return this; }
    // common lab method
    public Vector2D addScaled(Vector2D v, double s) { this.x += v.x * s; this.y += v.y * s; return this; }

    // multiply (scale)
    public Vector2D mul(double s) { this.x *= s; this.y *= s; return this; }       // used by current code
    public Vector2D mult(double s) { return mul(s); }                               // alias (many labs use mult)

    // magnitude & normalization
    public double len() { return Math.hypot(x, y); }                                 // used by current code
    public double mag() { return len(); }                                            // alias (some labs use mag)
    public Vector2D normalize() {
        double l = len();
        if (l > 0) { x /= l; y /= l; }
        return this;
    }

    // clamp speed
    public Vector2D limit(double max) {
        double l = len();
        if (l > max && l > 0) {
            x = x / l * max; y = y / l * max;
        }
        return this;
    }

    // handy static creators (optional)
    public static Vector2D fromAngle(double angle, double length) {
        return new Vector2D(Math.cos(angle) * length, Math.sin(angle) * length);
    }
}
