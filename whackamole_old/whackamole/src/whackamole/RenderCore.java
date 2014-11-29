package whackamole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import static whackamole.CalibCore.getOffsetX;
import static whackamole.CalibCore.getOffsetY;
import static whackamole.CalibCore.minX;
import static whackamole.CalibCore.minY;
import static whackamole.CalibCore.offsetHeight;
import static whackamole.CalibCore.offsetWidth;
import static whackamole.EngineCore.GRID;
import static whackamole.EngineCore.MOLES;
import static whackamole.EngineCore.MOVE_X;
import static whackamole.EngineCore.MOVE_Y;

public class RenderCore {

    public static double SCREEN_WIDTH = 1366;
    public static double SCREEN_HEIGHT = 768;

    public static final Group GROUP_ROOT = new Group();

    public static final Scene SCENE_SURFACE = new Scene(GROUP_ROOT, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
    public static final Canvas CANVAS_BACKGROUND_IMAGE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static final Canvas CANVAS_SURFACE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    public static Image imgWhackField = new Image("whackamole/Grass_Background.jpg");
    public static double scale;

    private static int debug_line_max = 3;
    private static double ratio_depth = 0.5;

    private static void initSurfaces() {
        GROUP_ROOT.getChildren().add(CANVAS_BACKGROUND_IMAGE);
        GROUP_ROOT.getChildren().add(CANVAS_SURFACE);
    }

    public static void prepRenderCore() {
        initSurfaces();
    }

    public static void renderBackgroundImage() {
        getGC(CANVAS_BACKGROUND_IMAGE).setFill(Color.WHITE);
        getGC(CANVAS_BACKGROUND_IMAGE).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        getGC(CANVAS_BACKGROUND_IMAGE).drawImage(imgWhackField, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public static void renderAll() {

        //renderField();
        //renderBackgroundImage();
        wipeSurface();
        renderDepthField();
        //renderMoles();
        //renderHover();
        //renderDebug();

    }

    private static void wipeSurface() {
        getGC(CANVAS_SURFACE).clearRect(0, 0, SCREEN_WIDTH, SCREEN_WIDTH);
    }

    private static void renderHover() {
        getGC(CANVAS_SURFACE).setFill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));
        //getGC(CANVAS_SURFACE).fillOval(MOVE_X - 100, MOVE_Y -100, 200, 200);

        scale = SCREEN_WIDTH - ((SCREEN_HEIGHT - calcY()) / SCREEN_HEIGHT) * (SCREEN_WIDTH - (SCREEN_WIDTH * ratio_depth));
        //scale -= 110;
        scale /= 3;

        getGC(CANVAS_SURFACE).strokeLine(calcX() - scale / 2, calcY(), calcX() + scale / 2, calcY());
        getGC(CANVAS_SURFACE).fillOval(calcX() - scale / 2, calcY() - scale / 4, scale, scale / 2);
    }

    private static double calcX() {
        return getOffsetX(MOVE_X, SCREEN_WIDTH);
    }

    private static double calcY() {
        return getOffsetY(MOVE_Y, SCREEN_HEIGHT);
    }

    private static GraphicsContext getGC(Canvas c) {
        return c.getGraphicsContext2D();
    }

    private static void renderDebug() {
        getGC(CANVAS_SURFACE).setStroke(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));
        getGC(CANVAS_SURFACE).strokeRect(minX, minY, offsetWidth, offsetHeight);
        getGC(CANVAS_SURFACE).setStroke(Color.RED.interpolate(Color.TRANSPARENT, 0.5));
        getGC(CANVAS_SURFACE).strokeOval(MOVE_X - 10, MOVE_Y - 10, 20, 20);
    }

    private static void renderMoles() {

        for (MoleObject m : MOLES) {
            getGC(CANVAS_SURFACE).setFill(m.getColor().interpolate(Color.TRANSPARENT, m.getFade()));
            getGC(CANVAS_SURFACE).fillOval(m.linkedSlotPosition.getX() - 30, m.linkedSlotPosition.getY() - 30, 60, 60);
        }
    }

    private static void renderDepthField() {
        getGC(CANVAS_SURFACE).setFill(Color.WHITE);
        getGC(CANVAS_SURFACE).setStroke(Color.BLUE);
        getGC(CANVAS_SURFACE).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (int x = 0; x <= debug_line_max; x++) {
            getGC(CANVAS_SURFACE).strokeLine(x * (SCREEN_WIDTH / debug_line_max), SCREEN_HEIGHT, x * (SCREEN_WIDTH / debug_line_max) * ratio_depth + (SCREEN_WIDTH - (SCREEN_WIDTH * ratio_depth)) / 2, 0);
        }

        getGC(CANVAS_SURFACE).strokeRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    }

    private static void renderField() {
        getGC(CANVAS_SURFACE).setFill(Color.WHITE);
        for (int x = 0; x < GRID; x++) {
            for (int y = 0; y < GRID; y++) {
                if (new BetterPoint2D(x * SCREEN_WIDTH / GRID, y * SCREEN_HEIGHT / GRID).distance(getOffsetX(MOVE_X - 100, SCREEN_WIDTH), getOffsetY(MOVE_Y - 100, SCREEN_HEIGHT)) < 100) {
                    getGC(CANVAS_SURFACE).setFill(Color.RED);
                } else {
                    getGC(CANVAS_SURFACE).setFill(Color.WHITE);
                }

                getGC(CANVAS_SURFACE).fillRect(x * SCREEN_WIDTH / GRID, y * SCREEN_HEIGHT / GRID, SCREEN_WIDTH / GRID, SCREEN_HEIGHT / GRID);

            }
        }

        getGC(CANVAS_SURFACE).setStroke(Color.BLUE);
        for (int x = 0; x < GRID; x++) {
            for (int y = 0; y < GRID; y++) {
                getGC(CANVAS_SURFACE).strokeRect(x * SCREEN_WIDTH / GRID, y * SCREEN_HEIGHT / GRID, SCREEN_WIDTH / GRID, SCREEN_HEIGHT / GRID);

            }
        }
    }

}
