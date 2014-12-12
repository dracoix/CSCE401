package mainapp;

import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import static mainapp.BetterUtils.time;
import static mainapp.CoreEngine.CANVAS_BACKGROUND_IMAGE;
import static mainapp.CoreEngine.CANVAS_CURSOR;
import static mainapp.CoreEngine.CANVAS_SURFACE;
import static mainapp.CoreEngine.CanvasWipe;
import static mainapp.CoreEngine.FINAL_SCORE;
import static mainapp.CoreEngine.SAVE_SCORES;
import static mainapp.CoreEngine.SCORES;
import static mainapp.CoreEngine.SCREEN_HEIGHT;
import static mainapp.CoreEngine.SCREEN_WIDTH;
import static mainapp.CoreEngine.fntImpact32;
import static mainapp.CoreEngine.fntImpact48;
import static mainapp.CoreEngine.getFontWidth;
import static mainapp.CoreEngine.getGC;
import static mainapp.CoreEngine.miniCalib.fX;
import static mainapp.CoreEngine.miniCalib.fY;
import static mainapp.CoreEngine.miniCalib.resetMiniCalib;
import static mainapp.CoreEngine.miniCalib.runMiniCalib;

public class ModeScore extends AbstractMode {

    final Image scoreBackground = new Image("BACKGROUND.png", SCREEN_WIDTH, SCREEN_HEIGHT, true, true);

    long timeToEnd = Long.MAX_VALUE;

    final String constHead = "ENTER NAME";
    final String constFoot = "SAVED!";
    double headCenter;
    double footCenter;

    final double cellWidth = SCREEN_WIDTH / 6;
    final double cellHeight = SCREEN_HEIGHT / 6;
    ArrayList<LetterCell> name = new ArrayList<>();

    int selectedCell;

    public ModeScore(AbstractMode nextMode) {
        super(nextMode);
        name.add(new LetterCell());
        name.add(new LetterCell());
        name.add(new LetterCell());
    }

    @Override
    public void tick() {
        resetMiniCalib();
        checkCells();

        renderBackground(CANVAS_BACKGROUND_IMAGE);
        renderForground(CANVAS_SURFACE);
        renderCursor(CANVAS_CURSOR);
    }

    void checkCells() {
        if (selectedCell < 3) {
            scrollers();
            timeToEnd = time() + 3000;
        } else {
            endTick();
        }
    }

    void endTick() {
        if (timeToEnd > time()) {
            return;
        } else {
            save();
            this.endMode();
        }
    }

    void save() {
        String compiled = name.get(0).getSelected()
                + name.get(1).getSelected()
                + name.get(2).getSelected();
        SCORES.add(new ScoreEntry(compiled, FINAL_SCORE));
        Collections.sort(SCORES);
        SAVE_SCORES();
        FINAL_SCORE = 0;
    }

    void scrollers() {

        if (fX > SCREEN_WIDTH * 3 / 4) {
            if (name.get(selectedCell).hover()) {
                selectedCell++;
            }
            return;
        }

        if (fY < SCREEN_HEIGHT / 2) {
            name.get(selectedCell).scrollUp();
            return;
        }

        if (fY > SCREEN_HEIGHT / 2) {
            name.get(selectedCell).scrollDown();
            return;
        }

        //if (name.get(selectedCell).hover()) {
        //selectedCell++;
        //}
    }

    private void renderBackground(Canvas c) {
        CanvasWipe(c);
        getGC(c).drawImage(scoreBackground, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void renderForground(Canvas c) {
        CanvasWipe(c);
        getGC(c).setFill(Color.WHITE);
        getGC(c).setFont(fntImpact48);
        headCenter = getFontWidth(getGC(CANVAS_SURFACE), constHead) / 2;
        footCenter = getFontWidth(getGC(CANVAS_SURFACE), constFoot) / 2;
        getGC(c).fillText(constHead, SCREEN_WIDTH / 2 - headCenter, SCREEN_HEIGHT / 3);
        renderCells(getGC(c));

        if (selectedCell >= 3) {
            getGC(c).setFill(Color.WHITE);
            getGC(c).setFont(fntImpact48);
            getGC(c).fillText(constFoot, SCREEN_WIDTH / 2 - footCenter, SCREEN_HEIGHT * 2 / 3);
        }
    }

    void renderCells(GraphicsContext gc) {
        gc.setFont(fntImpact48);
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.ORANGE);
        for (int i = 0; i < 3; i++) {
            renderCell(gc, i);
            gc.strokeRect(
                    cellWidth * i + cellWidth * 3 / 2,
                    SCREEN_HEIGHT / 2 - cellHeight / 2,
                    cellWidth, cellHeight);
        }
        gc.setFont(fntImpact48);
        gc.setFill((isHovering) ? (flash) ? Color.RED : Color.ORANGE : Color.WHITE);
        flash = !flash;
        gc.fillText(">>>", cellWidth * 5, SCREEN_HEIGHT / 2);
    }

    boolean flash;
    boolean isHovering;

    private void renderCursor(Canvas c) {
    }

    void renderCell(GraphicsContext gc, int i) {
        gc.save();
        if (selectedCell == i) {
            isHovering = (name.get(i).direction == 0);
            gc.setEffect(new Glow(100));
            gc.setFill(Color.RED.interpolate(Color.TRANSPARENT, 0.5));
            gc.fillRect(
                    cellWidth * i + cellWidth * 3 / 2,
                    SCREEN_HEIGHT / 2 - cellHeight / 2,
                    cellWidth, cellHeight);
            gc.setFill(Color.WHITE);
        }

        gc.fillText(
                name.get(i).getDisplay(),
                cellWidth * i + cellWidth * 2 - getFontWidth(gc, name.get(i).getDisplay() + "") / 2,
                SCREEN_HEIGHT / 2 + 20);

        gc.setFont(fntImpact32);
        switch (name.get(i).direction) {
            case -1:
                gc.fillText(
                        name.get(i).getDisplay(),
                        cellWidth * i + cellWidth * 2 - getFontWidth(gc, name.get(i).getDisplay() + "") / 2,
                        SCREEN_HEIGHT * 2 / 5);
                break;
            case 1:

                gc.fillText(
                        name.get(i).getDisplay(),
                        cellWidth * i + cellWidth * 2 - getFontWidth(gc, name.get(i).getDisplay() + "") / 2,
                        SCREEN_HEIGHT * 3 / 5 + 20);
                break;
        }

        gc.restore();
    }

    static class LetterCell {

        public static final char[] letters = {
            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z'
        };

        boolean locked;
        String letterSelected;
        String letterDisplayed = letters[0] + "";
        int index = 0;
        int direction = 0;

        long accumTime;

        long snap;

        boolean hover() {
            accumTime++;
            direction = 0;
            if (accumTime > 100) {
                lock();
            }
            return locked;
        }

        public void lock() {
            locked = true;
            letterSelected = letters[index] + "";
        }

        public String getDisplay() {
            return this.letterDisplayed;
        }

        private boolean delayed() {
            return snap >= time();
        }

        public void scrollUp() {
            if (!canScroll()) {
                return;
            }

            direction = -1;
            scroll();
        }

        public boolean canScroll() {
            if (locked) {
                return false;
            }
            return !delayed();
        }

        public void scroll() {
            accumTime = 0;
            index += direction;
            if (index < 0) {
                index = letters.length - 1;
            } else {
                if (index >= letters.length) {
                    index = 0;
                }
            }

            letterDisplayed = letters[index] + "";
            snap = time() + 750;
        }

        public void scrollDown() {
            if (!canScroll()) {
                return;
            }
            direction = 1;
            scroll();
        }

        public String getSelected() {
            return this.letterSelected;
        }
    }

}
