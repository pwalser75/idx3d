package idx3d.demos;

import idx3d.*;

/**
 * Chrome-shaded mech mesh (original {@code demo8}).
 */
public final class Demo08 extends idx3d_DemoPanel {

    @Override
    protected void prepareScene() {
        scene.addMaterial("Chrome", idx3d_Resources.material("materials/chrome.material"));

        idx3d_Resources.import3ds(scene, "meshes/mech.3ds");
        scene.rebuild();
        for (int i = 0; i < scene.objects; i++) {
            scene.object[i].setMaterial(scene.material("Chrome"));
        }
        scene.normalize();
        scene.rotate(3.14159265f / 2, 3.14159265f / 2, 0f);
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            scene.rotate(0f, 0.06f, 0f);
        }
    }
}
