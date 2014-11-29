package whackamole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import static whackamole.BetterMath.rms;
import static whackamole.BetterMath.sqr;

/**
 *
 * @author Expiscor
 */
public class CalibCore {

    public static Queue<BetterPoint2D> scatter = new LinkedList<BetterPoint2D>();
    public static double minX, maxX, minY, maxY;
    public static double offsetWidth, offsetHeight;
    public static double adjX, adjY;

    public static void resetCalib(double width, double height) {
        maxX = 0;
        maxY = 0;
        minY = height;
        minX = width;
    }

    private static void calibrateX(double x, double width) {
//        if (x <= 2) {
//            minX--;
//            maxX--;
//            return;
//        }
//
//        if (x >= width - 2) {
//            maxX++;
//            minX++;
//            return;
//        }

        if (x > maxX) {
            maxX = rms(maxX, x);
            return;
        }

        if (x < minX) {
            minX = rms(minX, x);
            return;
        }
    }

    private static void calibrateY(double y, double height) {
//        if (y <= 2) {
//            minY--;
//            maxY--;
//            return;
//        }
//
//        if (y >= height - 2) {
//            maxY++;
//            minY++;
//            return;
//        }

        if (y > maxY) {
            maxY = rms(maxY, y);
            return;
        }

        if (y < minY) {
            minY = rms(minY, y);
            return;
        }
    }

    public static void calibrate(double x, double y, double width, double height) {
        adjX = rms(x, adjX);
        adjY = rms(y, adjY);
        calibrateX(adjX, width);
        calibrateY(adjY, height);

        offsetWidth = maxX - minX;
        offsetHeight = maxY - minY;

    }

    private static void addScatter(double x, double y) {

        for (BetterPoint2D p : scatter) {
            if ((int) p.getX() == (int) x) {
                if ((int) p.getY() == (int) y) {
                    return;
                }
            }
        }

        scatter.add(new BetterPoint2D(x, y));
    }

    // 
    public static double getOffsetX(double x, double width) {
        adjX = rms(x, adjX);
        return ((adjX - minX) * width / offsetWidth);
    }

    public static double getOffsetY(double y, double height) {
        adjY = rms(y, adjY);
        return ((adjY - minY) * height / offsetHeight);
    }

}
