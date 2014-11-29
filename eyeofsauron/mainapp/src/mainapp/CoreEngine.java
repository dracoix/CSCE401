/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 * @author Expiscor
 */
public class CoreEngine {
    public static final BetterPoint2D SCREEN_MOUSE = new BetterPoint2D();
    
    public static final BetterPoint2D FUZZY_MOUSE = new BetterPoint2D();
    
    public static int MASTER_FRAME_TIME = 1000/30;
    
    public static double SCREEN_WIDTH = 1920;
    public static double SCREEN_HEIGHT = 1080;
    
     public static final Group GROUP_ROOT = new Group();

    public static final Scene SCENE_SURFACE = new Scene(GROUP_ROOT, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
    public static final Canvas CANVAS_BACKGROUND_IMAGE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final Canvas CANVAS_SURFACE = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    
    public static void prepEngine()
    {
       GROUP_ROOT.getChildren().add(CANVAS_BACKGROUND_IMAGE);
        GROUP_ROOT.getChildren().add(CANVAS_SURFACE);
    }
    
}
