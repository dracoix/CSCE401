/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import static mainapp.BetterUtils.time;
import static mainapp.CoreEngine.*;
import static mainapp.CoreEngine.miniCalib.fX;
import static mainapp.CoreEngine.miniCalib.fY;
import static mainapp.CoreEngine.miniCalib.resetMiniCalib;
import static mainapp.CoreRender.CANVAS_BACKGROUND;
import static mainapp.CoreRender.CANVAS_CURSOR;
import static mainapp.CoreRender.CANVAS_SURFACE;
import static mainapp.CoreRender.CanvasWipe;
import static mainapp.CoreRender.fntImpact20;
import static mainapp.CoreRender.fntImpact48;
import static mainapp.CoreRender.getFontHeight;
import static mainapp.CoreRender.getFontWidth;
import static mainapp.CoreRender.getGC;

public class ModeMenu extends AbstractMode {

    static final double gfxTowerWidth = 200;
    static final double gfxTowerHeight = 3.3355 * gfxTowerWidth;
    static final double gfxEyeWidth = gfxTowerWidth * 0.8;
    static final double gfxEyeHeight = gfxEyeWidth * 0.740097;

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

    final Image menuBackground = new Image("BACKGROUND.png", SCREEN_WIDTH, SCREEN_HEIGHT, true, true);
    final Image menuTower = new Image("TOWER.png", gfxTowerWidth, gfxTowerHeight, true, true);
    final Image menuEye = new Image("EmptyEye.png", gfxEyeWidth, gfxEyeHeight, true, true);

    long snap = time();

    boolean[] cellHover = new boolean[4];
    int cellHoverIndex;

    long cellPush[] = new long[4];
    boolean nextModeReady;

    Glow gText = new Glow(1);
    BoxBlur bbCursor = new BoxBlur(10, 10, 2);

    double fntHeight;
    final String helpText = "Move your eyes to control selection."
            + "\n CALIBRATE BEFORE EACH GAME FOR BEST RESULTS!";

    public ModeMenu(AbstractMode nextMode) {
        super(nextMode);
        miniCalc.init();
        CoreScores.init();
    }

    @Override
    public void tick() {
        //c.setEffect(null);

        resetMiniCalib();
        miniCalc.calc();
        buttonCheck();

        CanvasWipe(CANVAS_SURFACE);

        renderObjects(CANVAS_SURFACE.getGraphicsContext2D());

        CanvasWipe(CANVAS_CURSOR);
        CANVAS_CURSOR.setOpacity(1);
        CANVAS_CURSOR.setEffect(bbCursor);
        CANVAS_CURSOR.setBlendMode(BlendMode.HARD_LIGHT);
        renderCursor(CANVAS_CURSOR, CANVAS_CURSOR.getGraphicsContext2D());
    }

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

        getGC(CANVAS_BACKGROUND).drawImage(menuBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        getGC(CANVAS_BACKGROUND).drawImage(menuTower, SCREEN_WIDTH / 2 - gfxTowerWidth / 2, SCREEN_HEIGHT / 2 - 60, gfxTowerWidth, gfxTowerHeight);
        getGC(CANVAS_BACKGROUND).drawImage(menuEye, SCREEN_WIDTH / 2 - gfxEyeWidth / 2, SCREEN_HEIGHT / 2 - 60, gfxEyeWidth, gfxEyeHeight);

        // render 4 cells;
        renderText(gc);

    }

    public void updateObjects() {
    }

    private void renderText(GraphicsContext gc) {
        gc.setFont(fntImpact48);
        fntHeight = cellHeight / 2 + getFontHeight(gc) / 3;
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
                            cellHeight * 3 / 2 + fntHeight);

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
        gc.save();
        renderHelp(gc);
        gc.restore();

        //BOTTOM RIGHT
        renderScores(gc);
    }

    void renderScores(GraphicsContext gc) {
        gc.fillText(strScores,
                cellWidth * 2 + cellWidth / 2 - getFontWidth(gc, strScores) / 2,
                cellHeight * 3 / 2 + fntHeight);

        if (cellHoverIndex == 3) {
            gc.setFill(Color.WHITE);
        }
        gc.setFont(fntImpact20);
        for (int i = 0; i < CoreScores.count(); i++) {
            if (i > 9) {
                continue;
            }
            gc.fillText(CoreScores.getPrinted(i),
                    cellWidth * 2 + cellWidth / 4,
                    cellHeight * 3 / 2 + fntHeight + 32
                    + 20 * i);

        }
    }

    private void renderHelp(GraphicsContext gc) {
        if (cellHoverIndex != 2) {
            return;
        }
        gc.setFill(Color.BLACK);
        gc.fillRect(0,
                cellHeight * 2, cellWidth, cellHeight);
        gc.setFont(fntImpact20);
        gc.setFill(Color.WHITE);
        gc.fillText(helpText, 20, cellHeight * 2 + cellHeight / 2);

    }

    private void renderCursor(Canvas c, GraphicsContext gc) {
        gc.save();
        gc.setGlobalAlpha(0.1);
        gc.setFill(Color.YELLOW);

        gc.fillOval(SCREEN_CENTER_X - 10, SCREEN_CENTER_Y - gfxEyeHeight / 4, 20, gfxEyeHeight / 2);

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
                this.endMode();
                break;
            case 1:
                //TopRight
                nextMode = new ModeCalib(new ModeWhackamole(new ModeScore(this)));
                this.endMode();
                break;
            case 2:
                //BottomLeft
                //Help doesn't need a mode
                break;
            case 3:
                //BottomRight
                //Debug
                //nextMode = new ModeScore(this);
                //this.endMode();
                break;
        }
    }

    private static class miniCalc {

        // Search Light Eyecandy calculations
        // Uses miniCalib
        static double[] gfxWCursorX = new double[4];
        static double[] gfxWCursorY = new double[4];
        static double[] gfxHCursorX = new double[4];
        static double[] gfxHCursorY = new double[4];
        static double gfxDistC;
        static double gfxDistPer;
        static double gfxEndWidth;
        static double gfxEndHeight;
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

}
