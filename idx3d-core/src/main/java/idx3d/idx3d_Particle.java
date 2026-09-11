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

public class idx3d_Particle
{
	public boolean additive=false;
	public idx3d_Vector pos;
	idx3d_Vector pos2;
	float size;
	int xpos=0;
	int ypos=0;
	int width=0;
	int height=0;
	int z=0;
	int clipcode;
	boolean visible=false;
	idx3d_Texture img=null;
	
	
	// C O N S T R U C T O R S

		public idx3d_Particle(float x, float y, float z, idx3d_Texture img, float size)
		{
			pos=new idx3d_Vector(x,y,z);
			this.size=size;
			this.img=img;
		}
		
		public idx3d_Particle(idx3d_Vector v, idx3d_Texture img, float size)
		{
			pos=v;
			this.size=size;
			this.img=img;
		}
		
		public idx3d_Particle(idx3d_Vertex v, idx3d_Texture img, float size)
		{
			pos=v.pos;
			this.size=size;
			this.img=img;
		}
	
	// M E T H O D S
	
		public void setPos(float x, float y, float z)
		{
			pos=new idx3d_Vector(x,y,z);
		}
		
		public void setPos(idx3d_Vector v)
		{
			pos=v;
		}
		
		public idx3d_Vector getPos()
		{
			return pos;
		}
		
		public void move(float x, float y, float z)
		{
			pos.x+=x;
			pos.y+=y;
			pos.z+=z;
		}
		
		public void move(idx3d_Vector v)
		{
			pos.x+=v.x;
			pos.y+=v.y;
			pos.z+=v.z;
		}
		
		void project(idx3d_Matrix m, idx3d_Camera camera)
		{
			pos2=pos.transform(m);
			float fact=camera.screenscale/camera.fovfact/((pos2.z>0.1)?pos2.z:0.1f);
			xpos=(int)(pos2.x*fact+(camera.screenwidth>>1));
			ypos=(int)(-pos2.y*fact+(camera.screenheight>>1));
			z=(int)(65536f*pos2.z);
			width=(int)(size*fact);
			height=width;
			xpos-=width>>1;
			ypos-=height>>1;
		}
		
		void clipFrustrum(int w, int h)
		{
			// View plane clipping
			clipcode=0;
			if (xpos+width<0) clipcode|=1;
			if (xpos>=w) clipcode|=2;
			if (ypos+height<0) clipcode|=4;
			if (ypos>=h) clipcode|=8;
			if (z<0) clipcode|=16;
			visible=(clipcode==0);
		}
		
}