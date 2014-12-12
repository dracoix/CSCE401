package mainapp;

public class BetterMath {

    public static final double sqrt2 = 1.4142135623730950488016887242097d;

    public static double sqr(double x) {
        return x * x;
    }

    public static double hyp(double x, double y) {
        return Math.hypot(x, y);
    }

    public static double hyp2(double x, double y) {
        return sqr(x) + sqr(y);
    }

    public static double rms(double a, double b) {
        return Math.sqrt(hyp2(a, b) / 2);
    }
}
