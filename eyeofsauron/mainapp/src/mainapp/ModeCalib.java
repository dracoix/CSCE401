/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import static mainapp.BetterUtils.time;
import static mainapp.CoreEngine.*;
import static mainapp.CoreRender.*;

public class ModeCalib extends AbstractMode {

    final String strTitle = "Calibration: Get Ready To Follow The Circle!";
    String strCountDown = "";

    private final long beginTime = time();
    private final int speed = 20;
    private final int startCountFrom = 3;

    private long currentTick = 0;

    boolean ready;
    boolean done;

    BoxBlur bb = new BoxBlur(4, 4, 2);

    private double circleX = SCREEN_WIDTH / 2;
    private double circleY = SCREEN_HEIGHT / 2;

    private int side = 0;

    public ModeCalib(AbstractMode nextMode) {
        super(nextMode);
    }

    @Override
    public void tick() {
        if (ready) {
            getGC(CANVAS_SURFACE).setFill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));
            getGC(CANVAS_SURFACE).fillRect(0, 0, CANVAS_SURFACE.getWidth(), CANVAS_SURFACE.getHeight());
            bb.setHeight(5 + BetterUtils.Random.nextInt(20));
            bb.setWidth(bb.getHeight());
            bb.setIterations(5 + BetterUtils.Random.nextInt(20));
            CANVAS_SURFACE.setEffect(bb);
            eyeFollow(getGC(CANVAS_SURFACE));
        } else {
            CanvasWipe(CANVAS_SURFACE);
            runCountDown(getGC(CANVAS_SURFACE));
        }
        if (done) {
            ready = false;
            done = false;
            nextMode.startMode();
            this.endMode();
        }
    }

    private void eyeFollow(GraphicsContext gc) {
        switch (side) {
            case 0:
                //Center to Top
                circleY -= speed;
                if (circleY <= 40) {
                    circleY = 40;
                    side = 1;
                }
                break;
            case 1:
                //Top Center to Top Right
                circleX += speed;
                if (circleX >= SCREEN_WIDTH - 40) {
                    circleX = SCREEN_WIDTH - 40;
                    side = 2;
                }
                break;
            case 2:
                //Top Right to Bottom Right;
                circleY += speed;
                if (circleY >= SCREEN_HEIGHT - 40) {
                    circleY = SCREEN_HEIGHT - 40;
                    side = 3;
                }
                break;
            case 3:
                //Bottom Right to Bottom Left;
                circleX -= speed;
                if (circleX <= 40) {
                    circleX = 40;
                    side = 4;
                }
                break;
            case 4:
                //Bottom Left to To Left;
                circleY -= speed;
                if (circleY <= 40) {
                    circleY = 40;
                    side = 5;
                }
                break;
            case 5:
                getGC(CANVAS_DEBUG).clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                done = true;
                return;
        }

        gc.setFill(Color.RED.interpolate(Color.YELLOW, BetterUtils.Random.nextFloat() * 0.8));
        gc.fillOval(circleX - 20, circleY - 20, 40, 40);

        calibrate();

        getGC(CANVAS_DEBUG).clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        getGC(CANVAS_DEBUG).setStroke(Color.WHITE);
        getGC(CANVAS_DEBUG).strokeRect(CALIB_ADJ_MIN_X, CALIB_ADJ_MIN_Y, CALIB_ADJ_MAX_X - CALIB_ADJ_MIN_X, CALIB_ADJ_MAX_Y - CALIB_ADJ_MIN_Y);
        getGC(CANVAS_DEBUG).setFill(Color.RED.interpolate(Color.TRANSPARENT, 0.5));
        getGC(CANVAS_DEBUG).fillOval(calcFuzzyX() - 10, calcFuzzyY() - 10, 20, 20);

        getGC(CANVAS_DEBUG).setFill(Color.YELLOW.interpolate(Color.TRANSPARENT, 0.5));

        endCalib();
        getGC(CANVAS_DEBUG).fillRect(CALIB_ADJ_MIN_X, CALIB_ADJ_MIN_Y, (CALIB_ADJ_MAX_X - CALIB_ADJ_MIN_X), (CALIB_ADJ_MAX_Y - CALIB_ADJ_MIN_Y));
        getGC(CANVAS_DEBUG).setFill(Color.BLUE);
        getGC(CANVAS_DEBUG).fillOval(FUZZY_MOUSE.getX() - 2, FUZZY_MOUSE.getY() - 2, 4, 4);
    }

    private void runCountDown(GraphicsContext gc) {
        currentTick = startCountFrom - ((time() - beginTime) / 1000);
        strCountDown = currentTick + "";

        if (currentTick < 0) {
            strCountDown = "GO!";

        }
        if (currentTick < -1) {
            ready = true;
            resetCalib();
        }
        gc.setFont(fntImpact48);
        gc.setFill(Color.WHITE);
        gc.fillText(strTitle, SCREEN_WIDTH / 2 - getFontWidth(gc, strTitle) / 2, SCREEN_HEIGHT / 2);
        gc.fillText(strCountDown, SCREEN_WIDTH / 2 - getFontWidth(gc, strCountDown) / 2, SCREEN_HEIGHT / 2 + getFontHeight(gc));
    }
}
