/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    
    public ModeMenu(AbstractMode nextMode) {
        super(nextMode);
    }
    
    @Override
    public void init(Object[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    long snap = time();
    
    @Override
    public void tick(Canvas c) {
        c.setEffect(null);
        if (time() - snap > 500) {
            snap = time();
            lastOX = (SCREEN_MOUSE.getX() + lastX) / 2;
            lastX = (lastX + SCREEN_MOUSE.getX()) / 2;
            lastOY = (SCREEN_MOUSE.getY() + lastY) / 2;
            lastY = (lastY + SCREEN_MOUSE.getY()) / 2;
            lastDeltaX = lastDeltaX + (lastOX - SCREEN_MOUSE.getX());
            lastDeltaX /= 2;
            lastDeltaY = lastDeltaY + (lastOY - SCREEN_MOUSE.getY());
            lastDeltaY /= 2;
        }
        c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
        renderObjects(c.getGraphicsContext2D());
        
    }
    
    double lastX;
    double lastY;
    double lastOX;
    double lastOY;
    
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
    
    private double lastDeltaX;
    private double lastDeltaY;
    
    private void renderTopLeft(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        
        if (lastDeltaX > 0 && lastDeltaY > 0) {
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
        if (lastDeltaX < 0 && lastDeltaY > 0) {
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
        if (lastDeltaX > 0 && lastDeltaY < 0) {
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
        if (lastDeltaX < 0 && lastDeltaY < 0) {
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
