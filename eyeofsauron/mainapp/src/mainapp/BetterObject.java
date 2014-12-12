package mainapp;

public abstract class BetterObject {
    
    // Lessons learned early for JavaFX
    // Set objects to null by hand on tick()
    
    boolean garbage;

    public boolean garbage() {
        return this.garbage;
    }

    public void destroy() {
        garbage = true;
    }

}
