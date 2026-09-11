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

package idx3d;

import java.awt.Image;
import java.util.Vector;
import java.util.Enumeration;

public class idx3d_RenderPipeline
{
	// F I E L D S

		public idx3d_Screen screen;
		idx3d_Scene scene;
		public idx3d_Lightmap lightmap;

		private boolean resizingRequested=false;
		private boolean antialiasChangeRequested=false;
		private int requestedWidth;
		private int requestedHeight;
		private boolean requestedAntialias;
		boolean useIdBuffer=false;
		
		idx3d_Rasterizer rasterizer;
		Vector opaqueQueue=new Vector();
		Vector transparentQueue=new Vector();
		Vector particleQueue=new Vector();
		

	// Q U I C K  R E F E R E N C E S
	
		final int zFar=0xFFFFFFF;

	// B U F F E R S

		public int zBuffer[];
		public int idBuffer[];


	// C O N S T R U C T O R S

		public idx3d_RenderPipeline(idx3d_Scene scene, int w, int h)
		{
			this.scene=scene;
			screen=new idx3d_Screen(w,h);
			zBuffer=new int[screen.w*screen.h];
			rasterizer=new idx3d_Rasterizer(this);
		}
		

	// P U B L I C   M E T H O D S
	
		public void setAntialias(boolean antialias)
		{
			antialiasChangeRequested=true;
			requestedAntialias=antialias;
		}

		public float getFPS()
		{
			return (float)((int)(screen.FPS*100))/100;
		}
		
		public void resize(int w, int h)
		{
			resizingRequested=true;
			requestedWidth=w;
			requestedHeight=h;
		}
		
		public void buildLightMap()
		{
			if (lightmap==null) lightmap=new idx3d_Lightmap(scene);
			else lightmap.rebuildLightmap();
			rasterizer.loadLightmap(lightmap);
		}
		

		public final void render(idx3d_Camera cam)
		{
			if (scene.width<=0 || scene.height<=0 ) return;
			
			// Resize if requested
				if (resizingRequested) performResizing();
				if (antialiasChangeRequested) performAntialiasChange();
				rasterizer.rebuildReferences(this);
			
			// Clear buffers	
				idx3d_Math.clearBuffer(zBuffer,zFar);
				if (useIdBuffer) idx3d_Math.clearBuffer(idBuffer,-1);
				if (scene.environment.background!=null)
					screen.drawBackground(scene.environment.background,screen.w,screen.h);
				else screen.clear(scene.environment.bgcolor);
				
			// Prepare
				cam.setScreensize(screen.w,screen.h);
				scene.prepareForRendering();
				emptyQueues();
			
			// Project
				
				idx3d_Matrix m=idx3d_Matrix.multiply(cam.getMatrix(),scene.matrix);
				idx3d_Matrix nm=idx3d_Matrix.multiply(cam.getNormalMatrix(),scene.normalmatrix);
				idx3d_Matrix vertexProjection,normalProjection;
				idx3d_Object obj;
				idx3d_Triangle t;
				idx3d_Vertex v;
				int w=screen.w;
				int h=screen.h;
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
						{
							t=obj.triangle[i];
							t.project(normalProjection);
							t.clipFrustrum(w,h);
							enqueueTriangle(t);
						}				
					}
				}
				
				idx3d_Triangle[] tri;
				
				tri=getOpaqueQueue();
				if (tri!=null) for (int i=tri.length-1;i>=0;i--)
				{
					rasterizer.loadMaterial(tri[i].parent.material);
					rasterizer.render(tri[i]);
				}
				
				tri=getTransparentQueue();
				if (tri!=null) for (int i=0;i<tri.length;i++)
				{
					rasterizer.loadMaterial(tri[i].parent.material);
					rasterizer.render(tri[i]);
				}
				
				Enumeration particleSystems=scene.particleClusterData.elements();
				Enumeration particles;
				idx3d_ParticleCluster pc;
				idx3d_Particle p;
				idx3d_Matrix projection;
				idx3d_Texture img;
				while (particleSystems.hasMoreElements())
				{
					pc=(idx3d_ParticleCluster)particleSystems.nextElement();
					projection=pc.matrix.getClone();
					projection.transform(m);
					particles=pc.elements();
					while (particles.hasMoreElements())
					{
						p=(idx3d_Particle)particles.nextElement();
						p.project(projection,cam);
						p.clipFrustrum(w,h);
						p.additive=pc.additive;
						if (p.visible) enqueueParticle(p);
					}
				}
				
				idx3d_Particle[] pq=getParticleQueue();
				if (pq!=null) for (int i=0;i<pq.length;i++)
				{
					p=pq[i];
					if (p.additive) screen.add(p.img,p.xpos,p.ypos,p.width,p.height);	
					else screen.drawSprite(p.img,p.xpos,p.ypos,p.width,p.height);
					/*
					try
					{
						if (zBuffer[p.xpos+screen.width*p.ypos]>p.z)
						{
							if (p.additive) screen.add(p.img,p.xpos,p.ypos,p.width,p.height);	
							else screen.drawSprite(p.img,p.xpos,p.ypos,p.width,p.height);
						}
					}
					catch(ArrayIndexOutOfBoundsException e) 
					{
						if (p.additive) screen.add(p.img,p.xpos,p.ypos,p.width,p.height);	
						else screen.drawSprite(p.img,p.xpos,p.ypos,p.width,p.height);	
					}
					*/
				}
				screen.render();
			
		}
		
		public void useIdBuffer(boolean useIdBuffer)
		{
			this.useIdBuffer=useIdBuffer;
			if (useIdBuffer) idBuffer=new int[screen.w*screen.h];
			else idBuffer=null;
		}
		

	// P R I V A T E   M E T H O D S

		private void performResizing()
		{
			resizingRequested=false;
			screen.resize(requestedWidth,requestedHeight);
			zBuffer=new int[screen.w*screen.h];
			if (useIdBuffer) idBuffer=new int[screen.w*screen.h];
		}

		private void performAntialiasChange()
		{
			antialiasChangeRequested=false;
			screen.setAntialias(requestedAntialias);
			zBuffer=new int[screen.w*screen.h];
			if (useIdBuffer) idBuffer=new int[screen.w*screen.h];
		}
		
	// Triangle sorting
	
		private void emptyQueues()
		{
			opaqueQueue.removeAllElements();
			transparentQueue.removeAllElements();
			particleQueue.removeAllElements();
		}
	
		private void enqueueTriangle(idx3d_Triangle tri)
		{
			if (tri.parent.material==null) return;
			if (!tri.visible) return;
			if ((tri.parent.material.transparency==255)&&(tri.parent.material.reflectivity==0)) return;
			
			if (tri.parent.material.transparency>0) transparentQueue.addElement(tri);
			else opaqueQueue.addElement(tri);
		}
		
		private void enqueueParticle(idx3d_Particle p)
		{
			if (p.img==null) return;
			if (!p.visible) return;
			particleQueue.addElement(p);
		}
		
		private idx3d_Triangle[] getOpaqueQueue()
		{
			if (opaqueQueue.size()==0) return null;
			Enumeration enumeration=opaqueQueue.elements();
			idx3d_Triangle[] tri=new idx3d_Triangle[opaqueQueue.size()];
			
			int id=0;
			while (enumeration.hasMoreElements())
				tri[id++]=(idx3d_Triangle)enumeration.nextElement();				
			
			return sortTriangles(tri,0,tri.length-1);
		}
		
		private idx3d_Triangle[] getTransparentQueue()
		{
			if (transparentQueue.size()==0) return null;
			Enumeration enumeration=transparentQueue.elements();
			idx3d_Triangle[] tri=new idx3d_Triangle[transparentQueue.size()];
			
			int id=0;
			while (enumeration.hasMoreElements())
				tri[id++]=(idx3d_Triangle)enumeration.nextElement();				
			
			return sortTriangles(tri,0,tri.length-1);
		}
		
		private idx3d_Particle[] getParticleQueue()
		{
			if (particleQueue.size()==0) return null;
			Enumeration enumeration=particleQueue.elements();
			idx3d_Particle[] p=new idx3d_Particle[particleQueue.size()];
			
			int id=0;
			while (enumeration.hasMoreElements())
				p[id++]=(idx3d_Particle)enumeration.nextElement();				
			
			return sortParticles(p,0,p.length-1);
		}
		
		private idx3d_Triangle[] sortTriangles(idx3d_Triangle[] tri, int L, int R)
		{
			float m=(tri[L].dist+tri[R].dist)/2;
			int i=L;
			int j=R;
			idx3d_Triangle temp;
			
			do
			{
				while (tri[i].dist>m) i++;
				while (tri[j].dist<m) j--;
				
				if (i<=j)
				{
					temp=tri[i];
					tri[i]=tri[j];
					tri[j]=temp;
					i++;
					j--;
				}
			}		
			while (j>=i);
			
			if (L<j) sortTriangles(tri,L,j);
			if (R>i) sortTriangles(tri,i,R);
			
			return tri;
		}
		
		private idx3d_Particle[] sortParticles(idx3d_Particle[] p, int L, int R)
		{
			float m=(p[L].z+p[R].z)/2;
			int i=L;
			int j=R;
			idx3d_Particle temp;
			
			do
			{
				while (p[i].z>m) i++;
				while (p[j].z<m) j--;
				
				if (i<=j)
				{
					temp=p[i];
					p[i]=p[j];
					p[j]=temp;
					i++;
					j--;
				}
			}		
			while (j>=i);
			
			if (L<j) sortParticles(p,L,j);
			if (R>i) sortParticles(p,i,R);
			
			return p;
		}
		
		public void renderZBuffer()
		{
			for (int i=zBuffer.length-1;i>=0;i--)
				screen.p[i]=0xFF000000|idx3d_Color.gray(255-(zBuffer[i]>>9));
			screen.render();
		}		
}
