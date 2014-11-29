/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import com.sun.prism.paint.Paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static mainapp.CoreEngine.SCREEN_HEIGHT;
import static mainapp.CoreEngine.SCREEN_WIDTH;

/**
 *
 * @author Expiscor
 */
public class ModeMenu extends AbstractMode {

    final BetterPoint2D topLeft = new BetterPoint2D(CoreEngine.SCREEN_WIDTH / 6, CoreEngine.SCREEN_HEIGHT / 6);
    final BetterPoint2D topRight = new BetterPoint2D(CoreEngine.SCREEN_WIDTH * 5 / 6, CoreEngine.SCREEN_HEIGHT / 6);

    final BetterPoint2D bottomLeft = new BetterPoint2D(CoreEngine.SCREEN_WIDTH / 6, CoreEngine.SCREEN_HEIGHT * 5 / 6);
    final BetterPoint2D bottomRight = new BetterPoint2D(CoreEngine.SCREEN_WIDTH * 5 / 6, CoreEngine.SCREEN_HEIGHT * 5 / 6);

    public ModeMenu(AbstractMode nextMode) {
        super(nextMode);
    }

    @Override
    public void init(Object[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tick(Canvas c) {

        renderObjects(c.getGraphicsContext2D());

    }

    public void renderObjects(GraphicsContext gc) {
        // render 4 cells;

        renderTopLeft(gc);
        renderTopRight(gc);

        renderBottomLeft(gc);
        renderBottomRight(gc);

    }

    public void updateObjects() {

    }

    private void renderTopLeft(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (topLeft.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);
        }
        gc.fillRect(0, 0, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3);
    }

    private void renderTopRight(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (topRight.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);
        }

        gc.fillRect(SCREEN_WIDTH * 2 / 3, 0, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3);
    }

    private void renderBottomLeft(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (bottomLeft.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);
        }
        gc.fillRect(0, SCREEN_HEIGHT * 2 / 3, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3);
    }

    private void renderBottomRight(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (bottomRight.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);
        }

        gc.fillRect(SCREEN_WIDTH * 2 / 3, SCREEN_HEIGHT * 2 / 3, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3);
    }

}
