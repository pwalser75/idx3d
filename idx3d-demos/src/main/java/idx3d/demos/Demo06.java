package idx3d.demos;

import idx3d.*;

/**
 * Chrome wobble meshes with a toggleable lens flare (original {@code demo6}).
 */
public final class Demo06 extends idx3d_DemoPanel {

    private boolean useLensFlare = true;
    private idx3d_FXLensFlare lensFlare;

    @Override
    protected void prepareScene() {
        idx3d_Material metal = new idx3d_Material();
        metal.setEnvmap(idx3d_Resources.texture("textures/chrome.jpg"));
        metal.setReflectivity(255);
        scene.addMaterial("Metal", metal);

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2f, 0.2f, 1f), 0xFFFFFF, 320, 80));

        try {
            idx3d_Resources.import3ds(scene, "meshes/wobble.3ds");
            scene.rebuild();
            for (int i = 0; i < scene.objects; i++) {
                scene.object[i].setMaterial(scene.material("Metal"));
            }

            scene.normalize();

            for (int i = 0; i < scene.objects; i++) {
                idx3d_Toolkit.meshSmooth(scene.object[i]);
            }
        } catch (Exception e) {
            System.out.println(e + "");
        }

        lensFlare = new idx3d_FXLensFlare("LensFlare1", scene, false);
        lensFlare.preset1();
        scene.object("LensFlare1").scale(60f);
        plugin = lensFlare;
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float speed = 1;
            float dx = (float) Math.sin((float) animationTime()) / 20;
            float dy = (float) Math.cos((float) animationTime()) / 20;
            scene.rotate(-speed * dx, speed * dy, speed * 0.04f);
            scene.object("LensFlare1").rotate(-0.07f, 0.02f, 0.03f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == '1') {
            lensFlare.preset1();
            return;
        }
        if (key == '2') {
            lensFlare.preset2();
            return;
        }
        if (key == '3') {
            lensFlare.preset3();
            return;
        }
    }
}
