package idx3d.demos;

import idx3d.*;

/**
 * Glass demon under a checkered skydome with a lens flare (original {@code demo11}).
 */
public final class Demo11 extends idx3d_DemoPanel {

    private idx3d_FXLensFlare lensFlare;

    @Override
    protected void prepareScene() {
        scene.addMaterial("Sky", new idx3d_Material(idx3d_TextureFactory.CHECKERBOARD(128, 128, 1, 0x000000, 0x666666)));
        scene.material("Sky").setFlat(true);

        scene.addMaterial("Glass", idx3d_Resources.material("materials/glass.material"));
        scene.material("Glass").setTransparency(60);

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(-0.2f, -0.2f, 1f), 0xFFFFFF, 120, 120));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-1f, -1f, -4f), 0x0066FF, 120, 80));

        idx3d_Resources.import3ds(scene, "meshes/demon.3ds");
        scene.normalize();

        scene.object("Skydome").setMaterial(scene.material("Sky"));

        scene.object("Demon").setMaterial(scene.material("Glass"));
        idx3d_TextureProjector.projectTop(scene.object("Demon"));

        scene.object("Demon").matrixMeltdown();
        scene.defaultCamera.lookAt(scene.object("Demon").getCenter().transform(scene.matrix));
        scene.rotate(3.84159265f / 2, 0f, 0f);
        scene.scale(3f);
        scene.shift(0, 0.32f, 0);

        lensFlare = new idx3d_FXLensFlare("LensFlare1", scene, false);
        lensFlare.preset2();
        scene.object("LensFlare1").scale(16f);
        plugin = lensFlare;
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float speed = 0.64f;
            float dx = (float) Math.sin((float) animationTime()) / 20;
            float dy = (float) Math.cos((float) animationTime() / 2) / 8;
            scene.rotate(speed * dx, speed * dy, 0f);
            scene.defaultCamera.setFov(80f + 30f * (float) Math.sin((float) animationTime()));
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == '1') {
            lensFlare.preset1();
        }
        if (key == '2') {
            lensFlare.preset2();
        }
        if (key == '3') {
            lensFlare.preset3();
        }
        if (key == 'l') {
            new LightMapViewer(scene);
        }
    }
}
