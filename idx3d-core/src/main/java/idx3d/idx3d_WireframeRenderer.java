// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// | -----------------------------------------------------------------

package idx3d;

import java.awt.*;

public final class idx3d_WireframeRenderer
{
	idx3d_Scene scene;
	int width,height;
	
	Image offscreen;
	Graphics g;
	Color bgcolor=new Color(0x000000);
	Color fgcolor=new Color(0x00FF00);
	Component parent;
	
	public idx3d_WireframeRenderer(idx3d_Scene scene, Container parent)
	{
		this.parent=parent;
		this.scene=scene;
		
	}
	
	public Image render()
	{
		return render(scene.defaultCamera);
	}
	
	public synchronized Image render(idx3d_Camera cam) throws Error
	{
		if (scene.width!=width || scene.height!=height)
		{
			width=scene.width;
			height=scene.height;
			offscreen=parent.createImage(scene.width,scene.height);
			g=offscreen.getGraphics();
		}
		
		// Clear buffers	
			g.setColor(bgcolor);
			g.fillRect(0,0,scene.width,scene.height);
			g.setColor(fgcolor);
			
		// Prepare
			cam.setScreensize(scene.width,scene.height);
			scene.prepareForRendering();
		
		// Project
			
			idx3d_Matrix m=idx3d_Matrix.multiply(cam.getMatrix(),scene.matrix);
			idx3d_Matrix nm=idx3d_Matrix.multiply(cam.getNormalMatrix(),scene.normalmatrix);
			idx3d_Matrix vertexProjection,normalProjection;
			idx3d_Object obj;
			idx3d_Triangle t;
			idx3d_Vertex v;
			int w=scene.width;
			int h=scene.height;
			for(int id=scene.objects-1;id>=0;id--)
			{
				obj=scene.object[id];
				if (obj.visible)
				{
					vertexProjection=obj.matrix.getClone();
					normalProjection=obj.normalmatrix.getClone();
					vertexProjection.transform(m);
					normalProjection.transform(nm);
		
					for (int i=obj.vertices -1;i>=0;i--) 
					{
						v=obj.vertex[i];
						v.project(vertexProjection,normalProjection,cam);
						v.clipFrustrum(w,h);
					}
		
					for (int i=obj.triangles -1;i>=0;i--) 
						render(obj.triangle[i]);			
				}
			}
			
		// Return Image
		
			return offscreen;		
	}

	
	public void render(idx3d_Triangle tri)
	{
		g.drawLine(tri.p1.x,tri.p1.y,tri.p2.x,tri.p2.y);
		g.drawLine(tri.p2.x,tri.p2.y,tri.p3.x,tri.p3.y);
		g.drawLine(tri.p1.x,tri.p1.y,tri.p3.x,tri.p3.y);
	}
}
