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

import java.util.Vector;

public class idx3d_ParticleCluster extends idx3d_CoreObject
{
	public boolean additive=false;
	private float size;
	private idx3d_Texture img=null;
	public Vector particleData=new Vector();
	
	// C O N S T R U C T O R S

		public idx3d_ParticleCluster()
		{		
		}
		
		public idx3d_ParticleCluster(idx3d_Texture particleImage, float particleSize)
		{
			img=particleImage;
			size=particleSize;			
		}
		
		public idx3d_ParticleCluster(idx3d_Object obj, idx3d_Texture particleImage, float particleSize)
		// Imports the verticles of the given object as particles
		{
			img=particleImage;
			size=particleSize;
			obj.rebuild();
			for(int i=0;i<obj.vertices;i++)
				addParticle(obj.vertex[i]);		
		}
	
	// M E T H O D S
	
		public idx3d_ParticleCluster setAdditive(boolean additive)
		{
			this.additive=additive;
			return this;
		}
			
		public java.util.Enumeration elements()
		{
			return particleData.elements();
		}
	
		public void setParticleSize(float size)
		{
			this.size=size;
		}
		
		public float getParticleSize()
		{
			return size;
		}
		
		public void setImage(idx3d_Texture t)
		{
			img=t;
		}
		
		public idx3d_Texture getImage()
		{
			return img;
		}
	
	// Particle data structures
	
		public void addParticle(idx3d_Particle p)
		{
			particleData.addElement(p);
		}
		
		public void addParticle(float x, float y, float z)
		{
			particleData.addElement(new idx3d_Particle(x,y,z,img,size));
		}
		
		public void addParticle(idx3d_Vector v)
		{
			particleData.addElement(new idx3d_Particle(v,img,size));
		}
		
		public void addParticle(idx3d_Vector v, idx3d_Texture img, float size)
		{
			particleData.addElement(new idx3d_Particle(v,img,size));
		}
		
		public void addParticle(idx3d_Vertex v)
		{
			particleData.addElement(new idx3d_Particle(v,img,size));
		}
		
		public void removeParticle(idx3d_Particle p)
		{
			particleData.removeElement(p);
		}
		
		public void removeParticle(int id)
		{
			particleData.removeElementAt(id);
		}
		
		public int particles()
		{
			return particleData.size();
		}
		
		public idx3d_Particle particle(int id)
		{
			return (idx3d_Particle)particleData.elementAt(id);
		}
		
	// Helper methods
	
		public idx3d_Particle[] particleArray()
		{
			idx3d_Particle[] p=new idx3d_Particle[particles()];
			java.util.Enumeration enumeration=particleData.elements();
			int id=0;
			while(enumeration.hasMoreElements())
				p[id++]=(idx3d_Particle)enumeration.nextElement();
			return p;
		}
		
		public void tilt(float factor)
		{
			idx3d_Particle[] p=particleArray();
			for (int i=0;i<p.length;i++)
				p[i].move(idx3d_Vector.random(factor));
		}
			
}