package idx3d.demos;

import idx3d.*;

/**
 * Rotating height-field terrain with reflection (original {@code demo2}).
 */
public final class Demo02 extends idx3d_DemoPanel {

    private float speed = 1f;

    @Override
    protected void prepareScene() {
        idx3d_Material m = new idx3d_Material();
        idx3d_Texture heightMap = idx3d_Resources.texture("textures/terrain.gif");

        m.setTexture(idx3d_Resources.texture("textures/terrain.jpg"));
        m.setReflectivity(64);
        scene.addMaterial("Material1", m);

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2f, 0.2f, 1f), 0xFFFFFF, 480, 400));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-0.6f, -0.8f, 1f), 0x998877, 240, 120));

        scene.addObject("Field", idx3d_ObjectFactory.HEIGHTFIELD(heightMap, 0.4f, true));

        scene.object("Field").setMaterial(scene.material("Material1"));
        scene.object("Field").scale(0.88f);
        scene.defaultCamera.setFov(120);
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float dx = (float) Math.sin((float) animationTime()) / 20;
            float dy = (float) Math.cos((float) animationTime()) / 20;
            scene.object("Field").rotate(speed * dx, speed * dy, speed * -0.04f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == '.') {
            speed *= 1.2f;
            return;
        }
        if (key == ',') {
            speed *= 0.8f;
            return;
        }
        if (key == 'r') {
            scene.resize(200, 200);
            return;
        }
        if (key == 'l') {
            new LightMapViewer(scene);
            return;
        }
    }
}
