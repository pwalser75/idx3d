package idx3d.demos;

import idx3d.*;

/**
 * Torusknot showing seven material modes (flat, envmap, glass, texture,
 * reflective, transparent) over a checkerboard/gradient background
 * (original {@code demo4}).
 */
public final class Demo04 extends idx3d_DemoPanel {

    @Override
    protected void prepareScene() {
        idx3d_Texture bkgrd = idx3d_TextureFactory.blendTopDown(
                idx3d_TextureFactory.CHECKERBOARD(scene.width, scene.height, 4, 0x000000, 0x999999),
                idx3d_Resources.texture("textures/idxbkgrd.jpg"));
        scene.environment.setBackground(bkgrd);

        idx3d_Texture envmap = idx3d_Resources.texture("textures/skymap.jpg");
        idx3d_Texture texture = idx3d_Resources.texture("textures/texture.jpg");

        idx3d_Material mode1 = new idx3d_Material(0x0066FF);
        mode1.setFlat(true);

        idx3d_Material mode2 = new idx3d_Material(0x330099);
        mode2.setEnvmap(envmap);
        mode2.setReflectivity(63);

        idx3d_Material mode3 = new idx3d_Material();
        mode3.setEnvmap(envmap);

        idx3d_Material mode4 = idx3d_Resources.material("materials/glass.material");
        mode4.setTransparency(88);

        idx3d_Material mode5 = new idx3d_Material(texture);

        idx3d_Material mode6 = new idx3d_Material(texture);
        mode6.setEnvmap(envmap);
        mode6.setReflectivity(96);

        idx3d_Material mode7 = new idx3d_Material(texture);
        mode7.setEnvmap(envmap);
        mode7.setReflectivity(96);
        mode7.setTransparency(64);

        scene.addMaterial("Mode1", mode1);
        scene.addMaterial("Mode2", mode2);
        scene.addMaterial("Mode3", mode3);
        scene.addMaterial("Mode4", mode4);
        scene.addMaterial("Mode5", mode5);
        scene.addMaterial("Mode6", mode6);
        scene.addMaterial("Mode7", mode7);

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2f, 0.2f, 1f), 0xFFFFFF, 144, 120));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(0.6f, -1f, 1f), 0x332211, 100, 40));
        scene.addLight("Light3", new idx3d_Light(new idx3d_Vector(-1f, -1f, 1f), 0xccaa88, 200, 120));

        scene.environment.ambient = 0x554433;
        scene.defaultCamera.setFov(120f);

        scene.addObject("Torusknot", idx3d_ObjectFactory.TORUSKNOT(5f, 1f, 0.28f, 1.2f, 0.48f, 0.8f, 88, 9));
        scene.object("Torusknot").rotate(0.2f, 3.5f, -0.5f);
        scene.object("Torusknot").setMaterial(scene.material("Mode6"));
        scene.object("Torusknot").scale(0.72f);
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            scene.object("Torusknot").rotate(-0.01f, 0.02f, 0.05f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == '1') {
            scene.object("Torusknot").setMaterial(scene.material("Mode1"));
            return;
        }
        if (key == '2') {
            scene.object("Torusknot").setMaterial(scene.material("Mode2"));
            return;
        }
        if (key == '3') {
            scene.object("Torusknot").setMaterial(scene.material("Mode3"));
            return;
        }
        if (key == '4') {
            scene.object("Torusknot").setMaterial(scene.material("Mode4"));
            return;
        }
        if (key == '5') {
            scene.object("Torusknot").setMaterial(scene.material("Mode5"));
            return;
        }
        if (key == '6') {
            scene.object("Torusknot").setMaterial(scene.material("Mode6"));
            return;
        }
        if (key == '7') {
            scene.object("Torusknot").setMaterial(scene.material("Mode7"));
            return;
        }
        if (key == 'l') {
            new LightMapViewer(scene);
            return;
        }
    }
}
