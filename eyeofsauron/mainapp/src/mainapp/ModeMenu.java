/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.awt.image.BufferedImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import static mainapp.BetterUtils.time;
import static mainapp.CoreEngine.*;

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

    final double cellWidth = CoreEngine.SCREEN_WIDTH / 3;
    final double cellHeight = CoreEngine.SCREEN_HEIGHT / 3;

    long cellPush[] = new long[4];
    boolean nextModeReady;

    static final double gfxTowerWidth = 200;
    static final double gfxTowerHeight = 3.3355 * gfxTowerWidth;
    static final double gfxEyeWidth = gfxTowerWidth * 0.8;
    static final double gfxEyeHeight = gfxEyeWidth * 0.740097;

    final Image menuBackground = new Image("BACKGROUND.png", SCREEN_WIDTH, SCREEN_HEIGHT, true, true);
    final Image menuTower = new Image("TOWER.png", gfxTowerWidth, gfxTowerHeight, true, true);
    final Image menuEye = new Image("EmptyEye.png", gfxEyeWidth, gfxEyeHeight, true, true);

    public ModeMenu(AbstractMode nextMode) {
        super(nextMode);
        miniCalc.init();

    }

    @Override
    public void init(Object[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    long snap = time();

    @Override
    public void tick() {
        //c.setEffect(null);

        resetMiniCalib();
        buttonCheck();

        CanvasWipe(CANVAS_SURFACE);

        renderObjects(CANVAS_SURFACE.getGraphicsContext2D());

        //CANVAS_CURSOR.setBlendMode(null);
        //CanvasFade(CANVAS_CURSOR,0.9);
        CanvasWipe(CANVAS_CURSOR);
        CANVAS_CURSOR.setOpacity(1);
        //CANVAS_CURSOR.getGraphicsContext2D().clearRect(1, 1, 500,500);
        //CANVAS_CURSOR.getGraphicsContext2D().setFill(Color.BLACK);
        //CANVAS_CURSOR.getGraphicsContext2D().fillRect(0, 0, CANVAS_CURSOR.getWidth(), CANVAS_CURSOR.getHeight());
        //CANVAS_CURSOR.getGraphicsContext2D().clearRect(0, 0, CANVAS_CURSOR.getWidth(), CANVAS_CURSOR.getHeight());
        //CoreEngine.CanvasFade(CoreEngine.CANVAS_CURSOR, 0.9);
        CANVAS_CURSOR.setEffect(bbCursor);
        CANVAS_CURSOR.setBlendMode(BlendMode.HARD_LIGHT);
        renderCursor(CoreEngine.CANVAS_CURSOR, CoreEngine.CANVAS_CURSOR.getGraphicsContext2D());
    }

    double minX;
    double maxX;
    double minY;
    double maxY;

    double cY;
    double cX;
    static double fY;
    static double fX;

    void runMiniCalib() {
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
            //fX *=1;
            fX = (SCREEN_WIDTH * 5 / 6);
            //fX /= 2;
        }

        if (fX < (SCREEN_WIDTH * 1 / 6)) {
            //fX *=1;
            fX = (SCREEN_WIDTH * 1 / 6);
            //fX /= 2;
        }

        if (fY > (SCREEN_HEIGHT * 5 / 6)) {
            //fY *=1;
            fY = (SCREEN_HEIGHT * 5 / 6);
            //fY /= 2;
        }

        if (fY < (SCREEN_HEIGHT * 1 / 6)) {
            //fY *=1;
            fY = (SCREEN_HEIGHT * 1 / 6);
            //fY /= 2;
        }

    }

    void resetMiniCalib() {
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

    boolean[] cellHover = new boolean[4];
    int cellHoverIndex;

    void buttonCheck() {
        cellHover[0] = false;
        cellHover[1] = false;
        cellHover[2] = false;
        cellHover[3] = false;

        cellHoverIndex = -1;

        if (BetterPoint2D.collides(fX, fY, SCREEN_WIDTH / 6, SCREEN_HEIGHT / 6, 100)) {
            cellHoverIndex = 0;
        }

        if (BetterPoint2D.collides(fX, fY, SCREEN_WIDTH * 5 / 6, SCREEN_HEIGHT / 6, 100)) {
            cellHoverIndex = 1;
        }

        if (BetterPoint2D.collides(fX, fY, SCREEN_WIDTH / 6, SCREEN_HEIGHT * 5 / 6, 100)) {
            cellHoverIndex = 2;
        }

        if (BetterPoint2D.collides(fX, fY, SCREEN_WIDTH * 5 / 6, SCREEN_HEIGHT * 5 / 6, 100)) {
            cellHoverIndex = 3;
        }

        pushCell(cellHoverIndex);

    }

    public void renderObjects(GraphicsContext gc) {

        getGC(CANVAS_BACKGROUND_IMAGE).drawImage(menuBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        getGC(CANVAS_BACKGROUND_IMAGE).drawImage(menuTower, SCREEN_WIDTH / 2 - gfxTowerWidth / 2, SCREEN_HEIGHT / 2 - 60, gfxTowerWidth, gfxTowerHeight);
        getGC(CANVAS_BACKGROUND_IMAGE).drawImage(menuEye, SCREEN_WIDTH / 2 - gfxEyeWidth / 2, SCREEN_HEIGHT / 2 - 60, gfxEyeWidth, gfxEyeHeight);

        // render 4 cells;
        renderText(gc);

    }

    public void updateObjects() {

    }

    private double lastDeltaX;
    private double lastDeltaY;

    Glow gText = new Glow(1);

    double fntHeight;

    private void renderText(GraphicsContext gc) {
        gc.setFont(fntImpact48);
        fntHeight = cellHeight / 2 + getFontHeight(gc)/3;
        if (cellHoverIndex > -1) {
            gc.save();

            gc.setFill(Color.RED);
            switch (cellHoverIndex) {
                case 0:
                    gc.fillText(strCalib,
                            cellWidth / 2 - getFontWidth(gc, strCalib) / 2,
                            fntHeight);
                    break;
                case 1:
                    gc.fillText(strNewGame,
                            cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strNewGame) / 2,
                            fntHeight);
                    break;
                case 2:
                    gc.fillText(strHelp,
                            cellWidth / 2 - getFontWidth(gc, strHelp) / 2,
                            cellHeight * 2 + fntHeight);
                    break;
                case 3:
                    gc.fillText(strScores,
                            cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strScores) / 2,
                            cellHeight * 2 + fntHeight);
                    break;

            }
            gc.applyEffect(gText);
            gc.applyEffect(gText);
            gc.applyEffect(gText);
            gc.restore();
        }

        // TOP LEFT
        gc.setFill(Color.WHITE.interpolate(Color.TRANSPARENT, 0.5));
        gc.fillText(strCalib,
                cellWidth / 2 - getFontWidth(gc, strCalib) / 2,
                fntHeight);

        // TOP RIGHT
        gc.fillText(strNewGame,
                cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strNewGame) / 2,
                fntHeight);

        //BOTTOM LEFT
        gc.fillText(strHelp,
                cellWidth / 2 - getFontWidth(gc, strHelp) / 2,
                cellHeight * 2 + fntHeight);

        //BOTTOM RIGHT
        gc.fillText(strScores,
                cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strScores) / 2,
                cellHeight * 2 + fntHeight);

    }

    BoxBlur bbCursor = new BoxBlur(10, 10, 2);

    private static class miniCalc {

        static double[] gfxWCursorX = new double[4];
        static double[] gfxWCursorY = new double[4];
        static double[] gfxHCursorX = new double[4];
        static double[] gfxHCursorY = new double[4];
        static double gfxDistC, gfxDistPer, gfxEndWidth, gfxEndHeight;
        static double gfxSqrScreen = Math.hypot(SCREEN_WIDTH, SCREEN_HEIGHT);

        static void init() {
            gfxHCursorX[2] = SCREEN_CENTER_X;
            gfxHCursorX[3] = SCREEN_CENTER_X;
            gfxHCursorY[2] = SCREEN_CENTER_Y + gfxEyeHeight / 4;
            gfxHCursorY[3] = SCREEN_CENTER_Y - gfxEyeHeight / 4;

            gfxWCursorY[2] = SCREEN_CENTER_Y;
            gfxWCursorY[3] = SCREEN_CENTER_Y;
            gfxWCursorX[2] = SCREEN_CENTER_X + 10;
            gfxWCursorX[3] = SCREEN_CENTER_X - 10;

        }

        static void calc() {
            gfxDistC = BetterPoint2D.distance(fX, fY, SCREEN_CENTER_X, SCREEN_CENTER_Y);

            gfxDistPer = (gfxDistC / gfxSqrScreen);
            gfxEndWidth = gfxDistPer * 200;
            gfxEndHeight = gfxDistPer * 400;

            gfxHCursorX[0] = fX;
            gfxHCursorX[1] = fX;
            gfxHCursorY[0] = fY - gfxEndHeight / 2 - gfxEyeHeight / 4;
            gfxHCursorY[1] = fY + gfxEndHeight / 2 + gfxEyeHeight / 4;

            gfxWCursorX[0] = fX - gfxEndWidth / 2 - 10;
            gfxWCursorX[1] = fX + gfxEndWidth / 2 + 10;
            gfxWCursorY[0] = fY;
            gfxWCursorY[1] = fY;

        }
    }

    private void renderCursor(Canvas c, GraphicsContext gc) {

        gc.save();
        gc.setGlobalAlpha(0.1);
        //gc.setGlobalBlendMode(BlendMode.OVERLAY);
        gc.setFill(Color.YELLOW);

        gc.fillOval(SCREEN_CENTER_X - 10, SCREEN_CENTER_Y - gfxEyeHeight / 4, 20, gfxEyeHeight / 2);

        miniCalc.calc();

        gc.fillPolygon(miniCalc.gfxWCursorX, miniCalc.gfxWCursorY, 4);
        gc.fillPolygon(miniCalc.gfxHCursorX, miniCalc.gfxHCursorY, 4);

        gc.restore();
        gc.save();
        gc.setGlobalAlpha(0.3);
        gc.setFill(Color.YELLOW);

        gc.fillOval(fX - miniCalc.gfxEndWidth * 2 - 10,
                fY - miniCalc.gfxEndHeight / 2 - gfxEyeHeight / 4,
                miniCalc.gfxEndWidth * 4 + 20,
                miniCalc.gfxEndHeight + gfxEyeHeight / 2);

        gc.restore();
        gc.setFill(Color.BLACK);

        gc.fillOval(SCREEN_CENTER_X + (40 * fX / SCREEN_WIDTH) - 20 - (gfxEyeWidth / 16),
                SCREEN_CENTER_Y - (gfxEyeHeight / 4) + (20 * fY / SCREEN_HEIGHT) - 10,
                (gfxEyeWidth / 8),
                gfxEyeHeight / 2);

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
                nextMode = new ModeCalib(this);
                nextMode.startMode();
                this.endMode();
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
