/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import static mainapp.BetterMath.rms;

/**
 *
 * @author Expiscor
 */
public class CoreEngine {

    public static final BetterPoint2D SCREEN_MOUSE = new BetterPoint2D();

    public static final BetterPoint2D FUZZY_MOUSE = new BetterPoint2D();

    public static int MASTER_FRAME_TIME = 1000 / 30;

    public static double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();

    public static final Group GROUP_ROOT = new Group();

    public static final Scene SCENE_SURFACE = new Scene(GROUP_ROOT, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
    public static final Canvas CANVAS_BACKGROUND_IMAGE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final Canvas CANVAS_SURFACE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final Canvas CANVAS_DEBUG = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);

    public static double CALIB_MIN_X;
    public static double CALIB_MAX_X;
    public static double CALIB_MIN_Y;
    public static double CALIB_MAX_Y;
    public static byte[] CALIB_ADJ_X = new byte[256];
    public static byte[] CALIB_ADJ_Y = new byte[256];

    public static final Font fntImpact48 = Font.font("Impact", 48);
    public static final Font fntImpact32 = Font.font("Impact", 32);
    public static final Font fntImpact24 = Font.font("Impact", 24);
    public static final Font fntImpact20 = Font.font("Impact", 20);
    public static final Font fntImpact18 = Font.font("Impact", 18);

    public static void prepEngine() {
        GROUP_ROOT.getChildren().add(CANVAS_BACKGROUND_IMAGE);
        GROUP_ROOT.getChildren().add(CANVAS_SURFACE);
        GROUP_ROOT.getChildren().add(CANVAS_DEBUG);
    }

    public static double calcY() {
        return ((SCREEN_MOUSE.getY() - CALIB_MIN_Y) / (CALIB_MAX_Y - CALIB_MIN_Y)) * SCREEN_HEIGHT;
    }

    public static double calcX() {
        return ((SCREEN_MOUSE.getX() - CALIB_MIN_X) / (CALIB_MAX_X - CALIB_MIN_X)) * SCREEN_WIDTH;
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

    public static final BetterPoint2D FUZZY_ADJ_MOUSE = new BetterPoint2D();
    private static double tmpFuzzyY;
    private static double tmpFuzzyX;

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

    public static double getFontWidth(GraphicsContext gc, String s) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(s, gc.getFont());
    }

    public static double getFontHeight(GraphicsContext gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
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

    public static double CALIB_ADJ_MIN_Y;
    public static double CALIB_ADJ_MAX_Y;
    public static double CALIB_ADJ_MIN_X;
    public static double CALIB_ADJ_MAX_X;

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

}
