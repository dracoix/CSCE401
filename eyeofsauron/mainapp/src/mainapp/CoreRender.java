package mainapp;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CoreRender {
    
    // GLOBAL JavaFX Objects and Render Helpers
    
    public static final int MASTER_FRAMES_PER_SECOND = 30;
    public static final int MASTER_FRAME_TIME = 1000 / MASTER_FRAMES_PER_SECOND;

    public static final Group GROUP_ROOT = new Group();
    public static final Scene SCENE_SURFACE = new Scene(GROUP_ROOT, CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT, Color.BLACK);

    public static final Canvas CANVAS_BACKGROUND = new Canvas(CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);
    public static final Canvas CANVAS_SURFACE = new Canvas(CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);
    public static final Canvas CANVAS_CURSOR = new Canvas(CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);
    public static final Canvas CANVAS_DEBUG = new Canvas(CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);

    public static final Font fntImpact48 = Font.font("Impact", 48);
    public static final Font fntImpact32 = Font.font("Impact", 32);
    public static final Font fntImpact24 = Font.font("Impact", 24);
    public static final Font fntImpact20 = Font.font("Impact", 20);
    public static final Font fntImpact18 = Font.font("Impact", 18);

    public static double getFontWidth(GraphicsContext gc, String s) {
        return Toolkit.getToolkit().getFontLoader().computeStringWidth(s, gc.getFont());
    }

    public static double getFontHeight(GraphicsContext gc) {
        return Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
    }

    public static GraphicsContext getGC(Canvas c) {
        return c.getGraphicsContext2D();
    }

    public static void FullReset() {
        CanvasReset(CANVAS_BACKGROUND);
        CanvasReset(CANVAS_SURFACE);
        CanvasReset(CANVAS_CURSOR);
        CanvasReset(CANVAS_DEBUG);
    }

    public static void CanvasReset(Canvas c) {
        CanvasWipe(c);
        c.setBlendMode(null);
        c.setEffect(null);
    }

    public static void CanvasWipe(Canvas c) {
        c.getGraphicsContext2D().clearRect(0, 0, CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);
    }

    public static void CanvasWipe(GraphicsContext gc) {
        gc.clearRect(0, 0, CoreEngine.SCREEN_WIDTH, CoreEngine.SCREEN_HEIGHT);
    }

}
