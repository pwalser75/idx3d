package idx3d.demos;

import idx3d.*;

/**
 * Textured cave and Venus mesh with a lens flare (original {@code demo7}).
 */
public final class Demo07 extends idx3d_DemoPanel {

    private idx3d_FXLensFlare lensFlare;

    @Override
    protected void prepareScene() {
        idx3d_Texture stone1 = idx3d_TextureFactory.bilinearResample(idx3d_Resources.texture("textures/stone5.jpg"), 512, 512);
        scene.addMaterial("Stone1", new idx3d_Material(stone1));
        scene.addMaterial("Stone2", new idx3d_Material(idx3d_Resources.texture("textures/stone4.jpg")));

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(-0.2f, -0.2f, 1f), 0x999999, 400, 120));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-1f, -1f, 1f), 0x663300, 320, 80));
        scene.addLight("Light3", new idx3d_Light(new idx3d_Vector(0.5f, 0.5f, 1f), 0x665544, 240, 48));

        scene.setAmbient(0x221911);

        try {
            idx3d_Resources.import3ds(scene, "meshes/venus.3ds");
            scene.normalize();

            scene.object("Cave").setMaterial(scene.material("Stone1"));
            idx3d_TextureProjector.projectFrontal(scene.object("Cave"));

            scene.object("Venus").setMaterial(scene.material("Stone2"));
            idx3d_TextureProjector.projectFrontal(scene.object("Venus"));

            scene.rotate(3.84159265f / 2, 1.7f, 0f);
            scene.scale(3.6f);

            lensFlare = new idx3d_FXLensFlare("LensFlare1", scene, true);
            lensFlare.preset2();
            scene.object("LensFlare1").scale(8f);
        } catch (Exception e) {
            System.out.println(e + "");
        }
        plugin = lensFlare;
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float speed = 1;
            float dx = (float) Math.sin((float) animationTime()) / 20;
            float dy = (float) Math.cos((float) animationTime() / 2) / 8;
            scene.rotate(speed * dx, speed * dy, 0f);
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
        if (key == 'l') {
            new LightMapViewer(scene);
            return;
        }
    }
}
