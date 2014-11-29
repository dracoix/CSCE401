/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static mainapp.BetterUtils.Random;

/**
 *
 * @author Expiscor
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DriverEngine ENGINE = new DriverEngine();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //primaryStage.setWidth(SCREEN_WIDTH);
        //primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);

        primaryStage.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent t) -> {
            CoreEngine.SCREEN_MOUSE.set(t.getSceneX(), t.getSceneY());
        });
//        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent t) -> {
//            CoreEngine.WINDOW_X = t.getSceneX();
//            CoreEngine.WINDOW_Y = t.getSceneY();
//            CoreEngine.MOUSE_DOWN = true;
//        });

//        primaryStage.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent t) -> {
//            CoreEngine.MOUSE_DOWN = false;
//        });
//        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent t) -> {
//            //primaryStage.setX(t.getScreenX() - WINDOW_X);
//            //primaryStage.setY(t.getScreenY() - WINDOW_Y);
//        });
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        ENGINE.init(primaryStage);
        primaryStage.show();
        ENGINE.fire();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
