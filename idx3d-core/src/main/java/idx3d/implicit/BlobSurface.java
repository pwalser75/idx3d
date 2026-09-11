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
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------


package idx3d.implicit;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;

public class BlobSurface implements ImplicitSurface
{
	private idx3d_Vector center,dim;
	private float isolevel;
	
	private Vector blobData=new Vector();
	private Blob blob[]=null;
	private int blobs=0;
	private boolean needsRebuild=false;
	
	// Constructors
	
		private BlobSurface() {}
		
		public BlobSurface(idx3d_Vector center, idx3d_Vector dimension)
		{
			this.center=center;
			this.dim=dimension;
		}
		
	
	// Data structures
	
		public void addBlob(idx3d_Vector pos, float power)
		{
			blobData.addElement(new Blob(pos,power));
			needsRebuild=true;
		}
		
		private void rebuild()
		{
			if (!needsRebuild) return;
			needsRebuild=false;
			Enumeration enumeration=blobData.elements();
			blobs=blobData.size();
			blob=new Blob[blobs];
			int id=0;
			while (enumeration.hasMoreElements())
				blob[id++]=(Blob)enumeration.nextElement();
		}
		
	// Parameters
	
		public void setIsolevel(float isolevel)
		{
			this.isolevel=isolevel;
		}
		
		public void setCenter(idx3d_Vector v)
		{
			center=v;
		}
		
		public void setDimension(idx3d_Vector v)
		{
			dim=v;
		}
		
		public float getIsolevel()
		{
			return isolevel;
		}
		
	// Implicit Surface Interface Implemention	
	
		public boolean inside(idx3d_Vector v)
		{
			return getLevel(v)<isolevel;
		}
		
		private float getTorusLevel(idx3d_Vector v,float R, float r)
		{
			float x2=v.x*v.x;
			float y2=v.y*v.y;
			float z2=v.z*v.z;
			float R2=R*R;
			float r2=r*r;
			
			return x2*x2+y2*y2+z2*z2+R2*R2+2*(x2*y2+x2*z2+y2*z2-(R2+r2)*(x2+z2)+(R2-r2)*y2-R2*r2);
		}
		
		
		public float getLevel(idx3d_Vector v)
		{
			//return getTorusLevel(v,1.2f,0.3f);
			
			rebuild();
			float level=0;
			float r,rad;
			
			for (int i=0;i<blobs;i++)
			{
				rad=idx3d_Vector.sub(v,blob[i]).length();
				level+=blob[i].power/(rad*rad);
			}
			
			return level;
		}
		
		public idx3d_Vector getCenter()
		{
			return center;
		}
		
		public idx3d_Vector getDimension()
		{
			return dim;
		}
}

class Blob extends idx3d.idx3d_Vector
{
	float power;
	
	public Blob(idx3d_Vector pos, float power)
	{
		this.power=power;
		this.x=pos.x;
		this.y=pos.y;
		this.z=pos.z;
	}
}
