package mainapp;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DriverEngine {

    private Timeline MAINLOOP;
    AbstractMode CurrentMode = new ModeMenu(null);

    public DriverEngine() {

        CoreEngine.prepEngine();
    }

    void init(Stage primaryStage) {

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        CoreRender.SCENE_SURFACE.setCursor(Cursor.NONE);
        primaryStage.setScene(CoreRender.SCENE_SURFACE);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DriverEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        final KeyFrame oneFrame;
        oneFrame = new KeyFrame(Duration.millis(CoreRender.MASTER_FRAME_TIME), (ActionEvent event) -> {
            tick();
        });

        MAINLOOP = new Timeline();
        MAINLOOP.setCycleCount(Animation.INDEFINITE);
        MAINLOOP.getKeyFrames().add(oneFrame);
    }

    public void tick() {

        CoreEngine.FUZZY_MOUSE.MidpointAndSet(CoreEngine.SCREEN_MOUSE);
        CoreEngine.tickFuzzyAdj();
        if (CurrentMode.running()) {
            CurrentMode.tick();
        } else {
            CoreRender.FullReset();
            CurrentMode = CurrentMode.nextMode;
            CurrentMode.startMode();
        }
    }

    public void fire() {

        CurrentMode.startMode();

        MAINLOOP.play();
        System.out.println("Fired...");
    }
}
