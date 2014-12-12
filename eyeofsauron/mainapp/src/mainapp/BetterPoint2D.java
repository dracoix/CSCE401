package mainapp;

import static mainapp.BetterMath.hyp;
import static mainapp.BetterMath.sqrt2;

public class BetterPoint2D {

    private double x;
    private double y;

    BetterPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    BetterPoint2D() {

    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static double distance(BetterPoint2D a, BetterPoint2D b) {
        return distance(a.x, a.y, b.x, b.y);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return hyp((x2 - x1), (y2 - y1));
    }

    public double distance(BetterPoint2D p) {
        return distance(this, p);
    }

    public double distance(double x, double y) {
        return distance(this.x, this.y, x, y);
    }

    public void MidpointAndSet(double x, double y) {
        this.x = (this.x + x) / 2;
        this.y = (this.y + y) / 2;
    }

    public void MidpointAndSet(BetterPoint2D p) {
        MidpointAndSet(p.x, p.y);
    }

    public boolean collides(double x, double y, double r) {
        return distance(x, y) <= r;
    }

    public static boolean collides(double x1, double y1, double x2, double y2, double r) {
        return distance(x1, y1, x2, y2) <= r;
    }

    public static boolean collides(BetterPoint2D a, BetterPoint2D b, double r) {
        return distance(a, b) <= r;
    }

    public boolean collides(BetterPoint2D p, double r) {
        return collides(this, p, r);
    }

    public boolean reaches(double x, double y, double r) {
        return distance(x, y) <= r * sqrt2;
    }

    public boolean reaches(BetterPoint2D p, double r) {
        return p.reaches(this.x, this.y, r);
    }

}
