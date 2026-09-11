import idx3d.*;
import idx3d.implicit.*;
import java.awt.*;
import java.applet.*;

public final class implicitTest extends idx3d_BaseApplet
{
	KFMTesselator tesselator;
	idx3d_Texture img,img2;
	boolean training=false;
	BlobSurface surface;
	idx3d_Material meshMaterial=null;
	int learningrate=1;
	
	public void prepareScene()
	{
		// Setup Surface
		
			idx3d_Vector center=new idx3d_Vector(0f,0f,0f);
			idx3d_Vector dim=new idx3d_Vector(4f,4f,4f);
			surface=new BlobSurface(center, dim);
			surface.setIsolevel(6f);
			
			
			surface.addBlob(new idx3d_Vector(0.4f,0.4f,0.4f),0.5f);
			surface.addBlob(new idx3d_Vector(-0.6f,0.2f,-0.8f),0.8f);
			surface.addBlob(new idx3d_Vector(0.6f,-0.2f,-0.4f),1.8f);
			surface.addBlob(new idx3d_Vector(-1.2f,0f,0f),2.4f);
			surface.addBlob(new idx3d_Vector(-0.6f,-0.8f,0f),3f);
			surface.addBlob(new idx3d_Vector(1f,1f,1f),2f);
			
			
		// Prepare Scene
		
			scene.scale(0.5f);
			active=false;
			
			img=new idx3d_Texture(getDocumentBase(),"textures/particle1.jpg");
			img.setCircleAlpha();
			img2=new idx3d_Texture(getDocumentBase(),"textures/particle2.jpg");
			img2.setCircleAlpha();
			
			tesselator=new KFMTesselator(surface,1000,20);
			
			idx3d_Material m1=new idx3d_Material(0xFFFFFF);
			scene.addMaterial("Default",m1);
			meshMaterial=m1;
			
			idx3d_Material m2=new idx3d_Material(new idx3d_Texture(getDocumentBase(),"textures/texture.jpg"));
			scene.addMaterial("Texture",m2);
			 
			idx3d_Material m3=new idx3d_Material(0);
			m3.setFlat(true);
			m3.setWireframe(true);
			scene.addMaterial("Wireframe",m3);
			
			scene.setAmbient(0x333333);
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0f,0f,1f),0xFFFFFF,200,64));			
			viewNeuronsAndCentroids();
	}
	
	public void marchingCubes()
	{
		idx3d_Object obj=idx3d.implicit.MarchingCubes.generateIsosurface(20,surface);
		obj.setMaterial(meshMaterial);
		
		scene.removeAllObjects();
		scene.removeParticleCluster("Neurons");
		scene.removeParticleCluster("Centroids");
		
		scene.addObject("MarchingCubes",obj);
	}
	
	public void randomBlobs()
	{
		idx3d_Vector center=new idx3d_Vector(0f,0f,0f);
		idx3d_Vector dim=new idx3d_Vector(4f,4f,4f);
			
		surface=new BlobSurface(center, dim);
		surface.setIsolevel(idx3d_Math.random(0.32f,1f));
		for (int i=0;i<16;i++)
			surface.addBlob(idx3d_Vector.random(1f,1f,1f),idx3d_Math.random(-0.5f,0.5f));
		
		tesselator=new KFMTesselator(surface,2000,20);
	}

	public void runtime()
	{				
		if (active&&autorotation)
		{
			float speed=0.64f;
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/2000)/8;
			scene.rotate(speed*dx,speed*dy,0f);
		}
		
		if (training)
		{	
			tesselator.train(learningrate); 
			viewCentroids();
			if (tesselator.finishedLearning()) training=false;
		}
	}
	

	public void keyEvent(char key)
	{
		if (key=='1') viewNeurons();
		if (key=='2') viewCentroids();
		if (key=='3') viewNeuronsAndCentroids();
		if (key=='4') viewMesh();
		if (key=='5') marchingCubes();
		if (key=='d') learningrate=1;
		if (key=='f') learningrate=10;
		if (key=='g') learningrate=50;
		if (key=='q') meshMaterial.setFlat(!meshMaterial.isFlat());
		if (key=='c') scene.setBackgroundColor(0);
		if (key=='v') scene.setBackgroundColor(0xFFFFFF); 
		if (key=='m')
		{
			scene.setBackgroundColor(0);
			meshMaterial=scene.material("Default");
			for (int i=0;i<scene.objects;i++) 
				scene.object[i].setMaterial(meshMaterial);
		}
		if (key=='n')
		{
			scene.setBackgroundColor(0); 
			meshMaterial=scene.material("Texture");
			for (int i=0;i<scene.objects;i++) 
			{
				idx3d_TextureProjector.projectFrontal(scene.object[i]);
				scene.object[i].setMaterial(meshMaterial);
			}
			
		}
		if (key=='b')
		{
			scene.setBackgroundColor(0xFFFFFF); 
			meshMaterial=scene.material("Wireframe");
			for (int i=0;i<scene.objects;i++) 
				scene.object[i].setMaterial(meshMaterial);
			
			idx3d_Material m=meshMaterial.getClone();
			m.setColor(0xAAAAAA);
			for (int i=0;i<scene.objects;i++) 
				scene.addObject(
					scene.object[i].name+" [clone]",
					scene.object[i].getClone().flipNormals().setMaterial(m));

		}
		
		if (key=='t') { training=!training; }
		if (key=='r') { randomBlobs(); viewNeuronsAndCentroids();}
		if (key=='w')
		{
			idx3d_Material m=meshMaterial.getClone();
			m.setColor(0xCCCCCC);
			for (int i=0;i<scene.objects;i++) 
				scene.addObject(
					scene.object[i].name+" [clone]",
					scene.object[i].getClone().flipNormals().setMaterial(m));
		}
		
		if (key=='0') { tesselator.networkType=0; }
		
		return;
	}
	
	private void viewNeurons()
	{
		scene.removeAllObjects();
		scene.removeParticleCluster("Neurons");
		scene.removeParticleCluster("Centroids");
		
		scene.addParticleCluster("Neurons",tesselator.getNeurons(img,0.05f));
	}
	
	private void viewCentroids()
	{
		scene.removeAllObjects();
		scene.removeParticleCluster("Neurons");
		scene.removeParticleCluster("Centroids");
		
		scene.addParticleCluster("Centroids",tesselator.getCentroids(img2,0.04f));
	}
	
	private void viewNeuronsAndCentroids()
	{
		scene.removeAllObjects();
		scene.removeParticleCluster("Neurons");
		scene.removeParticleCluster("Centroids");
		
		scene.addParticleCluster("Neurons",tesselator.getNeurons(img,0.05f));
		scene.addParticleCluster("Centroids",tesselator.getCentroids(img2,0.04f));
	}
	
	private void viewMesh()
	{
		scene.removeAllObjects();
		scene.removeParticleCluster("Neurons");
		scene.removeParticleCluster("Centroids");
		
		scene.addObject("Mesh",tesselator.getObject());
		scene.rebuild();
		for (int i=0;i<scene.objects;i++) 
		{
			idx3d_TextureProjector.projectFrontal(scene.object[i]);
			scene.object[i].setMaterial(meshMaterial);
		}
	}
	
}
