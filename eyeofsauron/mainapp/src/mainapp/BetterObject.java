package mainapp;

public abstract class BetterObject {

    boolean garbage;

    public boolean garbage() {
        return this.garbage;
    }

    public void destroy() {
        garbage = true;
    }

}
