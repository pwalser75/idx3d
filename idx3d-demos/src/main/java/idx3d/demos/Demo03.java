package idx3d.demos;

import idx3d.*;

/**
 * Imported wobble mesh with four stone materials and frontal projection
 * (original {@code demo3}).
 */
public final class Demo03 extends idx3d_DemoPanel {

    @Override
    protected void prepareScene() {
        scene.addMaterial("Stone1", new idx3d_Material(idx3d_Resources.texture("textures/stone1.jpg")));
        scene.addMaterial("Stone2", new idx3d_Material(idx3d_Resources.texture("textures/stone2.jpg")));
        scene.addMaterial("Stone3", new idx3d_Material(idx3d_Resources.texture("textures/stone3.jpg")));
        scene.addMaterial("Stone4", new idx3d_Material(idx3d_Resources.texture("textures/stone4.jpg")));

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2f, 0.2f, 1f), 0xFFFFFF, 144, 120));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-1f, -1f, 1f), 0x332211, 100, 40));
        scene.addLight("Light3", new idx3d_Light(new idx3d_Vector(-1f, -1f, 1f), 0x666666, 200, 120));

        try {
            idx3d_Resources.import3ds(scene, "meshes/wobble.3ds");
            scene.rebuild();
            for (int i = 0; i < scene.objects; i++) {
                idx3d_TextureProjector.projectFrontal(scene.object[i]);
            }

            scene.object("Sphere1").setMaterial(scene.material("Stone1"));
            scene.object("Wobble1").setMaterial(scene.material("Stone2"));
            scene.object("Wobble2").setMaterial(scene.material("Stone3"));
            scene.object("Wobble3").setMaterial(scene.material("Stone4"));
            scene.normalize();
        } catch (Exception e) {
            System.out.println(e + "");
        }
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            float speed = 1;
            float dx = (float) Math.sin((float) animationTime()) / 20f;
            float dy = (float) Math.cos((float) animationTime()) / 20f;
            scene.rotate(-speed * dx, speed * dy, speed * 0.04f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == 'l') {
            new LightMapViewer(scene);
        }
    }
}
