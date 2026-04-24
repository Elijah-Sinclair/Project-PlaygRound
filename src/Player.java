import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class Player {


    private int worldX, worldY;
    private int width = 32, height = 32;
    private int speed = 4;

    private BufferedImage spriteSheet;
    private int frame = 0;
    private int frameDelay = 0;
    private int direction = 0; // 0=down, 1=left, 2=right, 3=up

    private boolean up, down, left, right;
    private int fps = 0;

    public Player(int startX, int startY) {
        this.worldX = startX;
        this.worldY = startY;
        loadSpriteSheet();
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = javax.imageio.ImageIO.read(new File("src/player_sheet.jpg"));
        } catch (Exception e) {
            spriteSheet = null; // fallback later
        }
    }

    public void update(java.util.List<SolidObject> solids, java.util.List<Collectible> collectibles, BufferedImage bg) {
        int dx = 0, dy = 0;
        if (up)    dy--;
        if (down)  dy++;
        if (left)  dx--;
        if (right) dx++;

        if (dx == 0 && dy == 0) {
            frame = 0;
            return;
        }

        // Set direction
        if (dx > 0) direction = 2;
        else if (dx < 0) direction = 1;
        else if (dy > 0) direction = 0;
        else if (dy < 0) direction = 3;

        int nextX = worldX + dx * speed;
        int nextY = worldY + dy * speed;

        // World bounds
        if (bg != null) {
            nextX = Math.max(0, Math.min(nextX, bg.getWidth() - width));
            nextY = Math.max(0, Math.min(nextY, bg.getHeight() - height));
        }

        Rectangle next = new Rectangle(nextX, nextY, width, height);

        // Collision with solids
        boolean collision = false;
        for (SolidObject s : solids) {
            if (next.intersects(s.getBounds())) {
                collision = true;
                break;
            }
        }

        if (!collision) {
            worldX = nextX;
            worldY = nextY;

            // Animate
            frameDelay++;
            if (frameDelay >= 5) {
                frame = (frame + 1) % 4;
                frameDelay = 0;
            }
        }
    }

    public boolean checkCollection(java.util.List<Collectible> collectibles) {
        Rectangle pRect = new Rectangle(worldX, worldY, width, height);
        for (Collectible c : collectibles) {
            if (c.isActive() && pRect.intersects(c.getBounds())) {
                c.setInactive();
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2, int camX, int camY, ImageFXManager fx) {
        int screenX = worldX - camX;
        int screenY = worldY - camY;

        BufferedImage frameImg;
        if (spriteSheet != null) {
             //adding this because the player sprite I am using does not match convention required
            frameImg = spriteSheet.getSubimage(frame * width, direction * height, width, height);
        } else {
            frameImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D pg = frameImg.createGraphics();
            pg.setColor(Color.BLUE);
            pg.fillRect(0, 0, width, height);
            pg.dispose();
        }

        // Apply effects
        if (fx.isGrayscaleOn()) frameImg = fx.applyGrayscale(frameImg);
        if (fx.isTintOn())      frameImg = fx.applyTint(frameImg);

        Composite old = g2.getComposite();
        if (fx.isFadeOn()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }
        g2.drawImage(frameImg, screenX, screenY, null);
        g2.setComposite(old);
    }

    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }

    public int getCamX(int panelWidth, BufferedImage bg) {
        int cam = worldX + width/2 - panelWidth/2;
        if (bg != null) cam = Math.max(0, Math.min(cam, bg.getWidth() - panelWidth));
        return cam;
    }

    public int getCamY(int panelHeight, BufferedImage bg) {
        int cam = worldY + height/2 - panelHeight/2;
        if (bg != null) cam = Math.max(0, Math.min(cam, bg.getHeight() - panelHeight));
        return cam;
    }

    public void handleKeyPressed(int code) {
        if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = true;
        if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = true;
        if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = true;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = true;
    }

    public void handleKeyReleased(int code) {
        if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = false;
        if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = false;
        if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = false;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = false;
    }

    public int getFps() { return fps; }
    public void setFps(int fps) { this.fps = fps; }
}