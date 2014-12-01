/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import static mainapp.CoreEngine.CANVAS_SURFACE;
import static mainapp.CoreEngine.MASTER_FRAME_TIME;
import static mainapp.CoreEngine.SCENE_SURFACE;

/**
 *
 * @author David 'dracoix' Rathbun
 */
public class DriverEngine {

    private Timeline MAINLOOP;
    AbstractMode CurrentMode = new ModeMenu(null);

    public DriverEngine() {

        CoreEngine.prepEngine();
    }

    void init(Stage primaryStage) {

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        SCENE_SURFACE.setCursor(Cursor.NONE);
        primaryStage.setScene(SCENE_SURFACE);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DriverEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        final KeyFrame oneFrame;
        oneFrame = new KeyFrame(Duration.millis(MASTER_FRAME_TIME), (ActionEvent event) -> {
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
            CurrentMode.tick(CANVAS_SURFACE);
        } else {
            CurrentMode = CurrentMode.nextMode;
        }

    }

    public void fire() {

        CurrentMode.startMode();

        MAINLOOP.play();
        System.out.println("Fired...");
    }
}
