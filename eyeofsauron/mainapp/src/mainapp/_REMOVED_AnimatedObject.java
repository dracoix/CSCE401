package mainapp;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.image.Image;

public abstract class _REMOVED_AnimatedObject extends BetterObject {

    _REMOVED_AnimatedImage aniCreate;
    _REMOVED_AnimatedImage aniStatic;
    _REMOVED_AnimatedImage aniDecaying;
    _REMOVED_AnimatedImage aniDecayed;

    Image curFrame;
    _REMOVED_AnimatedImage curAni;

    Queue<_REMOVED_AnimatedImage> aniSet = new LinkedList<>();

    public Image getCurrentFrame() {
        return curFrame;
    }

    @Override
    public void destroy() {
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
        if (aniSet.size() == 0) {
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
