package mainapp;

import javafx.scene.effect.BlendMode;
import javafx.stage.Screen;
import static mainapp.BetterMath.rms;

public class CoreEngine {

    //True Mouse Location on Screen
    public static final BetterPoint2D SCREEN_MOUSE = new BetterPoint2D();

    //Smoothed Mouse Location on Screen
    public static final BetterPoint2D FUZZY_MOUSE = new BetterPoint2D();

    //Virtual Mouse Location on Screen
    public static final BetterPoint2D FUZZY_ADJ_MOUSE = new BetterPoint2D();

    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();

    public static final double SCREEN_CENTER_X = SCREEN_WIDTH / 2;
    public static final double SCREEN_CENTER_Y = SCREEN_HEIGHT / 2;

    public static double CALIB_MIN_X;
    public static double CALIB_MAX_X;
    public static double CALIB_MIN_Y;
    public static double CALIB_MAX_Y;
    public static byte[] CALIB_ADJ_X = new byte[256];
    public static byte[] CALIB_ADJ_Y = new byte[256];

    private static double tmpFuzzyY;
    private static double tmpFuzzyX;

    public static double CALIB_ADJ_MIN_Y;
    public static double CALIB_ADJ_MAX_Y;
    public static double CALIB_ADJ_MIN_X;
    public static double CALIB_ADJ_MAX_X;

    public static void prepEngine() {
        CoreRender.GROUP_ROOT.getChildren().add(CoreRender.CANVAS_BACKGROUND);
        CoreRender.GROUP_ROOT.getChildren().add(CoreRender.CANVAS_SURFACE);
        CoreRender.GROUP_ROOT.getChildren().add(CoreRender.CANVAS_CURSOR);
        CoreRender.GROUP_ROOT.getChildren().add(CoreRender.CANVAS_DEBUG);
        CoreRender.GROUP_ROOT.setBlendMode(BlendMode.SCREEN);
    }

    public static double calcFuzzyY() {

        tmpFuzzyY = ((FUZZY_MOUSE.getY() - CALIB_ADJ_MIN_Y) / (CALIB_ADJ_MAX_Y - CALIB_ADJ_MIN_Y)) * SCREEN_HEIGHT;
        if (tmpFuzzyY < 0) {
            tmpFuzzyY = 0;
        }
        if (tmpFuzzyY > SCREEN_HEIGHT) {
            tmpFuzzyY = SCREEN_HEIGHT;
        }
        return tmpFuzzyY;
    }

    public static void tickFuzzyAdj() {
        FUZZY_ADJ_MOUSE.set(calcFuzzyX(), calcFuzzyY());
    }

    public static double calcFuzzyX() {
        tmpFuzzyX = ((FUZZY_MOUSE.getX() - CALIB_ADJ_MIN_X) / (CALIB_ADJ_MAX_X - CALIB_ADJ_MIN_X)) * SCREEN_WIDTH;
        if (tmpFuzzyX < 0) {
            tmpFuzzyX = 0;
        }
        if (tmpFuzzyX > SCREEN_WIDTH) {
            tmpFuzzyX = SCREEN_WIDTH;
        }
        return tmpFuzzyX;
    }

    public static void resetCalib() {
        CALIB_MIN_X = SCREEN_WIDTH;
        CALIB_MIN_Y = SCREEN_HEIGHT;
        CALIB_MAX_X = 0;
        CALIB_MAX_Y = 0;

        for (int i = 0; i < 256; i++) {
            CALIB_ADJ_X[i] = 0;
            CALIB_ADJ_Y[i] = 0;
        }
    }

    public static void calibrate() {
        if (FUZZY_MOUSE.getX() > CALIB_MAX_X) {
            CALIB_MAX_X = FUZZY_MOUSE.getX();
        }

        if (FUZZY_MOUSE.getX() < CALIB_MIN_X) {
            CALIB_MIN_X = FUZZY_MOUSE.getX();
        }

        if (FUZZY_MOUSE.getY() > CALIB_MAX_Y) {
            CALIB_MAX_Y = FUZZY_MOUSE.getY();
        }

        if (FUZZY_MOUSE.getY() < CALIB_MIN_Y) {
            CALIB_MIN_Y = FUZZY_MOUSE.getY();
        }

        CALIB_ADJ_X[(int) (255 * (FUZZY_MOUSE.getX() / SCREEN_WIDTH))] = 1;
        CALIB_ADJ_Y[(int) (255 * (FUZZY_MOUSE.getY() / SCREEN_HEIGHT))] = 1;
    }

    public static void endCalib() {
        CALIB_ADJ_MIN_Y = SCREEN_HEIGHT / 2;
        CALIB_ADJ_MAX_Y = SCREEN_HEIGHT / 2;
        CALIB_ADJ_MIN_X = SCREEN_WIDTH / 2;
        CALIB_ADJ_MAX_X = SCREEN_WIDTH / 2;

        for (int x = 127; x >= 0; x--) {
            if (CALIB_ADJ_X[x] == 1) {
                CALIB_ADJ_MIN_X = rms(CALIB_ADJ_MIN_X, ((double) x * SCREEN_WIDTH / 255));
            }
        }

        for (int x = 128; x < 256; x++) {
            if (CALIB_ADJ_X[x] == 1) {
                CALIB_ADJ_MAX_X = rms(CALIB_ADJ_MAX_X, ((double) x * SCREEN_WIDTH / 255));
            }
        }

        for (int y = 127; y >= 0; y--) {
            if (CALIB_ADJ_Y[y] == 1) {
                CALIB_ADJ_MIN_Y = rms(CALIB_ADJ_MIN_Y, ((double) y * SCREEN_HEIGHT / 255));
            }
        }

        for (int y = 128; y < 256; y++) {
            if (CALIB_ADJ_Y[y] == 1) {
                CALIB_ADJ_MAX_Y = rms(CALIB_ADJ_MAX_Y, ((double) y * SCREEN_HEIGHT / 255));
            }
        }
    }

    static class miniCalib {
        // Dynamic live calibration for simple tasks
        // Used on menu and score entry
        
        public static double maxX;
        public static double maxY;
        public static double minX;
        public static double minY;
        public static double cX;
        public static double cY;
        public static double fX;
        public static double fY;

        static void runMiniCalib() {
            if (SCREEN_MOUSE.getX() > maxX) {
                maxX += SCREEN_MOUSE.getX();
                maxX /= 2;
            }
            if (SCREEN_MOUSE.getX() < minX) {
                minX += SCREEN_MOUSE.getX();
                minX /= 2;
            }

            if (SCREEN_MOUSE.getY() > maxY) {
                maxY += SCREEN_MOUSE.getY();
                maxY /= 2;
            }
            if (SCREEN_MOUSE.getY() < minY) {
                minY += SCREEN_MOUSE.getY();
                minY /= 2;
            }

            cX = (minX + maxX) / 2;
            cY = (minY + maxY) / 2;

            if ((maxX - minX) > 0.5) {
                fX *= 2;
                fX += (1 - (((maxX - SCREEN_MOUSE.getX())) / (maxX - minX))) * SCREEN_WIDTH;
                fX /= 3;
            }

            if ((maxY - minY) > 0.5) {
                fY *= 2;
                fY += (1 - (((maxY - SCREEN_MOUSE.getY())) / (maxY - minY))) * SCREEN_HEIGHT;
                fY /= 3;
            }

            if (fX > (SCREEN_WIDTH * 5 / 6)) {
                fX = (SCREEN_WIDTH * 5 / 6);
            }

            if (fX < (SCREEN_WIDTH * 1 / 6)) {
                fX = (SCREEN_WIDTH * 1 / 6);
            }

            if (fY > (SCREEN_HEIGHT * 5 / 6)) {
                fY = (SCREEN_HEIGHT * 5 / 6);
            }

            if (fY < (SCREEN_HEIGHT * 1 / 6)) {
                fY = (SCREEN_HEIGHT * 1 / 6);
            }

        }

        static void resetMiniCalib() {
            //maxX = 0;
            //minX = SCREEN_WIDTH;

            maxX--;
            minX++;

            //maxY = 0;
            //minY = SCREEN_HEIGHT;
            maxY--;
            minY++;

            if (minY > maxY) {
                maxY += 1;
                minY -= 1;
            }

            if (minX > maxX) {
                maxX += 1;
                minX -= 1;
            }

            cX = (minX + maxX) / 2;
            cY = (minY + maxY) / 2;

            runMiniCalib();

        }

    }

}
