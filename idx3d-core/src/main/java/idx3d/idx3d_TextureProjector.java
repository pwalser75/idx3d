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

public class idx3d_TextureProjector
{
	public static final void projectFrontal(idx3d_Object obj)
	{
		obj.rebuild();
		idx3d_Vector min=obj.min();
		idx3d_Vector max=obj.max();
		float du=1/(max.x-min.x);
		float dv=1/(max.y-min.y);
		for (int i=0; i<obj.vertices;i++)
		{
			obj.vertex[i].u=(obj.vertex[i].pos.x-min.x)*du;
			obj.vertex[i].v=1-(obj.vertex[i].pos.y-min.y)*dv;
		}
	}

	public static final void projectTop(idx3d_Object obj)
	{
		obj.rebuild();
		idx3d_Vector min=obj.min();
		idx3d_Vector max=obj.max();
		float du=1/(max.x-min.x);
		float dv=1/(max.z-min.z);
		for (int i=0; i<obj.vertices;i++)
		{
			obj.vertex[i].u=(obj.vertex[i].pos.x-min.x)*du;
			obj.vertex[i].v=(obj.vertex[i].pos.z-min.z)*dv;
		}
	}
	
	public static final void projectCylindric(idx3d_Object obj)
	{
		obj.rebuild();
		idx3d_Vector min=obj.min();
		idx3d_Vector max=obj.max();
		float dz=1/(max.z-min.z);
		for (int i=0; i<obj.vertices;i++)
		{
			obj.vertex[i].pos.buildCylindric();
			obj.vertex[i].u=obj.vertex[i].pos.theta/(2*3.14159265f);
			obj.vertex[i].v=(obj.vertex[i].pos.z-min.z)*dz;
		}
	}
	
		


}