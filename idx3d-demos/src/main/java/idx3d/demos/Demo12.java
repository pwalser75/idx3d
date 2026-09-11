package idx3d.demos;

import idx3d.*;

/**
 * Chrome torus knot over a background image (original {@code demo12}).
 */
public final class Demo12 extends idx3d_DemoPanel {

    private idx3d_Material metal;
    private idx3d_Material wireframe;

    @Override
    protected void prepareScene() {
        idx3d_Object obj = idx3d_ObjectFactory.TORUSKNOT(2, 3, 0.32f, 1.2f, 0.48f, 1.2f, 120, 20);
        obj.removeDuplicateVertices();

        metal = new idx3d_Material();
        metal.setEnvmap(idx3d_Resources.texture("textures/chrome.jpg"));
        metal.setReflectivity(255);
        metal.setColor(0x223344);
        scene.addMaterial("Metal", metal);

        wireframe = new idx3d_Material();
        wireframe.setWireframe(true);
        wireframe.setColor(0x333333);
        wireframe.setFlat(true);
        scene.addMaterial("Wireframe", wireframe);

        idx3d_TextureProjector.projectFrontal(obj);

        obj.setMaterial(metal);
        scene.addObject("Torus Knot", obj);

        scene.setAmbient(0x111111);
        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.5f, 0.5f, 1f), 0xFFFFFF, 200, 80));
        scene.normalize();
        scene.scale(0.82f);

        scene.setBackground(idx3d_Resources.texture("textures/back.jpg"));
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float speed = 1f;
            float dx = (float) Math.sin((float) animationTime()) / 20;
            float dy = (float) Math.cos((float) animationTime()) / 20;
            scene.rotate(-speed * dx, speed * dy, speed * 0.04f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == 'm') {
            for (int i = 0; i < scene.objects; i++) {
                idx3d_Toolkit.meshSmooth(scene.object[i]);
            }
        }
        if (key == '1') {
            scene.object("Torus Knot").setMaterial(metal);
        }
        if (key == '2') {
            scene.object("Torus Knot").setMaterial(wireframe);
        }
    }
}
