/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.image.Image;
import static mainapp.BetterUtils.time;

/**
 *
 * @author Expiscor
 */
public class AnimatedImage extends BetterObject{

    double framesPerSecond;
    long delayBetweenFrames;
    
    boolean loops;
    
    long snap = time();
    
    long cacheTimeDifference;
    Image cacheCurrentFrame;
    
    Queue<Image> frames = new LinkedList<>();
    
    private void transit() {
        if (frames.size() == 0)
        {
            cacheCurrentFrame = null;
            this.destroy();
        }
        cacheCurrentFrame = frames.poll();
        if (loops) frames.add(cacheCurrentFrame);
        
    }

    Image step() {
        cacheTimeDifference = time() - snap;
        if (cacheTimeDifference > delayBetweenFrames) {
            for (int i = 0; i < cacheTimeDifference / delayBetweenFrames; i++) {
                transit();
            }
        }
        return cacheCurrentFrame;
    }

    Image getCurrentFrame() {
        return cacheCurrentFrame;
    }

}
