/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import static mainapp.BetterUtils.Random;

/**
 *
 * @author Expiscor
 */
public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        WebView webTest = new WebView();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        long x;
        x = Random.nextLong();
        
        StackPane root = new StackPane();
        //root.getChildren().add(btn);
        root.getChildren().add(webTest);
        webTest.getEngine().load("http://google.com");
        webTest.getEngine().getDocument().
                
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
