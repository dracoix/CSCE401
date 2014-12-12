package mainapp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class RenderObject extends BetterObject {

    //Abstraction is partially used.
    
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
        return renderPosition;
    }

    public BetterPoint2D getPosition() {
        return this.position;
    }

    public void syncPosition(BetterPoint2D sync) {
        this.position = sync;
    }

    public void tick() {
        if (this.currentFrame == null) return;
        renderPosition.set(this.position.getX() - this.centerW(), this.position.getY() - this.centerH());
    }

    public void render(GraphicsContext gc)
    {
        if (this.currentFrame== null) return;
        gc.drawImage(currentFrame, renderPosition.getX(), renderPosition.getY());
    }
    
    public void renderPost(GraphicsContext gc)
    {
        this.tick();
        render(gc);
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
