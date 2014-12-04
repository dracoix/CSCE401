/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.util.ArrayList;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import static mainapp.BetterMath.rms;
import static mainapp.BetterUtils.time;
import static mainapp.CoreEngine.CANVAS_BACKGROUND_IMAGE;
import static mainapp.CoreEngine.CANVAS_CURSOR;
import static mainapp.CoreEngine.CANVAS_SURFACE;
import static mainapp.CoreEngine.CanvasWipe;
import static mainapp.CoreEngine.FUZZY_ADJ_MOUSE;
import static mainapp.CoreEngine.SCREEN_HEIGHT;
import static mainapp.CoreEngine.SCREEN_MOUSE;
import static mainapp.CoreEngine.SCREEN_WIDTH;

/**
 *
 * @author Expiscor
 */
public class ModeWhackamole extends AbstractMode {

    private final ArrayList<BetterPoint2D> molePositions = new ArrayList<>();
    private final ArrayList<Slot> slots = new ArrayList<>();
    private final Hammer myHammer = new Hammer();

    final ImageView fakeView = new ImageView();
    Image imgHammer = new Image("Mallet.png");
    final Image background = new Image("bg_template_font.png", SCREEN_WIDTH, SCREEN_HEIGHT, false, true);
    //Image imgBackground = new Image("")
    WritableImage renderedHammer = new WritableImage(560, 560);

    public final BetterPoint2D fuzzyPosition = new BetterPoint2D();

    private int slotsX = 2;
    private int slotsY = 1;
    private int level = 1;
    private int score;
    private int livesLeft = 5;

    boolean isGameOver;

    private double senseRange = rms(SCREEN_HEIGHT, SCREEN_WIDTH) / 8;

    long MAX_DELAY = 1000;
    int MAX_MOLES = 1;

    public ModeWhackamole(AbstractMode nextMode) {
        super(nextMode);
        
        fakeView.setBlendMode(BlendMode.SCREEN);
        fakeView.setFitWidth(473.36);
        fakeView.setFitHeight(300);
        fakeView.setImage(imgHammer);
        setupGame();

    }

    public void nextLevel() {
        level++;

        slotsX += ((level % 2) == 1) ? 1 : 0;
        slotsY += ((level % 2) == 1) ? 0 : 1;

        if (level >= 4) {
            slotsX = 3;
            slotsY = 3;
        }

        showAnnounce("Level " + level, 5);
        
        setupGame();
        
    }

    public void showAnnounce(String s, int t) {
        fadeAnnounce = 1.0;
        strAnnounce = "" + s;
        pauseSnap = time() + t * 1000;
    }

    double fadeAnnounce;

    String strAnnounce = "";

    long pauseSnap = time();

    public boolean isPaused() {
        return time() - pauseSnap < 0;
    }

    public void SetDifficulty(int level) {
        MAX_MOLES = level;
        MAX_DELAY = (int) (3000 / Math.log1p(level));
    }

    @Override
    public void init(Object args[]) {
        if (args != null) {

        }
    }

    public void setupGame() {
        SetDifficulty(level);
        double tmpY, hY;
        double tmpX, wX;
        clearSlots();
        hY = SCREEN_HEIGHT / ((slotsY));
        for (int y = 0; y < slotsY; y++) {
            tmpY = hY / 2 + hY * y;
            wX = calcWidthAtY(tmpY) / (slotsX);
            for (int x = 0; x < slotsX; x++) {
                tmpX = calcOffsetX(tmpY) + wX * x + wX / 2;
                slots.add(new Slot(tmpX, tmpY));
            }
        }
    }

    public void clearSlots() {
        for (Slot s : slots) {
            s.destroy();
        }
        slots.clear();
    }

    private double calcOffsetX(double y) {
        return SCREEN_WIDTH / 2 - (calcWidthAtY(y)) / 2;
    }

    private double calcWidthAtY(double y) {
        return SCREEN_WIDTH / 2 + (SCREEN_WIDTH / 2) * (y / SCREEN_HEIGHT);
    }

    public void endGame() {
        isGameOver = true;
        if (!isPaused()) {
            this.endMode();
        }
    }

    @Override
    public void tick() {

        if (!isPaused() || !isGameOver) {
            updateObjects();
        }

        CanvasWipe(CANVAS_BACKGROUND_IMAGE);
        getGC(CANVAS_BACKGROUND_IMAGE).drawImage(background, 0, 0);
        CANVAS_SURFACE.setBlendMode(BlendMode.SRC_OVER);
        CANVAS_SURFACE.setEffect(null);
        CanvasWipe(CANVAS_SURFACE);
        renderDepthField(CANVAS_SURFACE);
        renderBackground(CANVAS_SURFACE);
        renderGameText(CANVAS_SURFACE);
        renderMoles(CANVAS_SURFACE);
        renderShadow(CANVAS_SURFACE);
        CANVAS_CURSOR.setBlendMode(BlendMode.SRC_OVER);
        CANVAS_CURSOR.setEffect(null);
        CanvasWipe(CANVAS_CURSOR);
        renderHammer(CANVAS_CURSOR);
        renderAnnounce(CANVAS_SURFACE);
    }

    boolean allEmpty;

    private void updateHammer() {
        if (myHammer.isReadyToWhack()) {
            myHammer.tick(true);
            if (myHammer.whackTarget()) {
                score++;
                if (score > (double) Math.pow(level, 1.7)) {
                    nextLevel();
                    return;
                }
            }
        } else {
            myHammer.tick(false);
        }
    }

    private void updateObjects() {

        updateHammer();

        allEmpty = true;
        for (Slot s : slots) {
            if (s.isPopped()) {
                allEmpty = false;
            }
            if (FUZZY_ADJ_MOUSE.collides(s.position, senseRange)) {
                fuzzyPosition.MidpointAndSet(s.position);
                myHammer.setTarget(s.childMole);
            }
            s.tick();
            if (s.hasDug()) {
                if (livesLeft > 0) {
                    livesLeft--;
                } else {
                    GameOver();
                    return;
                }
            }
        }

        if (allEmpty) {
            fuzzyPosition.MidpointAndSet(FUZZY_ADJ_MOUSE);
        }

    }

    private void updateMole(Mole m) {
        if (m == null) {
            return;
        }
        if (m.garbage()) {
            return;
        }
        m.tick(true);
    }

    private void renderDepthField(Canvas c) {
        getGC(c).setFill(Color.WHITE);
        getGC(c).setStroke(Color.BLUE);
        //getGC(c).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (int x = 0; x <= slotsX; x++) {
            getGC(c).strokeLine(
                    x * (SCREEN_WIDTH / slotsX),
                    SCREEN_HEIGHT,
                    x * (SCREEN_WIDTH / slotsX) / 2 + (SCREEN_WIDTH - (SCREEN_WIDTH / 2)) / 2,
                    0);
        }

        for (int y = 0; y <= slotsY; y++) {
            getGC(c).strokeLine(
                    calcOffsetX(y * (SCREEN_HEIGHT / slotsY)),
                    y * (SCREEN_HEIGHT / slotsY),
                    calcOffsetX(y * (SCREEN_HEIGHT / slotsY)) + calcWidthAtY(y * (SCREEN_HEIGHT / slotsY)),
                    y * (SCREEN_HEIGHT / slotsY));
        }

        getGC(c).strokeRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    }

    private void renderBackground(Canvas c) {

        //scale = SCREEN_WIDTH - ((SCREEN_HEIGHT - calcY()) / SCREEN_HEIGHT) * (SCREEN_WIDTH - (SCREEN_WIDTH * ratio_depth));
    }

    private GraphicsContext getGC(Canvas c) {
        return c.getGraphicsContext2D();
    }

    private void renderMoles(Canvas c) {

        for (Slot s : slots) {
            //cacheMoleRenderPos = s.childMole.getRenderPosition();
            if (s.filled) {
                if (s.childMole == null) {
                    continue;
                }
                getGC(c).setFill((s.isWhacked()) ? Color.BLACK : (s.hasDug()) ? Color.RED : Color.ORANGE);

                getGC(c).fillOval(s.position.getX() - 20, s.position.getY() - 20, 40, 40);

            }
            //c.getGraphicsContext2D().drawImage(
            //        s.childMole.getCurrentFrame(),
            //        cacheMoleRenderPos.getX(),
            //        cacheMoleRenderPos.getY());
        }

    }

    private void renderShadow(Canvas c) {

        getGC(c).setFill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));

        getGC(c).fillOval(fuzzyPosition.getX() - calcWidthAtY(fuzzyPosition.getY()) / 10,
                fuzzyPosition.getY() - calcWidthAtY(fuzzyPosition.getY()) / 20,
                calcWidthAtY(fuzzyPosition.getY()) / 5,
                calcWidthAtY(fuzzyPosition.getY()) / 10);

    }

    SnapshotParameters cacheSP = new SnapshotParameters();

    private void renderHammer(Canvas c) {
        fakeView.setFitHeight(calcWidthAtY(fuzzyPosition.getY()) / 4);
        fakeView.setFitWidth(calcWidthAtY(fuzzyPosition.getY()) / 4);
        //fakeView.setImage(imgHammer);
        fakeView.setRotate(Math.abs(60 - myHammer.angle));
        fakeView.setSmooth(true);
        renderedHammer = new WritableImage(900, 900);
        cacheSP.setFill(Color.TRANSPARENT);
        fakeView.snapshot(cacheSP, renderedHammer);

        getGC(c).drawImage(renderedHammer,
                fuzzyPosition.getX() - (calcWidthAtY(fuzzyPosition.getY()) / 24),
                fuzzyPosition.getY() - (calcWidthAtY(fuzzyPosition.getY()) / 4));
    }

    private void renderGameText(Canvas c) {
        getGC(c).setFont(CoreEngine.fntImpact24);
        getGC(c).setFill(Color.WHITE);
        getGC(c).fillText("SCORE: " + score, 4, 32);
        getGC(c).fillText("LEVEL: " + level, 4, 64);
        getGC(c).fillText("HAMMERS LEFT: " + livesLeft, SCREEN_WIDTH - 256, 32);
    }

    private void GameOver() {
        pauseSnap = time() + 5000;
        fadeAnnounce = 1;
        strAnnounce = "GAME OVER";
        endGame();
    }

    private void renderAnnounce(Canvas c) {
        if (strAnnounce.equals("") || !isPaused()) {
            return;
        }
        getGC(c).setFont(CoreEngine.fntImpact48);
        getGC(c).setFill(Color.WHITE.interpolate(Color.TRANSPARENT, fadeAnnounce));
        getGC(c).fillText(strAnnounce, SCREEN_WIDTH / 2 - CoreEngine.getFontWidth(getGC(c), strAnnounce) / 2, SCREEN_HEIGHT / 2);
        getGC(c).setStroke(Color.BLACK.interpolate(Color.TRANSPARENT, fadeAnnounce));
        getGC(c).strokeText(strAnnounce, SCREEN_WIDTH / 2 - CoreEngine.getFontWidth(getGC(c), strAnnounce) / 2, SCREEN_HEIGHT / 2);
        fadeAnnounce /= 1.1;
    }

    class Slot extends BetterObject {

        boolean filled;
        BetterPoint2D position = new BetterPoint2D();

        Mole childMole;

        public Slot(double x, double y) {
            this.position.set(x, y);
        }

        public void tryCreateMole() {
            if (this.filled) {
                return;
            }
            childMole = new Mole(position);
            this.filled = true;
        }

        @Override
        public void destroy() {
            super.destroy();
            if (childMole != null) {
                childMole.destroy();
            }
        }

        public void tick() {

            if (BetterUtils.Random.nextDouble() > 0.992) {
                tryCreateMole();
            }

            if (childMole == null) {
                this.filled = false;
            } else {
                if (childMole.garbage()) {
                    childMole = null;
                } else {
                    updateMole(childMole);
                }
            }
        }

        private boolean isPopped() {
            if (childMole == null) {
                return false;
            }
            if (childMole.whacked) {
                return false;
            }
            return this.filled;
        }

        private boolean isWhacked() {
            if (this.childMole != null) {
                return this.childMole.whacked;
            }
            return false;

        }

        private boolean hasDug() {
            if (this.childMole != null) {
                return this.childMole.hide;
            }
            return false;
        }
    }

    class Hammer extends AnimatedObject {

        Mole target;
        long hoverTimeRequired = 5;
        long hoverTimeAccumulated;
        double angle = 0;
        boolean isWhacking;

        BetterPoint2D position = new BetterPoint2D();

        public Hammer() {
            this.curFrame = imgHammer;
        }

        public void setHoverTimeRequired(long ms) {
            this.hoverTimeRequired = ms;
        }

        public BetterPoint2D getPosition() {
            return this.position;
        }

        public void setTarget(Mole m) {
            if (m == null) {
                return;
            }
            if (m.equals(target)) {

            } else {
                hoverTimeAccumulated = 0;
                target = m;
            }
        }

        @Override
        public void tick(boolean proceed) {
            super.tick(proceed);
            isWhacking(proceed);

        }

        void isWhacking(boolean proceed) {
            if (proceed & angle == 0) {
                isWhacking = true;
            }

            if (isWhacking) {
                angle *= 1.5;
                angle += 1;
            }

            if (angle >= 150) {
                angle = 0;
                isWhacking = false;
            }
        }

        public boolean whackTarget() {
            if (target != null) {
                this.hoverTimeAccumulated = 0;
                this.target.whackMe();
                return true;
            }
            return false;
        }

        public boolean isReadyToWhack() {
            if (this.target == null) {
                return false;
            }
            if (this.target.whacked == true) {
                return false;
            }
            hoverTimeAccumulated++;
            return hoverTimeAccumulated >= hoverTimeRequired;
        }

    }

    class Mole extends AnimatedObject {

        boolean whacked;
        boolean hide;
        long timeUntilHide;
        long timeUntilDeath = Long.MAX_VALUE;

        BetterPoint2D position;
        BetterPoint2D renderPosition = new BetterPoint2D();

        public BetterPoint2D getRenderPosition() {
            renderPosition.set(this.position.getX() - this.curFrame.getWidth() / 2, this.position.getY() - this.curFrame.getHeight() / 2);
            return renderPosition;
        }

        public BetterPoint2D getPosition() {
            return this.position;
        }

        public Mole(BetterPoint2D sync) {
            this.position = sync;
            timeUntilHide = (long) (time() + MAX_DELAY + (BetterUtils.Random.nextDouble() * MAX_DELAY));
        }

        public void whackMe() {
            whacked = true;
            hide = false;
            timeUntilHide = Long.MAX_VALUE;
            timeUntilDeath = time() + 1000;
            tick(true);
        }

        @Override
        public void tick(boolean nextAnimation) {
            if (time() > timeUntilDeath) {
                this.destroy();
                return;
            }

            if (time() > timeUntilHide) {
                this.hide = true;
                this.destroy();
                return;
            }

            super.tick(nextAnimation);

        }

        @Override
        public void destroy() {
            super.destroy();
            this.renderPosition = null;
        }

    }

}
