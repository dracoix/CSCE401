package whackamole;

import static whackamole.BetterMath.sqr;

public class BetterPoint2D {

    double x;
    double y;

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

    public double distance(BetterPoint2D p) {
        return distance(p.x, p.y);
    }

    public double distance(double x, double y) {
        return Math.sqrt(sqr(this.x - x) + sqr(this.y - y));
    }

    public void MidpointAndSet(double x, double y) {
        this.x = (this.x + x) / 2;
        this.y = (this.y + y) / 2;
    }

    public void MidpointAndSet(BetterPoint2D p) {
        MidpointAndSet(p.x, p.y);
    }

}
