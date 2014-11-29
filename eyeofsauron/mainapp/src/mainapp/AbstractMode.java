/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.canvas.Canvas;

/**
 *
 * @author Expiscor
 */
public abstract class AbstractMode {
    
    private boolean running;
    public abstract void init(Object args[]);
    
    public boolean running()
    {
        return this.running;
    }
    public void startMode()
    {
        this.running = true;
    }
    public void endMode()
    {
        this.running = false;
    }
    public abstract void tick(Canvas c);
    
}
