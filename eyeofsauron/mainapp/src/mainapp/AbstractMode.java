package mainapp;

public abstract class AbstractMode {

    private boolean running;
    AbstractMode nextMode;

    public AbstractMode(AbstractMode nextMode) {
        this.nextMode = nextMode;
    }

    public boolean running() {
        return this.running;
    }

    public void startMode() {
        this.running = true;
    }

    public void endMode() {
        this.running = false;
    }

    public abstract void tick();

}
