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

package idx3d.implicit;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;

public class KFMTesselator
// Kohonen feature map tesselator
{
	Centroid[][] weightMatrix=null;
	idx3d_Vector[] trainingValues=null;
	float maxLearningRate=0.8f;
	float minLearningRate=0.1f;
	float learningRate;
	float maxActivationRadius=8f;
	float minActivationRadius=0.1f;
	float activationRadius;
	int mapsize;
	int iterations;
	int step=0;
	int precision=5;
	
	// Map type
	// 0=cyclic map
	// 1=2d kohonen map
	// 2=cyclic strip	
	public int networkType=1;
	
	// C O N S T R U C T O R S

		public KFMTesselator(ImplicitSurface surface, int neurons, int mapsize)
		{
			iterations=neurons*precision;
			this.mapsize=mapsize;
			weightMatrix=new Centroid[mapsize][mapsize];
			randomizeWeightMatrix(weightMatrix,surface);
			trainingValues=new idx3d_Vector[neurons];
			uniformTrainingValues(surface);
		}
		
		public KFMTesselator(ImplicitSurface surface, int mapsize)
		{
			this.mapsize=mapsize;
			iterations=mapsize*mapsize*precision;
			
			weightMatrix=new Centroid[mapsize][mapsize];
			randomizeWeightMatrix(weightMatrix,surface);	
			gridInitTrainingValues(surface,2*mapsize);
		}
		
		public KFMTesselator(idx3d_Object obj, int mapsize)
		{
			this.mapsize=mapsize;
			obj.rebuild();
			iterations=obj.vertices*precision;
			weightMatrix=new Centroid[mapsize][mapsize];
			randomizeWeightMatrix();
			trainingValues=new idx3d_Vector[obj.vertices];
			for (int i=0;i<obj.vertices;i++) trainingValues[i]=obj.vertex[i].pos;
		}
	
	// M E T H O D S
			
		public void fullTrain()
		{
			while (!finishedLearning())
				train(1);
		}
			
		public void train(int cycles)
		{
			int randomId;
			for (int i=0;i<cycles;i++)
			{
				randomId=(int)idx3d_Math.random(0,trainingValues.length);
				train(trainingValues[randomId]);
				step++;
			}					
		}

		private void train(idx3d_Vector input)
		{
			float completed=(float)step/(float)iterations;
			activationRadius=minActivationRadius+(maxActivationRadius-minActivationRadius)*(1f-completed);
			learningRate=minLearningRate+(maxLearningRate-minLearningRate)*(1f-completed);
			if (finishedLearning()) return;
			
			Centroid winner=getWinnerCentroid(input);
			learn(winner,input);
		}
		
		public boolean finishedLearning()
		{
			return step>iterations;
		}
		
		private void learn(Centroid winner, idx3d_Vector input)
		{
			float feedback;
			
			Centroid current;
			
			for (int i=0;i<weightMatrix.length;i++)
				for (int j=0;j<weightMatrix[0].length;j++)
				{
					current=weightMatrix[i][j];
					feedback=learningRate*getFeedback(winner,current);
					current.x+=feedback*(input.x-current.x);
					current.y+=feedback*(input.y-current.y);
					current.z+=feedback*(input.z-current.z);								
				}
		}
		
		private float getFeedback(Centroid winner, Centroid centroid)
		{
			float dist=(float)getDist(winner,centroid);
			return (float)Math.exp(-0.5f*dist*dist/(activationRadius*activationRadius));
		}
		
		private Centroid getWinnerCentroid(idx3d_Vector input)
		{
			Centroid winner=weightMatrix[0][0];
			
			float mindist=idx3d_Vector.distance(input,winner);
			float dist;
			
			for (int i=0;i<mapsize;i++)
				for (int j=0;j<mapsize;j++)
				{
					dist=idx3d_Vector.distance(input,weightMatrix[i][j]);
					if (dist<mindist)
					{
						winner=weightMatrix[i][j];
						mindist=dist;
					}
				}
			return winner;
		}
		
		public void randomizeWeightMatrix(Centroid[][] weightMatrix, ImplicitSurface surface)
		{
			for (int i=0;i<mapsize;i++)
				for (int j=0;j<mapsize;j++)
					weightMatrix[i][j]=new Centroid(i,j,getRandomVector(surface.getCenter(),surface.getDimension()));
					
		}
		
		public void randomizeWeightMatrix()
		{
			for (int i=0;i<mapsize;i++)
				for (int j=0;j<mapsize;j++)
					weightMatrix[i][j]=new Centroid(i,j,idx3d_Vector.random(1f));
					
		}
		
		private float getDist(Centroid winner, Centroid centroid)
		{
			if (networkType==0)
			// 2d cyclic map
			{
				int a=(winner.a-centroid.a+mapsize)%mapsize;
				a=(a<mapsize-a) ? a : mapsize-a;
				int b=(winner.b-centroid.b+mapsize)%mapsize;
				b=(b<mapsize-b) ? b : mapsize-b;
				
				return a+b;
			}
			
			if (networkType==1)
			// 2d kohonen map
			{
				int a=winner.a>centroid.a ? winner.a-centroid.a : centroid.a-winner.a;
				int b=winner.b>centroid.b ? winner.b-centroid.b : centroid.b-winner.b;
				return a+b;
			}
			
			if (networkType==2)
			// cyclic patch
			{
				int a=winner.a+winner.b*mapsize;
				int b=centroid.a+centroid.b*mapsize;
				int n=mapsize*mapsize;
				int temp=(a-b+n)%n;
				return(float)((idx3d_Math.min(temp,n-temp)+1)>>1);
			}
			
			return 0;
		}
		
		private void gridInitTrainingValues(ImplicitSurface surface, int gridsize)
		{
			Vector neurons=new Vector();
			float x,y,z,dx,dy,dz,xBase,yBase,zBase;
			idx3d_Vector c=surface.getCenter();
			idx3d_Vector d=surface.getDimension();
			
			xBase=c.x-d.x/2;
			yBase=c.y-d.y/2;
			zBase=c.z-d.z/2;
			dx=d.x/(float)gridsize;
			dy=d.y/(float)gridsize;
			dz=d.z/(float)gridsize;
			idx3d_Vector v1,v2;
			boolean i1,i2;
			
			x=xBase;
			for (int i=0;i<gridsize;i++)
			{
				y=yBase;
				for (int j=0;j<gridsize;j++)
				{
					z=zBase;
					for (int k=0;k<gridsize;k++)
					{
						v1=new idx3d_Vector(x,y,z);
						v2=new idx3d_Vector(x+dx,y+dy,z+dz);
						i1=surface.inside(v1);
						i2=surface.inside(v2);
						
						if (i1 != i2)
						{
							if (i1) neurons.addElement(collapse(v1,v2,surface));
							else neurons.addElement(collapse(v2,v1,surface));
						}
						
						z+=dz;
					}
					y+=dy;
				}
				x+=dx;
			}
			
			trainingValues=new idx3d_Vector[neurons.size()];
			Enumeration enumeration=neurons.elements();
			int pos=0;
			while (enumeration.hasMoreElements())
				trainingValues[pos++]=(idx3d_Vector)enumeration.nextElement();
		}
		
			
		private idx3d_Vector getRandomVector(idx3d_Vector center, idx3d_Vector dim)
		{
			return new idx3d_Vector(
				idx3d_Math.random(dim.x/2)+center.x,
				idx3d_Math.random(dim.y/2)+center.y,
				idx3d_Math.random(dim.z/2)+center.z);
		}
			
		private idx3d_Vector randomVectorInside(ImplicitSurface surface)
		{
			idx3d_Vector v;
			do v=getRandomVector(surface.getCenter(),surface.getDimension());
			while (!surface.inside(v));
			return v;
		}
		
		private idx3d_Vector randomVectorOutside(ImplicitSurface surface)
		{
			idx3d_Vector v;
			do v=getRandomVector(surface.getCenter(),surface.getDimension());
			while (surface.inside(v));
			return v;
		}
		
		private void uniformTrainingValues(ImplicitSurface surface)
		{
			for (int i=0;i<trainingValues.length;i++)
				trainingValues[i]=getUniformCollapsedNeuron(surface);
		}
			
		private idx3d_Vector getUniformCollapsedNeuron(ImplicitSurface surface)
		{
			return collapse(randomVectorInside(surface),randomVectorOutside(surface),surface);
		}
		
		private idx3d_Vector collapse(idx3d_Vector inside, idx3d_Vector outside, ImplicitSurface surface)
		{
			idx3d_Vector neuron=idx3d_Vector.center(inside,outside);
			idx3d_Vector i=inside;
			idx3d_Vector o=outside;
			for (int k=0;k<24;k++)
			{
				if (surface.inside(neuron)) i=neuron;
				else o=neuron;
				neuron=idx3d_Vector.center(i,o);
			}
			 return neuron;
		}
			
		
		
	// Mesh / Cluster generation
		
		public idx3d_ParticleCluster getNeurons(idx3d_Texture img, float size)
		{
			idx3d_Object obj=new idx3d_Object();
			for (int i=0;i<trainingValues.length;i++)
				obj.addVertex(new idx3d_Vertex(trainingValues[i]));
				
			return new idx3d_ParticleCluster(obj,img,size);
		}
		
		public idx3d_ParticleCluster getCentroids(idx3d_Texture img, float size)
		{
			idx3d_Object obj=new idx3d_Object();
			for (int i=0;i<weightMatrix.length;i++)
				for (int j=0;j<weightMatrix[0].length;j++)
					obj.addVertex(new idx3d_Vertex(weightMatrix[i][j]));
				
			return new idx3d_ParticleCluster(obj,img,size);
		}
		
		public idx3d_Object getObject()
		{
			idx3d_Object obj=new idx3d_Object();
			
			if (networkType==1)
			// 2d kohonen map
			{
				idx3d_Vertex[][] v=new idx3d_Vertex[mapsize][mapsize];
				
				for (int a=0;a<mapsize;a++)
					for (int b=0;b<mapsize;b++)
					{
						v[a][b]=new idx3d_Vertex(weightMatrix[a][b]);
						obj.addVertex(v[a][b]);
					}
					
				
				idx3d_Vertex p1,p2,p3,p4;
				
				for (int a=0;a<mapsize-1;a++)
					for (int b=0;b<mapsize-1;b++)
					{
						p1=v[a%mapsize][b%mapsize];
						p2=v[(a+1)%mapsize][b%mapsize];
						p3=v[(a+1)%mapsize][(b+1)%mapsize];
						p4=v[a%mapsize][(b+1)%mapsize];
						
						obj.addTriangle(p1,p2,p3);
						obj.addTriangle(p1,p3,p4);
					}
			}
			
			if (networkType==0)
			// 2d cyclic map
			{
				idx3d_Vertex[][] v=new idx3d_Vertex[mapsize][mapsize];
				
				for (int a=0;a<mapsize;a++)
					for (int b=0;b<mapsize;b++)
					{
						v[a][b]=new idx3d_Vertex(weightMatrix[a][b]);
						obj.addVertex(v[a][b]);
					}
					
				
				idx3d_Vertex p1,p2,p3,p4;
				
				for (int a=0;a<mapsize;a++)
					for (int b=0;b<mapsize;b++)
					{
						p1=v[a%mapsize][b%mapsize];
						p2=v[(a+1)%mapsize][b%mapsize];
						p3=v[(a+1)%mapsize][(b+1)%mapsize];
						p4=v[a%mapsize][(b+1)%mapsize];
						
						obj.addTriangle(p1,p2,p3);
						obj.addTriangle(p1,p3,p4);
					}
			}
			
			if (networkType==2)
			// cyclic strip
			{
				idx3d_Vertex[][] v=new idx3d_Vertex[mapsize][mapsize];
				
				for (int a=0;a<mapsize;a++)
					for (int b=0;b<mapsize;b++)
					{
						v[a][b]=new idx3d_Vertex(weightMatrix[a][b]);
						obj.addVertex(v[a][b]);
					}
					
				int centroids=mapsize*mapsize;
				for (int i=0;i<centroids;i++)
				{
					if (i%2==0)
					{
						obj.addTriangle(
							v[i%mapsize][i/mapsize],
							v[(i+1)%mapsize][((i+1)/mapsize)%mapsize],
							v[(i+2)%mapsize][((i+2)/mapsize)%mapsize]);
					}
					else
					{
						obj.addTriangle(
							v[i%mapsize][i/mapsize],
							v[(i+2)%mapsize][((i+2)/mapsize)%mapsize],
							v[(i+1)%mapsize][((i+1)/mapsize)%mapsize]);
					}
				}
			}
			
			
			return obj;
		}
		
}

class Centroid extends idx3d_Vector
{
	int a,b;
	
	public Centroid(int a, int b, idx3d_Vector v)
	{
		super(v.x,v.y,v.z);
		this.a=a;
		this.b=b;	
	}	
	
	public String toString()
	{
		return "Centroid("+a+","+b+")  "+super.toString();
	}
	
}
