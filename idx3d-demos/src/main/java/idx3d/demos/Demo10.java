package idx3d.demos;

import idx3d.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Chrome blob with clickable, id-buffer picked link boxes (original {@code demo10}).
 */
public final class Demo10 extends idx3d_DemoPanel {

    private static final String LINK1_URL = "http://www.darksim.com/html/gm_metal.html";
    private static final String LINK2_URL = "http://www.chscene.ch/index1.htm";
    private static final String LINK3_URL = "http://www.condensity.com";

    private int mouseX = -1;
    private int mouseY = -1;
    private boolean handCursor = false;
    private String hoverLabel = null;

    @Override
    protected void prepareScene() {
        scene.useIdBuffer(true);

        idx3d_Material metal = new idx3d_Material();
        metal.setEnvmap(idx3d_Resources.texture("textures/chrome.jpg"));
        metal.setReflectivity(255);
        scene.addMaterial("Metal", metal);

        scene.addMaterial("Flat", new idx3d_Material());
        scene.material("Flat").setFlat(true);

        scene.addMaterial("Link1", new idx3d_Material(idx3d_Resources.texture("textures/link1.jpg")));
        scene.addMaterial("Link2", new idx3d_Material(idx3d_Resources.texture("textures/link2.jpg")));
        scene.addMaterial("Link3", new idx3d_Material(idx3d_Resources.texture("textures/link3.jpg")));

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2f, 0.2f, 1f), 0xFFFFFF, 200, 80));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-1f, -1f, 0.4f), 0xAAAAAA, 100, 80));

        idx3d_Resources.import3ds(scene, "meshes/linkable.3ds");
        scene.rebuild();
        scene.normalize();
        scene.rotate(-3.14159265f / 2, -2f, 0.4f);

        for (int i = 0; i < scene.objects; i++) {
            if (scene.object[i].name.startsWith("Box")) {
                scene.object[i].setMaterial(scene.material("Flat"));
                scene.object[i].userData = idx3d_Vector.random(0.1f);
                scene.object[i].detach();
            }
        }

        scene.object("Blob").setMaterial(scene.material("Metal"));

        scene.object("Link1").setMaterial(scene.material("Link1"));
        scene.object("Link2").setMaterial(scene.material("Link2"));
        scene.object("Link3").setMaterial(scene.material("Link3"));
        scene.material("Link1").setTransparency(64);
        scene.material("Link2").setTransparency(64);
        scene.material("Link3").setTransparency(64);

        scene.object("Inside1").setMaterial(scene.material("Metal"));
        scene.object("Inside2").setMaterial(scene.material("Metal"));
        scene.object("Inside3").setMaterial(scene.material("Metal"));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseX = -1;
                mouseY = -1;
            }
        });
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            scene.rotate(0f, 0.04f, 0f);
        }
        float hue = (float) ((int) (((long) (animationTime() * 1000) / 48) & 255)) / 255;
        scene.material("Flat").setColor(Color.getHSBColor(hue, 1, 1).getRGB());

        for (int i = 0; i < scene.objects; i++) {
            if (scene.object[i].name.startsWith("Box")) {
                scene.object[i].rotateSelf((idx3d_Vector) scene.object[i].userData);
            }
        }

        updateHover();
    }

    private void updateHover() {
        idx3d_Object obj = (mouseX < 0 || mouseY < 0) ? null : scene.identifyObjectAt(mouseX, mouseY);
        scene.material("Link1").setTransparency(0);
        scene.material("Link2").setTransparency(0);
        scene.material("Link3").setTransparency(0);

        hoverLabel = null;
        boolean link = obj != null
                && (obj.name.equals("Link1") || obj.name.equals("Link2") || obj.name.equals("Link3"));
        if (obj != null) {
            if (obj.name.equals("Link1")) {
                scene.material("Link1").setTransparency(88);
                hoverLabel = LINK1_URL;
            }
            if (obj.name.equals("Link2")) {
                scene.material("Link2").setTransparency(88);
                hoverLabel = LINK2_URL;
            }
            if (obj.name.equals("Link3")) {
                scene.material("Link3").setTransparency(88);
                hoverLabel = LINK3_URL;
            }
        }
        setHandCursor(link);
    }

    private void setHandCursor(boolean hand) {
        if (handCursor == hand) {
            return;
        }
        handCursor = hand;
        setCursor(Cursor.getPredefinedCursor(hand ? Cursor.HAND_CURSOR : Cursor.MOVE_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.darkGray);
        g.fillRect(0, getHeight() - 18, getWidth(), 18);
        if (hoverLabel != null) {
            g.setColor(Color.white);
            g.drawString(hoverLabel, 4, getHeight() - 4);
        }
    }

    @Override
    protected void onMousePressed(MouseEvent e) {
        super.onMousePressed(e);
        autorotation = false;
        idx3d_Object obj = scene.identifyObjectAt(e.getX(), e.getY());
        if (obj != null) {
            if (obj.name.equals("Link1")) {
                openLink(LINK1_URL);
            }
            if (obj.name.equals("Link2")) {
                openLink(LINK2_URL);
            }
            if (obj.name.equals("Link3")) {
                openLink(LINK3_URL);
            }
        }
    }

    @Override
    protected void onMouseReleased(MouseEvent e) {
        super.onMouseReleased(e);
    }

    @Override
    protected void onMouseDragged(MouseEvent e) {
        super.onMouseDragged(e);
    }

    @Override
    protected void keyEvent(char key) {
        if (key == 'l') {
            new LightMapViewer(scene);
        }
    }

    private static void openLink(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } else {
                System.out.println("link: " + url);
            }
        } catch (Exception e) {
            System.out.println("link: " + url);
        }
    }
}
