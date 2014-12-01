/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.image.Image;

/**
 *
 * @author Expiscor
 */
public abstract class AnimatedObject extends BetterObject {

    AnimatedImage aniCreate;
    AnimatedImage aniStatic;
    AnimatedImage aniDecaying;
    AnimatedImage aniDecayed;

    Image curFrame;
    AnimatedImage curAni;

    Queue<AnimatedImage> aniSet = new LinkedList<>();

    public Image getCurrentFrame()
    {
        return curFrame;
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
        aniSet.clear();
        aniSet = null;
    }
    
    public void create() {
        aniSet.clear();
        aniSet.add(aniCreate);
        aniSet.add(aniStatic);
        aniSet.add(aniDecaying);
        aniSet.add(aniDecayed);
    }

    private Image firstAniFrame() {
        if (aniSet.size() == 0)
        {
            this.destroy();
            return null;
        }
        curAni = aniSet.poll();
        return curAni.getCurrentFrame();
    }

    public void tick(boolean nextAnimation) {
        if (nextAnimation) {
            //curFrame = firstAniFrame();
        } else {
            //curFrame = curAni.step();
        }
    }
}
