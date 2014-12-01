/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static mainapp.BetterMath.rms;
import static mainapp.BetterUtils.time;
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
    
    public final BetterPoint2D fuzzyPosition = new BetterPoint2D();
    
    private int slotsX = 1;
    private int slotsY = 1;
    private int difficulty;
    private int level;
    private int hits;
    private int totalMoles;
    private int score;
    
    private double senseRange = rms(SCREEN_HEIGHT, SCREEN_WIDTH) / 8;
    
    long MAX_DELAY = 1000;
    int MAX_MOLES = 1;
    
    public ModeWhackamole(AbstractMode nextMode) {
        super(nextMode);
        setupGame();
    }
    
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
        renderDepthField(c);
        renderBackground(c);
        renderScore(c);
        renderMoles(c);
        renderShadow(c);
        renderHammer(c);
    }
    
    boolean allEmpty;
    
    private void updateObjects() {
        
        if (myHammer.isReadyToWhack()) {
            if (myHammer.whackTarget()) {
                score++;
            }
            myHammer.tick(true);
        } else {
            myHammer.tick(false);
        }
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
        getGC(c).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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
    
    private BetterPoint2D cacheMoleRenderPos;
    
    private void renderMoles(Canvas c) {
        
        for (Slot s : slots) {
            //cacheMoleRenderPos = s.childMole.getRenderPosition();
            if (s.filled) {
                if (s.childMole == null) {
                    continue;
                }
                getGC(c).setFill((s.childMole.whacked) ? Color.BLACK : Color.RED);
                
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
        
        getGC(c).fillOval(fuzzyPosition.getX() - calcWidthAtY(fuzzyPosition.getY()) / 8, fuzzyPosition.getY() - calcWidthAtY(fuzzyPosition.getY()) / 16, calcWidthAtY(fuzzyPosition.getY()) / 4, calcWidthAtY(fuzzyPosition.getY()) / 8);
        
    }
    
    private void renderHammer(Canvas c) {
        
    }
    
    private void renderScore(Canvas c) {
        getGC(c).setFont(CoreEngine.fntImpact24);
        getGC(c).setFill(Color.BLACK);
        getGC(c).fillText(score + "", 4, 32);
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
            
            if (BetterUtils.Random.nextDouble() > 0.995) {
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
          if (childMole == null) return false;
          if (childMole.whacked) return false;
          return this.filled;
        }
    }
    
    class Hammer extends AnimatedObject {
        
        Mole target;
        long hoverTimeRequired = 5;
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
            this.position.set(fuzzyPosition.getX(), fuzzyPosition.getY());
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
            timeUntilDeath = time() + 1000;
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
