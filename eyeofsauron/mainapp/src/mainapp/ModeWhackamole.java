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
import static mainapp.CoreEngine.FUZZY_ADJ_MOUSE;
import static mainapp.CoreEngine.SCREEN_HEIGHT;
import static mainapp.CoreEngine.SCREEN_WIDTH;
import static mainapp.CoreRender.CANVAS_BACKGROUND;
import static mainapp.CoreRender.CANVAS_CURSOR;
import static mainapp.CoreRender.CANVAS_SURFACE;
import static mainapp.CoreRender.CanvasWipe;
import static mainapp.CoreRender.fntImpact24;
import static mainapp.CoreRender.fntImpact32;
import static mainapp.CoreRender.fntImpact48;
import static mainapp.CoreRender.getFontWidth;
import static mainapp.CoreRender.getGC;

public class ModeWhackamole extends AbstractMode {

    private final ArrayList<BetterPoint2D> molePositions = new ArrayList<>();
    private final ArrayList<Slot> slots = new ArrayList<>();
    public final BetterPoint2D fuzzyPosition = new BetterPoint2D();
    private final Hammer myHammer = new Hammer(fuzzyPosition);

    final ImageView fakeView = new ImageView();
    final Image imgHammer = new Image("Mallet.png");
    final Image imgBackground = new Image("BG_Lava.png", SCREEN_WIDTH, SCREEN_HEIGHT, false, true);
    WritableImage renderedHammer = new WritableImage(560, 560);
    final Image[] imgsHole = new Image[9];
    final Image[] imgsMole = new Image[18];

    private int slotsX = 2;
    private int slotsY = 1;
    private int level = 1;

    private int livesLeft = 5;

    boolean isGameOver;

    private final double senseRange = rms(SCREEN_HEIGHT, SCREEN_WIDTH) / 8;

    long MAX_DELAY = 1000;
    int MAX_MOLES = 1;

    double fadeAnnounce;
    String strAnnounce = "";

    long pauseSnap = time();

    SnapshotParameters cacheSP = new SnapshotParameters();
    boolean hammerIsAligned;

    public ModeWhackamole(AbstractMode nextMode) {
        super(nextMode);

        fakeView.setBlendMode(BlendMode.SCREEN);
        fakeView.setFitWidth(473.36);
        fakeView.setFitHeight(300);
        fakeView.setImage(imgHammer);

        for (int i = 0; i < 9; i++) {
            imgsMole[i] = new Image("Hole" + 7 + "x.png",
                    ((i + 1) / (double) 10) * SCREEN_WIDTH / 4,
                    ((i + 1) / (double) 10) * SCREEN_WIDTH / 4, true, true);
        }
        for (int i = 9; i < 18; i++) {
            imgsMole[i] = new Image("Mole" + (i - 9) + "x.png", SCREEN_WIDTH / 4, SCREEN_HEIGHT / 4, true, true);
        }

    }

    @Override
    public void startMode() {
        setupGame();
        showAnnounce("LEVEL 1...", 5);
        super.startMode();
    }

    public void nextLevel() {
        level++;

        slotsX += ((level % 2) == 1) ? 1 : 0;
        slotsY += ((level % 2) == 1) ? 0 : 1;

        if (level >= 4) {
            slotsX = 3;
            slotsY = 3;
        }

        showAnnounce("LEVEL " + level, 5);
        setupGame();
    }

    public void showAnnounce(String s, int t) {
        fadeAnnounce = 1.0;
        strAnnounce = "" + s;
        pause(t);
    }

    public void pause(int secs) {
        pauseSnap = time() + (secs * 1000);
    }

    public boolean isPaused() {
        return time() - pauseSnap < 0;
    }

    public void SetDifficulty(int level) {
        MAX_MOLES = level;
        MAX_DELAY = (int) (5000 / Math.log1p(level));
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
        slots.stream().forEach((s) -> {
            s.destroy();
        });
        slots.clear();
    }

    private double calcOffsetX(double y) {
        return SCREEN_WIDTH / 2 - (calcWidthAtY(y)) / 2;
    }

    private double calcWidthAtY(double y) {
        return SCREEN_WIDTH / 2 + (SCREEN_WIDTH / 2) * (y / SCREEN_HEIGHT);
    }

    @Override
    public void tick() {

        if (isGameOver) {
            endGame();
        }
        if (!isPaused()) {
            updateObjects();
        } else {
            fuzzyPosition.MidpointAndSet(FUZZY_ADJ_MOUSE);
            updateHammer();
        }

        renderBackgroundCanvas(CANVAS_BACKGROUND);
        renderForgroundCanvas(CANVAS_SURFACE);
        renderCursorCanvas(CANVAS_CURSOR);

    }

    private void GameOver() {
        pause(5);
        fadeAnnounce = 1;
        strAnnounce = "GAME OVER \nSCORE: " + CoreScores.getCurrentScore();
        isGameOver = true;
        endGame();
    }

    public void endGame() {
        if (!isPaused()) {
            this.endMode();
        }
    }

    private void updateObjects() {
        updateHammer();

        hammerIsAligned = false;
        for (Slot s : slots) {
            if (s.filled()) {
                if (FUZZY_ADJ_MOUSE.collides(s.position, senseRange)) {
                    fuzzyPosition.MidpointAndSet(s.position);
                    myHammer.setTarget(s.childMole);
                    hammerIsAligned = true;
                }
            }
            s.tick();
        }

        if (!hammerIsAligned) {
            myHammer.setTarget(null);
            fuzzyPosition.MidpointAndSet(FUZZY_ADJ_MOUSE);
        }
        if (livesLeft == 0) {
            GameOver();
        }

    }

    private void updateHammer() {

        myHammer.tick();
        if (myHammer.isReadyToWhack()) {
            myHammer.startWhacking();
            if (myHammer.whackTarget()) {
                CoreScores.modifyScoreBy(1);
                if (CoreScores.getCurrentScore() > (double) Math.pow(level, 2)) {
                    nextLevel();
                }
            }
        }
    }

    private void updateMole(Mole m) {
        if (m == null) {
            return;
        }
        if (m.garbage()) {
            return;
        }
        m.tick();
    }

    private void renderDepthField(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLUE);
        //getGC(c).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (int x = 0; x <= slotsX; x++) {
            gc.strokeLine(
                    x * (SCREEN_WIDTH / slotsX),
                    SCREEN_HEIGHT,
                    x * (SCREEN_WIDTH / slotsX) / 2 + (SCREEN_WIDTH - (SCREEN_WIDTH / 2)) / 2,
                    0);
        }

        for (int y = 0; y <= slotsY; y++) {
            gc.strokeLine(
                    calcOffsetX(y * (SCREEN_HEIGHT / slotsY)),
                    y * (SCREEN_HEIGHT / slotsY),
                    calcOffsetX(y * (SCREEN_HEIGHT / slotsY)) + calcWidthAtY(y * (SCREEN_HEIGHT / slotsY)),
                    y * (SCREEN_HEIGHT / slotsY));
        }
        gc.strokeRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void renderBackgroundCanvas(Canvas c) {
        CanvasWipe(c);
        getGC(c).drawImage(imgBackground, 0, 0);
    }

    private void renderMoles(GraphicsContext gc) {
        for (Slot s : slots) {
            if (!s.filled()) {
                continue;
            }
            gc.drawImage(s.childMole.getCurrentFrame(),
                    s.position.getX()
                    - s.slotWidth * s.childMole.centerW(),
                    s.position.getY()
                    - s.slotWidth * s.childMole.centerH(),
                    s.slotWidth * s.childMole.width(),
                    s.slotWidth * s.childMole.height());
        }
    }

    private void renderShadow(GraphicsContext gc) {

        gc.setFill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));

        gc.fillOval(fuzzyPosition.getX() - calcWidthAtY(fuzzyPosition.getY()) / 10,
                fuzzyPosition.getY() - calcWidthAtY(fuzzyPosition.getY()) / 20,
                calcWidthAtY(fuzzyPosition.getY()) / 5,
                calcWidthAtY(fuzzyPosition.getY()) / 10);

    }

    private void renderHammer(GraphicsContext gc) {
        fakeView.setFitHeight(calcWidthAtY(fuzzyPosition.getY()) / 4);
        fakeView.setFitWidth(calcWidthAtY(fuzzyPosition.getY()) / 4);
        fakeView.setRotate(Math.abs(60 - myHammer.angle));
        fakeView.setSmooth(true);
        renderedHammer = new WritableImage(900, 900);
        cacheSP.setFill(Color.TRANSPARENT);
        fakeView.snapshot(cacheSP, renderedHammer);
        gc.drawImage(renderedHammer,
                fuzzyPosition.getX() - (calcWidthAtY(fuzzyPosition.getY()) / 24),
                fuzzyPosition.getY() - (calcWidthAtY(fuzzyPosition.getY()) / 4));
    }

    private void renderGameText(GraphicsContext gc) {
        gc.setFill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.5));
        gc.setStroke(Color.RED);

        gc.fillRect(16, 16, 200, 96);
        gc.fillRect(SCREEN_WIDTH - (256 + 16), 16, 256, 48);

        gc.strokeRect(16, 16, 200, 96);
        gc.strokeRect(SCREEN_WIDTH - (256 + 16), 16, 256, 48);

        gc.setFont(fntImpact32);
        gc.setFill(Color.WHITE);
        gc.fillText("SCORE: " + CoreScores.getCurrentScore(), 20, 48);
        gc.fillText("LEVEL: " + level, 20, 96);
        gc.fillText("HAMMERS LEFT: " + livesLeft, SCREEN_WIDTH - 256, 48);

    }

    private void renderAnnounce(GraphicsContext gc) {
        if (strAnnounce.equals("") || !isPaused()) {
            return;
        }
        gc.setFont(fntImpact48);
        gc.setFill(Color.WHITE.interpolate(Color.TRANSPARENT, fadeAnnounce));
        gc.fillText(strAnnounce,
                SCREEN_WIDTH / 2 - getFontWidth(gc, strAnnounce) / 2,
                SCREEN_HEIGHT / 2);
        gc.setStroke(Color.BLACK.interpolate(Color.TRANSPARENT, fadeAnnounce));
        gc.strokeText(strAnnounce,
                SCREEN_WIDTH / 2 - getFontWidth(gc, strAnnounce) / 2,
                SCREEN_HEIGHT / 2);
        fadeAnnounce /= 1.1;
    }

    private void renderForgroundCanvas(Canvas c) {
        CanvasWipe(c);
        renderForgroundGC(getGC(c));
    }

    private void renderForgroundGC(GraphicsContext gc) {
        //renderDepthField(gc);
        renderGameText(gc);
        renderMoles(gc);
        renderShadow(gc);
        renderAnnounce(gc);
    }

    private void renderCursorCanvas(Canvas c) {
        CanvasWipe(c);
        renderHammer(getGC(c));
    }

    class Slot extends BetterObject {

        BetterPoint2D position = new BetterPoint2D();
        double slotWidth;
        Mole childMole;

        public Slot(double x, double y) {
            this.position.set(x, y);
            slotWidth = (0.5 + (y / (2 * SCREEN_HEIGHT)));
        }

        public boolean filled() {
            if (childMole == null) {
                return false;
            } else {
                if (childMole.garbage()) {
                    childMole = null;
                    return false;
                }
            }
            return true;
        }

        public void createMole() {
            childMole = new Mole(position);
        }

        @Override
        public void destroy() {
            super.destroy();
            if (childMole != null) {
                childMole.destroy();
            }
        }

        public void tick() {

            if (filled()) {
                updateMole(childMole);
            } else {
                if (BetterUtils.Random.nextDouble() > 0.992) {
                    createMole();
                }
            }

        }
    }

    class Hammer extends RenderObject {

        Mole target;
        long hoverTimeRequired = 5;
        long hoverTimeAccumulated;
        double angle = 0;
        boolean isWhacking;

        public Hammer(BetterPoint2D sync) {
            this.setCurrentFrame(imgHammer);
        }

        public void setHoverTimeRequired(long ms) {
            this.hoverTimeRequired = ms;
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

        public void startWhacking() {
            isWhacking = true;
        }

        @Override
        public void tick() {
            super.tick();
            isWhacking();
        }

        void isWhacking() {
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

    class Mole extends RenderObject {

        boolean whacked;
        boolean hide;
        long timeInit = time();
        long timeUntilHide;
        long timeUntilDeath = Long.MAX_VALUE;

        public Mole(BetterPoint2D sync) {
            this.syncPosition(sync);
            timeUntilHide = (long) (time() + MAX_DELAY + (BetterUtils.Random.nextDouble() * MAX_DELAY));
        }

        public void whackMe() {
            whacked = true;
            hide = false;
            timeUntilHide = Long.MAX_VALUE;
            timeUntilDeath = time() + 1000;
            this.setCurrentFrame(imgsMole[9]);
            tick();
        }

        @Override
        public void tick() {
            super.tick();
            if (time() > timeUntilDeath) {
                this.destroy();
                return;
            }

            if (time() > timeUntilHide) {
                this.hide = true;
                if (livesLeft > 0) {
                    livesLeft--;
                }

                this.destroy();
                return;
            }

            if (!this.whacked) {
                this.setCurrentFrame(
                        imgsMole[(int) Math.abs(
                                Math.sin(Math.PI * (timeUntilHide - time()) / (timeUntilHide - timeInit)) * 17)]
                );
            } else {
                this.setCurrentFrame(imgsMole[9]);
            }

        }

    }

}
