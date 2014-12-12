/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 *
 * @author David 'dracoix' Rathbun
 */
public class _REMOVED_BetterGraphic {

    Image myImage;
    Color mySolidColor;
    Color myBorderColor;
    double width;
    double height;
    double alpha;
    BetterPoint2D renderPosition;
    boolean useBorder;
    boolean useFill;
    boolean useTrans;
    GraphicsContext gc;

    _REMOVED_BetterGraphic() {

    }

    public void draw(GraphicsContext gc) {
        this.gc = gc;
        if (gc == null) {
            return;
        }
        if (myImage == null) {
            drawShape();
        } else {
            drawImage();
        }
    }

    public void drawShape() {
        gc.fillRect(renderPosition.getX(), renderPosition.getY(), width, height);
    }

    public void drawImage() {

        gc.drawImage(myImage, renderPosition.getX(), renderPosition.getY());

    }

    public void drawBoth() {
        drawImage();
        drawShape();
    }

}
