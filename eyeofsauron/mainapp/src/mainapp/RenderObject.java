package mainapp;

import javafx.scene.image.Image;

public class RenderObject extends BetterObject {

    Image currentFrame;
    BetterPoint2D position;
    BetterPoint2D renderPosition = new BetterPoint2D();

    public Image getCurrentFrame() {
        return this.currentFrame;
    }

    public void setCurrentFrame(Image i) {
        this.currentFrame = i;
    }

    public double width() {
        return (currentFrame == null) ? 0 : this.currentFrame.getWidth();
    }

    public double height() {
        return (currentFrame == null) ? 0 : this.currentFrame.getHeight();
    }

    public BetterPoint2D getRenderPosition() {
        renderPosition.set(this.position.getX() - this.width() / 2, this.position.getY() - this.height() / 2);
        return renderPosition;
    }

    public BetterPoint2D getPosition() {
        return this.position;
    }

    public void syncPosition(BetterPoint2D sync) {
        this.position = sync;
    }

    public void tick() {

    }

    public double centerW() {
        return this.width() / 2;
    }

    public double centerH() {
        return this.height() / 2;
    }

    @Override
    public void destroy() {
        this.currentFrame = null;
        this.renderPosition = null;
        this.position = null;
        super.destroy();
    }

}
