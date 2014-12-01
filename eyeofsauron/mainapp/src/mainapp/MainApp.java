/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Expiscor
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DriverEngine ENGINE = new DriverEngine();
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);

        primaryStage.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent t) -> {
            CoreEngine.SCREEN_MOUSE.set(t.getSceneX(), t.getSceneY());
        });

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        ENGINE.init(primaryStage);
        primaryStage.show();
        ENGINE.fire();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
