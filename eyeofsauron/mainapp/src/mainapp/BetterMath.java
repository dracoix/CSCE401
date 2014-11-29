/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

/**
 *
 * @author Expiscor
 */
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
}
