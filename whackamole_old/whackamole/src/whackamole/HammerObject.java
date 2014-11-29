package whackamole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.scene.image.Image;

public class HammerObject {

    BetterPoint2D position;
    Image frames[];
    Image currentFrame;

    double scale;

    boolean isAnimating;
    int currentFrameIndex;
    int currentTimeStep;

    public void adjustScale(double y, double height) {
        scale = (0.5 + (y / height));
    }

    public double getDrawWidth() {
        return currentFrame.getWidth() * scale;
    }

    public double getDrawHeight() {
        return currentFrame.getHeight() * scale;
    }

    public void animate() {
        if (isAnimating) {
            currentTimeStep++;
        }
        currentFrameIndex = 1;
    }

    public void tick() {

    }

}
