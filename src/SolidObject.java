import java.awt.*;
import java.awt.image.*;

public class SolidObject {

    private int worldX, worldY, width, height;
    private static BufferedImage treeImg;

    static {
        try {
            treeImg = javax.imageio.ImageIO.read(new java.io.File("src/tree.png"));
        } catch (Exception e) {
            treeImg = null;
        }
    }

    public SolidObject(int x, int y, int w, int h) {
        this.worldX = x;
        this.worldY = y;
        this.width = w;
        this.height = h;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int sx = worldX - camX;
        int sy = worldY - camY;

        if (treeImg != null) {
            g2.drawImage(treeImg, sx, sy, width, height, null);
        } else {
            g2.setColor(new Color(100, 60, 20));
            g2.fillRect(sx, sy, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(worldX, worldY, width, height);
    }
}
