package whackamole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Expiscor
 */
public class BetterMath {

    public static double sqr(double x) {
        return x * x;
    }

    public static double rms(double a, double b) {
        return Math.sqrt((sqr(a) + sqr(b)) / 2);
    }
}
