import java.awt.*;
import java.awt.image.*;

public class Collectible {

    private int worldX, worldY;
    private int size = 32;
    private boolean active = true;
    private int animFrame = 0;

    private static BufferedImage sheet;

    static {
        try {
            sheet = javax.imageio.ImageIO.read(new java.io.File("src/coin_sheet.png"));
        } catch (Exception e) {
            sheet = null;
        }
    }

    public Collectible(int x, int y) {
        this.worldX = x;
        this.worldY = y;
    }

    public void updateAnimation() {
        animFrame = (animFrame + 1) % 4;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int sx = worldX - camX;
        int sy = worldY - camY;

        BufferedImage img;
        if (sheet != null) {
            img = sheet.getSubimage(animFrame * size, 0, size, size);
        } else {
            img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = img.createGraphics();
            ig.setColor(Color.YELLOW);
            ig.fillOval(4, 4, size-8, size-8);
            ig.dispose();
        }

        g2.drawImage(img, sx, sy, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(worldX, worldY, size, size);
    }

    public boolean isActive() { return active; }
    public void setInactive() { active = false; }
}