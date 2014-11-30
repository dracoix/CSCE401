/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.awt.FontMetrics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import static mainapp.BetterUtils.time;
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

    final String strCalib = "CALIBRATION";
    final String strNewGame = "NEW GAME";
    final String strScores = "SCORES";
    final String strHelp = "HELP";
    final Font fntImpact48 = Font.font("Impact", 48);
    final Font fntImpact32 = Font.font("Impact", 32);
    final Font fntImpact24 = Font.font("Impact", 24);
    final Font fntImpact20 = Font.font("Impact", 20);
    final Font fntImpact18 = Font.font("Impact", 18);

    final double cellWidth = CoreEngine.SCREEN_WIDTH / 3;
    final double cellHeight = CoreEngine.SCREEN_HEIGHT / 3;

    long cellPush[] = new long[4];
    boolean nextModeReady;

    public ModeMenu(AbstractMode nextMode) {
        super(nextMode);
    }

    @Override
    public void init(Object[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tick(Canvas c) {

        c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
        renderObjects(c.getGraphicsContext2D());

    }

    public void renderObjects(GraphicsContext gc) {
        // render 4 cells;
        renderTopLeft(gc);
        renderTopRight(gc);

        renderBottomLeft(gc);
        renderBottomRight(gc);

        renderCursor(gc);

    }

    public void updateObjects() {

    }

    public double getFontWidth(GraphicsContext gc, String s) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(s, gc.getFont());

    }

    public double getFontHeight(GraphicsContext gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
    }

    private void renderTopLeft(GraphicsContext gc) {
        gc.setFill(Color.WHITE);

        if (topLeft.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);

            pushCell(0);

        }

        gc.fillRect(0, 0, cellWidth, cellHeight);

        gc.setFont(fntImpact32);
        gc.setFill(Color.BLACK);
        gc.fillText(strCalib, cellWidth / 2 - getFontWidth(gc, strCalib) / 2, cellHeight / 2 + getFontHeight(gc) / 2);

    }

    private void renderTopRight(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (topRight.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);

            pushCell(1);

        }

        gc.fillRect(cellWidth * 2, 0, cellWidth, cellHeight);

        gc.setFont(fntImpact32);
        gc.setFill(Color.BLACK);
        gc.fillText(strNewGame, cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strNewGame) / 2, cellHeight / 2 + getFontHeight(gc) / 2);

    }

    private void renderBottomLeft(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (bottomLeft.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);

            pushCell(2);

        }
        gc.fillRect(0, cellHeight * 2, cellWidth, cellHeight);

        gc.setFont(fntImpact32);
        gc.setFill(Color.BLACK);
        gc.fillText(strHelp, cellWidth / 2 - getFontWidth(gc, strHelp) / 2, cellHeight * 2 + cellHeight / 2 + getFontHeight(gc) / 2);

    }

    private void renderBottomRight(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        if (bottomRight.collides(CoreEngine.FUZZY_MOUSE, 200)) {
            gc.setFill(Color.RED);

            pushCell(3);

        }

        gc.fillRect(cellWidth * 2, cellHeight * 2, cellWidth, cellHeight);

        gc.setFont(fntImpact32);
        gc.setFill(Color.BLACK);
        gc.fillText(strScores, cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strScores) / 2, cellHeight * 2 + cellHeight / 2 + getFontHeight(gc) / 2);

    }

    private void renderCursor(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillOval(CoreEngine.FUZZY_MOUSE.getX() - 20, CoreEngine.FUZZY_MOUSE.getY() - 20, 40, 40);

        gc.setFill(Color.GREEN);
        gc.fillOval(CoreEngine.SCREEN_MOUSE.getX() - 2, CoreEngine.SCREEN_MOUSE.getY() - 2, 4, 4);
    }

    private void pushCell(int cellIndex) {
        for (int i = 0; i < 4; i++) {
            if (i == cellIndex) {
                if (cellPush[i] != 0) {
                    if (time() - cellPush[i] > 2000) {
                        fireMode(i);
                        cellPush[i] = 0;
                    }
                } else {
                    cellPush[i] = time();
                }
            } else {
                cellPush[i] = 0;
            }
        }
    }

    private void fireMode(int i) {
        switch (i) {
            case 0:
                //TopLeft
                nextMode = new ModeCalib(null);
                break;
            case 1:
                //TopRight
                nextMode = new ModeWhackamole(null);
                nextMode.startMode();
                this.endMode();
                break;
            case 2:
                //BottomLeft
                //nextMode = new ModeHelp;
                break;
            case 3:
            //BottomRight
                //nextMode = new ModeScore(null);

                //FADE SCORES
                break;
        }
    }

}
