// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | Swing front-end replacing the legacy idx3d_BaseApplet.
// | -----------------------------------------------------------------

package idx3d;

import idx3d.debug.Inspector;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Swing replacement for the legacy {@code idx3d_BaseApplet}: renders an
 * {@link idx3d_Scene} on a {@link JPanel} with a dedicated render thread, and
 * provides the controls shared by every demo (rotate, camera shift, scale,
 * antialias, inspection).
 *
 * <p>Subclasses build their scene in {@link #prepareScene()} and may animate it
 * in {@link #runtime()} and react to keys in {@link #keyEvent(char)}.</p>
 */
public abstract class idx3d_DemoPanel extends JPanel implements Runnable {

    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;

    public idx3d_Scene scene;
    public boolean active = true;
    public boolean autorotation = true;
    public boolean antialias = false;
    public idx3d_FXPlugin plugin = null;

    private volatile Thread thread;
    private volatile boolean running = false;
    private volatile int pendingWidth = -1;
    private volatile int pendingHeight = -1;

    private int oldx = 0;
    private int oldy = 0;

    /**
     * Animation time scale. {@code runtime()} is called every rendered frame
     * (smooth), but all transform deltas are multiplied by this factor and the
     * {@link #animationTime()} clock advances at this rate, so demos animate at
     * 1/10 speed. Override with the system property {@code idx3d.animationScale}
     * (set to 1 for the original speed).
     */
    protected float animationScale = parseAnimationScale();
    private long lastAnimationNanos = 0;
    private double animationSeconds = 0;

    private static float parseAnimationScale() {
        try {
            return Float.parseFloat(System.getProperty("idx3d.animationScale", "0.1"));
        } catch (NumberFormatException e) {
            return 0.1f;
        }
    }

    /**
     * Virtual animation clock, in seconds, advancing at {@link #animationScale}
     * relative to wall-clock time. Use this instead of
     * {@code System.currentTimeMillis()} in time-based animations so they slow
     * down smoothly too.
     */
    protected double animationTime() {
        return animationSeconds;
    }

    private void advanceAnimationClock() {
        long now = System.nanoTime();
        if (lastAnimationNanos != 0) {
            animationSeconds += (now - lastAnimationNanos) / 1_000_000_000.0 * animationScale;
        }
        lastAnimationNanos = now;
    }

    protected idx3d_DemoPanel() {
        setBackground(Color.BLACK);
        setOpaque(true);
        setFocusable(true);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pendingWidth = getWidth();
                pendingHeight = getHeight();
            }
        });
    }

    /** Builds the scene. Called once, after {@link #scene} has been created. */
    protected abstract void prepareScene();

    /** Per-frame animation hook, called before each render. */
    protected void runtime() {
    }

    /** Demo-specific key handling, invoked after the shared controls. */
    protected void keyEvent(char key) {
    }

    // -- lifecycle ---------------------------------------------------------

    /** Creates the scene (first call) and starts the render thread. */
    public void start() {
        if (scene == null) {
            int w = getWidth() > 0 ? getWidth() : DEFAULT_WIDTH;
            int h = getHeight() > 0 ? getHeight() : DEFAULT_HEIGHT;
            scene = new idx3d_Scene(w, h);
            prepareScene();
        }
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this, "idx3d-render");
        thread.setDaemon(true);
        thread.start();
    }

    /** Stops the render thread (the scene is kept). */
    public void stop() {
        running = false;
        Thread t = thread;
        thread = null;
        if (t != null) {
            t.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (running) {
            int w = pendingWidth;
            int h = pendingHeight;
            if (w > 0 && h > 0 && scene != null) {
                pendingWidth = -1;
                pendingHeight = -1;
                scene.resize(w, h);
            }
            if (active) {
                repaint();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scene == null) {
            return;
        }
        advanceAnimationClock();
        idx3d_Animation.setScale(animationScale);
        try {
            runtime();
        } finally {
            idx3d_Animation.setScale(1f);
        }
        scene.render();
        if (plugin != null) {
            plugin.apply();
        }
        g.drawImage(scene.getImage(), 0, 0, null);
    }

    // -- input -------------------------------------------------------------

    protected void onKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            System.out.println(scene.getFPS() + "");
            return;
        }
        if (key == KeyEvent.VK_PAGE_UP) {
            scene.defaultCamera.shift(0f, 0f, 0.2f);
            return;
        }
        if (key == KeyEvent.VK_PAGE_DOWN) {
            scene.defaultCamera.shift(0f, 0f, -0.2f);
            return;
        }
        if (key == KeyEvent.VK_UP) {
            scene.defaultCamera.shift(0f, -0.2f, 0f);
            return;
        }
        if (key == KeyEvent.VK_DOWN) {
            scene.defaultCamera.shift(0f, 0.2f, 0f);
            return;
        }
        if (key == KeyEvent.VK_LEFT) {
            scene.defaultCamera.shift(0.2f, 0f, 0f);
            return;
        }
        if (key == KeyEvent.VK_RIGHT) {
            scene.defaultCamera.shift(-0.2f, 0f, 0f);
            return;
        }
        char c = e.getKeyChar();
        if (c == '+') {
            scene.scale(1.2f);
            return;
        }
        if (c == '-') {
            scene.scale(0.8f);
            return;
        }
        if (c == 'a') {
            antialias = !antialias;
            scene.setAntialias(antialias);
            return;
        }
        if (c == 'f') {
            for (int i = 0; i < scene.objects; i++) {
                scene.object[i].flipNormals();
            }
            return;
        }
        if (c == 'i') {
            Inspector.inspect(scene);
            return;
        }
        if (c == 's') {
            active = !active;
            return;
        }
        keyEvent(c);
    }

    protected void onMousePressed(MouseEvent e) {
        oldx = e.getX();
        oldy = e.getY();
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    protected void onMouseDragged(MouseEvent e) {
        autorotation = false;
        float dx = (float) (e.getY() - oldy) / 50;
        float dy = (float) (oldx - e.getX()) / 50;
        if (e.isShiftDown() || e.isMetaDown()) {
            scene.shift(-dy, -dx, 0);
        } else {
            scene.rotate(dx, dy, 0);
        }
        oldx = e.getX();
        oldy = e.getY();
    }

    protected void onMouseReleased(MouseEvent e) {
        autorotation = true;
        setCursor(Cursor.getDefaultCursor());
    }
}
