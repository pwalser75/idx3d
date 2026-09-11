// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   KeyframePrototype.java

import idx3d.*;
import java.applet.Applet;
import java.awt.*;

public final class KeyframePrototype extends Applet
{

    public KeyframePrototype()
    {
    }

    public void init()
    {
        scene = new idx3d_Scene(size().width, size().height);
        idx3d_Object idx3d_object = idx3d_ObjectFactory.TORUSKNOT(2.0F, 3F, 0.3F, 1.2F, 0.48F, 1.2F, 100, 10);
        idx3d_object.removeDuplicateVertices();
        idx3d_Material idx3d_material = new idx3d_Material(getDocumentBase(), "materials/silver.material");
        scene.defaultCamera.setFov(100F);
        idx3d_object.setMaterial(idx3d_material);
        scene.addObject("Torus Knot", idx3d_object);
        scene.setAmbient(0x444444);
        scene.addLight("Light1", new idx3d_Light(new idx3d_Vector(0.2F, 0.2F, 1.0F), 0xffffff, 320, 100));
        scene.normalize();
        m1 = idx3d_object.matrix.getClone();
        m2 = idx3d_object.matrix.getClone();
        m1.rotate(2.0F, 1.0F, 4F);
        m2.rotate(-1F, 5F, 13F);
        n1 = idx3d_object.normalmatrix.getClone();
        n2 = idx3d_object.normalmatrix.getClone();
        n1.rotate(2.0F, 1.0F, 4F);
        n2.rotate(-1F, 5F, 13F);
    }

    public synchronized void paint(Graphics g)
    {
        repaint();
    }

    public synchronized void update(Graphics g)
    {
        scene.render();
        g.drawImage(scene.getImage(), 0, 0, this);
    }

    public boolean mouseDrag(Event event, int i, int j)
    {
        float f = (float)i / (float)size().width;
        scene.object("Torus Knot").matrix = interpolateMatrix(m1, m2, f);
        scene.object("Torus Knot").normalmatrix = interpolateMatrix(n1, n2, f);
        repaint();
        return true;
    }

    public idx3d_Matrix interpolateMatrix(idx3d_Matrix idx3d_matrix, idx3d_Matrix idx3d_matrix1, float f)
    {
        idx3d_Matrix idx3d_matrix2 = new idx3d_Matrix();
        float af[][] = idx3d_matrix.exportToArray();
        float af1[][] = idx3d_matrix1.exportToArray();
        float af2[][] = new float[4][4];
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
                af2[i][j] = idx3d_Math.interpolate(af[i][j], af1[i][j], f);

        }

        idx3d_matrix2.importFromArray(af2);
        return idx3d_matrix2;
    }

    idx3d_Scene scene;
    idx3d_Matrix m1;
    idx3d_Matrix m2;
    idx3d_Matrix n1;
    idx3d_Matrix n2;
}
