package idx3d.demos;

import idx3d.*;

/**
 * Torus + wineglass of revolution with glass/plastic/wireframe/flat materials
 * over a generated gradient background (original {@code demo1}).
 */
public final class Demo01 extends idx3d_DemoPanel {

    @Override
    protected void prepareScene() {
        idx3d_Texture bkgrd = new idx3d_Texture(2, 2);
        bkgrd.pixel[0] = 0xFF;
        bkgrd.pixel[1] = 0xFF00;
        bkgrd.pixel[2] = 0xFF0000;
        bkgrd.pixel[3] = 0xFFFF00;
        scene.environment.setBackground(idx3d_TextureFactory.bilinearResample(bkgrd, scene.width, scene.height));

        idx3d_Material crystal = idx3d_Resources.material("materials/glass.material");
        crystal.setReflectivity(255);
        scene.addMaterial("Crystal", crystal);

        idx3d_Material plastic = new idx3d_Material(idx3d_Resources.texture("textures/texture.jpg"));
        scene.addMaterial("Plastic", plastic);

        idx3d_Material wireframe = new idx3d_Material(0x000000);
        wireframe.setWireframe(true);
        wireframe.setFlat(true);
        scene.addMaterial("Wireframe", wireframe);

        idx3d_Material flat = new idx3d_Material(0xFFFFFF);
        flat.setFlat(true);
        scene.addMaterial("Flat", flat);

        idx3d_Material blank = new idx3d_Material(0xFFFFFF);
        scene.addMaterial("Blank", blank);

        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.4f, 0.4f, 1f), 0x666666, 640, 120));
        scene.addLight("Light2", new idx3d_Light(new idx3d_Vector(-1f, -1f, 1f), 0x996600, 120, 40));
        scene.addLight("Light3", new idx3d_Light(new idx3d_Vector(0.8f, -0.3f, 1f), 0x336699, 240, 80));
        scene.addLight("Light4", new idx3d_Light(new idx3d_Vector(-0.5f, 1f, 1f), 0x441166, 200, 80));

        idx3d_Vector[] path = new idx3d_Vector[9];
        path[0] = new idx3d_Vector(0.4f, 0.0f, 0);
        path[1] = new idx3d_Vector(0.6f, 0.3f, 0);
        path[2] = new idx3d_Vector(0.8f, 0.4f, 0);
        path[3] = new idx3d_Vector(0.9f, 0.3f, 0);
        path[4] = new idx3d_Vector(1.0f, 0.0f, 0);
        path[5] = new idx3d_Vector(0.9f, -0.3f, 0);
        path[6] = new idx3d_Vector(0.8f, -0.4f, 0);
        path[7] = new idx3d_Vector(0.6f, -0.3f, 0);
        path[8] = new idx3d_Vector(0.4f, 0.0f, 0);

        scene.addObject("Torus", idx3d_ObjectFactory.ROTATIONOBJECT(path, 40));
        scene.object("Torus").rotate(4.2f, 0.2f, -0.5f);
        scene.object("Torus").shift(-0.5f, 0f, 0f);
        scene.object("Torus").scale(0.72f);
        scene.object("Torus").setMaterial(scene.material("Plastic"));

        path = new idx3d_Vector[15];
        path[0] = new idx3d_Vector(0.0f, 0.2f, 0);
        path[1] = new idx3d_Vector(0.13f, 0.25f, 0);
        path[2] = new idx3d_Vector(0.33f, 0.3f, 0);
        path[3] = new idx3d_Vector(0.43f, 0.6f, 0);
        path[4] = new idx3d_Vector(0.48f, 0.9f, 0);
        path[5] = new idx3d_Vector(0.5f, 0.9f, 0);
        path[6] = new idx3d_Vector(0.45f, 0.6f, 0);
        path[7] = new idx3d_Vector(0.35f, 0.3f, 0);
        path[8] = new idx3d_Vector(0.25f, 0.2f, 0);
        path[9] = new idx3d_Vector(0.1f, 0.15f, 0);
        path[10] = new idx3d_Vector(0.1f, 0.0f, 0);
        path[11] = new idx3d_Vector(0.1f, -0.5f, 0);
        path[12] = new idx3d_Vector(0.35f, -0.55f, 0);
        path[13] = new idx3d_Vector(0.4f, -0.6f, 0);
        path[14] = new idx3d_Vector(0.0f, -0.6f, 0);

        scene.addObject("Wineglass", idx3d_ObjectFactory.ROTATIONOBJECT(path, 40));
        scene.object("Wineglass").rotate(0.5f, 0f, 0f);
        scene.object("Wineglass").setMaterial(scene.material("Crystal"));
        scene.object("Wineglass").removeDuplicateVertices();

        scene.defaultCamera.setFov(80);
        scene.scale(0.88f);
    }

    @Override
    protected void runtime() {
        if (autorotation) {
            scene.object("Torus").rotate(0f, 0.05f, 0.03f);
            scene.object("Wineglass").rotate(0f, -0.08f, -0.1f);
            scene.rotate(0.04f, 0.02f, 0.01f);
        }
    }

    @Override
    protected void keyEvent(char key) {
        if (key == 'l') {
            new LightMapViewer(scene);
            return;
        }
        if (key == '1') {
            scene.object("Wineglass").setMaterial(scene.material("Wireframe"));
            scene.object("Torus").setMaterial(scene.material("Wireframe"));
        }
        if (key == '2') {
            scene.object("Wineglass").setMaterial(scene.material("Flat"));
            scene.object("Torus").setMaterial(scene.material("Flat"));
        }
        if (key == '3') {
            scene.object("Wineglass").setMaterial(scene.material("Blank"));
            scene.object("Torus").setMaterial(scene.material("Blank"));
        }
        if (key == '4') {
            scene.object("Wineglass").setMaterial(scene.material("Crystal"));
            scene.object("Torus").setMaterial(scene.material("Plastic"));
        }
        if (key == 'm') {
            idx3d.idx3d_Toolkit.meshSmooth(scene.object("Wineglass"));
            idx3d.idx3d_Toolkit.meshSmooth(scene.object("Torus"));
        }
    }
}
