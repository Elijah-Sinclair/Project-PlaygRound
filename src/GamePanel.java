import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    private static final int TARGET_FPS = 60;
    private static final int TARGET_COLLECT = 5;

    private Thread gameThread;
    private boolean running = false;

    private BufferedImage backBuffer;

    private Player player;
    private List<SolidObject> solidObjects = new ArrayList<>();
    private List<Collectible> collectibles = new ArrayList<>();

    private int collected = 0;
    private boolean gameOver = false;

    private int fpsCounter = 0;

    SoundManager soundManager;
    private ImageFXManager fxManager;

    private BufferedImage backgroundImage;

    public GamePanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);

        loadAssets();

        player = new Player(400, 300);
        fxManager = new ImageFXManager();
        soundManager = SoundManager.getInstance();

        initWorld();

        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private void loadAssets() {
        try {
            backgroundImage = javax.imageio.ImageIO.read(new java.io.File("src/background2.jpg"));
        } catch (Exception e) {
            System.err.println("Background not loaded - using fallback");
        }
    }

    private void initWorld() {
        // Solid objects
        solidObjects.add(new SolidObject(600, 400, 64, 64));
        solidObjects.add(new SolidObject(1200, 300, 64, 64));
        solidObjects.add(new SolidObject(300, 500, 64, 64));
        solidObjects.add(new SolidObject(1000, 150, 64, 64));

        // Collectibles
        collectibles.add(new Collectible(200, 200));
        collectibles.add(new Collectible(700, 600));
        collectibles.add(new Collectible(1100, 150));
        collectibles.add(new Collectible(1100, 150));
        collectibles.add(new Collectible(400, 200));
        collectibles.add(new Collectible(100, 400));
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        running = true;
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1_000_000_000.0 / TARGET_FPS;
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            if (now - lastTime >= nsPerFrame) {
                update();
                repaint();
                lastTime += nsPerFrame;
                fpsCounter++;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                player.setFps(fpsCounter);
                fpsCounter = 0;
                timer += 1000;
            }

            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }

    private void update() {
        if (gameOver) return;

        player.update(solidObjects, collectibles, backgroundImage);

        if (player.checkCollection(collectibles)) {
            collected++;
            soundManager.playClip("collect", false);
            if (collected >= TARGET_COLLECT) {
                gameOver = true;
            }
        }

        for (Collectible c : collectibles) {
            if (c.isActive()) c.updateAnimation();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) backBuffer.getGraphics();
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int camX = player.getCamX(getWidth(), backgroundImage);
        int camY = player.getCamY(getHeight(), backgroundImage);

        // Draw background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, -camX, -camY, null);
        }

        // Solids
        for (SolidObject obj : solidObjects) {
            obj.draw(g2, camX, camY);
        }

        // Collectibles
        for (Collectible c : collectibles) {
            if (c.isActive()) c.draw(g2, camX, camY);
        }

        // Player
        player.draw(g2, camX, camY, fxManager);

        // HUD
        drawHUD(g2);

        // Game Over
        if (gameOver) {
            drawGameOver(g2);
        }

        g2.dispose();

        // Final blit
        Graphics2D screenG = (Graphics2D) g.create();
        if (gameOver) {
            BufferedImage gray = fxManager.applyGrayscale(backBuffer);
            screenG.drawImage(gray, 0, 0, null);
        } else {
            screenG.drawImage(backBuffer, 0, 0, null);
        }
        screenG.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        g2.drawString("FPS: " + player.getFps(), 10, 20);
        g2.drawString("World: (" + player.getWorldX() + ", " + player.getWorldY() + ")", 10, 40);
//        g2.drawString("Effects: " + fxManager.getActiveEffectsString(), 10, 60);
        g2.drawString("Collected: " + collected + "/" + TARGET_COLLECT, 10, 80);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.RED);
        String text = "GAME OVER";
        int w = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, getWidth()/2 - w/2, getHeight()/2);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        player.handleKeyPressed(e.getKeyCode());
        fxManager.handleKeyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.handleKeyReleased(e.getKeyCode());
    }
}
