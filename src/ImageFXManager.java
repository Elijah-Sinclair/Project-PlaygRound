import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;

public class ImageFXManager {

    private boolean fadeOn = false;
    private boolean grayOn = false;
    private boolean tintOn = false;

    public boolean isFadeOn()      { return fadeOn; }
    public boolean isGrayscaleOn() { return grayOn; }
    public boolean isTintOn()      { return tintOn; }

    public void handleKeyPressed(int code) {
        if (code == KeyEvent.VK_1) fadeOn = !fadeOn;
        if (code == KeyEvent.VK_2) grayOn = !grayOn;
        if (code == KeyEvent.VK_3) tintOn = !tintOn;
    }

    public String getActiveEffectsString() {
        StringBuilder sb = new StringBuilder();
        if (fadeOn)  sb.append("Fade ");
        if (grayOn)  sb.append("Grayscale ");
        if (tintOn)  sb.append("Tint ");
        return sb.length() == 0 ? "none" : sb.toString().trim();
    }

    public BufferedImage applyGrayscale(BufferedImage src) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return op.filter(src, dest);
    }

    public BufferedImage applyTint(BufferedImage src) {
        float[] scales = {1.3f, 0.5f, 0.5f}; // reddish tint
        float[] offsets = {20f, 0f, 0f};
        RescaleOp op = new RescaleOp(scales, offsets, null);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return op.filter(src, dest);
    }
}