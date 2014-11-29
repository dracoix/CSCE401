/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.canvas.Canvas;
import static mainapp.BetterUtils.time;
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

    public final BetterPoint2D fuzzyPosition = new BetterPoint2D();

    private int slotsX = 1;
    private int slotsY = 1;
    private int difficulty;
    private int level;
    private int hits;
    private int totalMoles;
    private int score;

    private double senseRange = 20;

    long MAX_DELAY = 1000;
    int MAX_MOLES = 1;

    public void SetDifficulty(int level) {
        MAX_MOLES = level;
        MAX_DELAY = (int) (1000 / Math.log1p(level));
    }

    @Override
    public void init(Object args[]) {
        if (args != null) {
            difficulty = (int) args[0];
        }
    }

    public void setupGame() {
        double tmpY, hY;
        double tmpX, wX;
        clearSlots();
        hY = SCREEN_HEIGHT / ((slotsY * 2));
        for (int y = 0; y < slotsY; y++) {
            tmpY = hY + hY * y;
            wX = calcOffsetWidth(tmpY) / (slotsX * 2);
            for (int x = 0; x < slotsX; x++) {
                tmpX = calcOffsetX(tmpY) + wX * x;
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
        return (SCREEN_WIDTH / 4) * (y / SCREEN_HEIGHT);
    }

    private double calcOffsetWidth(double y) {
        return SCREEN_WIDTH - (SCREEN_WIDTH / 2 + (SCREEN_WIDTH / 2) * (y / SCREEN_HEIGHT));
    }

    public void levelUp() {
        level++;
    }

    public void endGame() {
        this.endMode();
    }

    @Override
    public void tick(Canvas c) {

        updateObjects();
        c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
        renderBackground(c);
        renderMoles(c);
        renderShadow(c);
        renderHammer(c);
    }

    private void updateObjects() {

        if (myHammer.isReadyToWhack()) {
            if (myHammer.whackTarget()) {
                score++;
            }
            myHammer.tick(true);
        } else {
            myHammer.tick(false);
        }

        for (Slot s : slots) {
            if (SCREEN_MOUSE.collides(s.position, senseRange)) {
                fuzzyPosition.MidpointAndSet(s.position);
                myHammer.setTarget(s.childMole);
            }
            s.tick();
        }

    }

    private void updateMole(Mole m) {
        if (m.garbage()) {
            return;
        }
        m.tick(true);
    }

    private void renderBackground(Canvas c) {

    }

    private BetterPoint2D cacheMoleRenderPos;

    private void renderMoles(Canvas c) {

        for (Slot s : slots) {
            cacheMoleRenderPos = s.childMole.getRenderPosition();
            c.getGraphicsContext2D().drawImage(
                    s.childMole.getCurrentFrame(),
                    cacheMoleRenderPos.getX(),
                    cacheMoleRenderPos.getY());
        }

    }

    private void renderShadow(Canvas c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void renderHammer(Canvas c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            if (childMole.garbage()) {
                childMole = null;
            }
            if (childMole == null) {
                this.filled = false;
            } else {
                updateMole(childMole);
            }
        }
    }

    class Hammer extends AnimatedObject {

        Mole target;
        long hoverTimeRequired;
        long hoverTimeAccumulated;

        BetterPoint2D position = new BetterPoint2D();

        public Hammer() {

        }

        public void setHoverTimeRequired(long ms) {
            this.hoverTimeRequired = ms;
        }

        public BetterPoint2D getPosition() {
            return this.position;
        }

        public void setTarget(Mole m) {

            if (m.equals(target)) {

            } else {
                hoverTimeAccumulated = 0;
                target = m;
            }
        }

        @Override
        public void tick(boolean proceed) {
            super.tick(proceed);
            this.position.set(fuzzyPosition.getX(), fuzzyPosition.getY());

        }

        public boolean whackTarget() {
            if (target != null) {
                this.target.whackMe();
                return true;
            }
            return false;
        }

        public boolean isReadyToWhack() {
            hoverTimeAccumulated++;
            return hoverTimeAccumulated >= hoverTimeRequired;
        }

    }

    class Mole extends AnimatedObject {

        boolean whacked;
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
            timeUntilHide = (long) (MAX_DELAY + (BetterUtils.Random.nextDouble() * MAX_DELAY));
        }

        public void whackMe() {
            whacked = true;
            timeUntilHide = Long.MAX_VALUE;
            timeUntilDeath = time() + 5000;
            tick(true);
        }

        @Override
        public void tick(boolean nextAnimation) {
            if (time() > timeUntilDeath) {
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
