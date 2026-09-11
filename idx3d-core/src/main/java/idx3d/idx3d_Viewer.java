// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | Modernized Swing viewer panel (was an AWT Panel with AWT-1.0 events).
// | -----------------------------------------------------------------

package idx3d;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A self-contained Swing panel that continuously renders an {@link idx3d_Scene}.
 * Replaces the legacy AWT {@code Panel} implementation.
 */
public class idx3d_Viewer extends JPanel implements Runnable {

    private idx3d_Scene scene;
    private volatile Thread thread;

    private int oldx = 0;
    private int oldy = 0;
    private boolean autorotation = true;
    private boolean antialias = false;

    public idx3d_Viewer() {
        setSize(100, 100);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                oldx = e.getX();
                oldy = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                autorotation = true;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                autorotation = false;
                float dx = (float) (e.getY() - oldy) / 50;
                float dy = (float) (oldx - e.getX()) / 50;
                scene.rotate(dx, dy, 0);
                oldx = e.getX();
                oldy = e.getY();
                if (!isRunning()) {
                    repaint();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_SPACE) {
                    System.out.println(scene.getFPS() + "");
                    return;
                }
                char c = e.getKeyChar();
                if (c == 'a') {
                    antialias = !antialias;
                    scene.setAntialias(antialias);
                }
                if (c == 's') {
                    autorotation = !autorotation;
                }
                if (c == 'p') {
                    saveScreenshot();
                }
                if (!isRunning()) {
                    repaint();
                }
            }
        });
    }

    public void setScene(idx3d_Scene scene) {
        this.scene = scene;
    }

    public boolean isRunning() {
        return thread != null;
    }

    public idx3d_Scene getScene() {
        if (scene == null) {
            scene = new idx3d_Scene(getWidth(), getHeight());
        }
        return scene;
    }

    public void resize(int w, int h) {
        getScene().resize(w, h);
    }

    public void resize(Dimension d) {
        resize(d.width, d.height);
    }

    public void setRunnable(boolean runnable) {
        if (runnable) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this, "idx3d-viewer");
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void stop() {
        thread = null;
    }

    @Override
    public void run() {
        while (thread == Thread.currentThread()) {
            repaint();
            try {
                Thread.sleep(50);
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
        if (getWidth() < 1 || getHeight() < 1) {
            return;
        }
        if (autorotation) {
            float speed = 1;
            float dx = (float) Math.sin((float) System.currentTimeMillis() / 1000) / 20;
            float dy = (float) Math.cos((float) System.currentTimeMillis() / 2000) / 8;
            scene.rotate(speed * dx, speed * dy, 0f);
        }
        scene.render();
        g.drawImage(scene.getImage(), 0, 0, null);
    }

    private void saveScreenshot() {
        int id = 1;
        while (new File("Screenshot#" + id + ".jpg").exists()) {
            id++;
        }
        String filename = "Screenshot#" + id + ".jpg";
        try {
            BufferedImage buf = new BufferedImage(scene.width, scene.height, BufferedImage.TYPE_INT_BGR);
            Graphics g = buf.createGraphics();
            g.drawImage(scene.getImage(), 0, 0, null);
            g.dispose();
            ImageIO.write(buf, "jpg", new File(filename));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(scene != null ? scene.width : 100, scene != null ? scene.height : 100);
    }
}
