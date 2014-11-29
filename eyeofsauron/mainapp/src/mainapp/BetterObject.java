/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

/**
 *
 * @author Expiscor
 */
public abstract class BetterObject {

    boolean garbage;
    
    //public abstract void init(Object args[]);

    public boolean garbage() {
        return this.garbage;
    }

    public void destroy() {
        garbage = true;
    }
    
}
