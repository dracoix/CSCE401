package whackamole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Expiscor
 */
public class Whackamole extends Application {

    @Override
    public void start(Stage primaryStage) {
        EngineDriver ENGINE = new EngineDriver();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //primaryStage.setWidth(SCREEN_WIDTH);
        //primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);

        primaryStage.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent t) -> {
            EngineCore.MASTER_MOUSE_X = t.getSceneX();
            EngineCore.MASTER_MOUSE_Y = t.getSceneY();
            EngineCore.MOUSE_MOVE = true;
        });
        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent t) -> {
            EngineCore.WINDOW_X = t.getSceneX();
            EngineCore.WINDOW_Y = t.getSceneY();
            EngineCore.MOUSE_DOWN = true;
        });

        primaryStage.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent t) -> {
            EngineCore.MOUSE_DOWN = false;
        });
        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent t) -> {
            //primaryStage.setX(t.getScreenX() - WINDOW_X);
            //primaryStage.setY(t.getScreenY() - WINDOW_Y);
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
