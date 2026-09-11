import idx3d.*;
import java.awt.*;
import java.applet.*;

public class demo14 extends idx3d_BaseApplet
{
	//int[] colors={0xFF,0xFF00,0xFFFF00,0xFF0000};
	//int[] colors={0x00,0x99,0x330066,0xFFFFFF};
	int[] colors={0x000000,0x663300,0x996633,0xffffcc};

	int[] palette=idx3d_Color.makeGradient(colors,256);
	
	public void prepareScene()
	{
		// Particle Cluster with Glows
			
			idx3d_Texture blueglow=idx3d_TextureFactory.createGlow(256,256,0x6600CC);
			idx3d_Texture orangeglow=idx3d_TextureFactory.createGlow(256,256,0xCC6600);
			idx3d_ParticleCluster cluster=new idx3d_ParticleCluster();
			for (int i=0;i<16;i++)
			{
				cluster.addParticle(new idx3d_Particle(idx3d_Vector.random(2),blueglow,0.8f));
				cluster.addParticle(new idx3d_Particle(idx3d_Vector.random(2),orangeglow,0.8f));
			}
			cluster.setAdditive(true);
			//scene.addParticleCluster("GlowCluster",cluster);
			
		// Other object
		
			idx3d_Material crystal=new idx3d_Material(getDocumentBase(),"glass.material");
			crystal.setReflectivity(255);
			scene.addMaterial("Crystal",crystal);
			
			
			idx3d_Vector[] path=new idx3d_Vector[32];
			path[0]=new idx3d_Vector(0,1f,0);
			path[path.length-1]=new idx3d_Vector(0,-1f,0);
			
			for (int i=1;i<path.length-1;i++)			
				path[i]=new idx3d_Vector(0.5f+0.4f*idx3d_Math.fractalNoise((float)2*i/path.length,1),1-2f*i/path.length,0);
			
			scene.addObject("Weird",idx3d_ObjectFactory.ROTATIONOBJECT(path,24));
			scene.object("Weird").rotate(0.5f,0f,0f);
			scene.object("Weird").setMaterial(scene.material("Crystal"));
			scene.object("Weird").removeDuplicateVertices();
						
		// New Filter
		/*
			class  inverter extends idx3d_FXPlugin
			{
				public inverter(idx3d_Scene scene)
				{
					super(scene);
				}
				
				public void apply()
				{
					int rel;
					
					for (int i=0;i<screen.pixel.length;i++)
					{
						rel=255*(i%screen.width)/screen.width;
						//screen.pixel[i]=idx3d_Color.inv(screen.pixel[i]);
						//screen.pixel[i]=idx3d_Color.transparency(screen.pixel[i],idx3d_Color.inv(screen.pixel[i]),255-(screen.pixel[i]&255));
						screen.pixel[i]=palette[idx3d_Color.getAverage(screen.pixel[i])];
					}
				}
			};
			
			plugin=new inverter(scene);		
		*/

	}
		
	public void runtime()
	{
		if (autorotation) scene.rotate(0.02f,0.07f,-0.04f);
		
	}
	
	public void keyEvent(char key)
	{
	}
	


}
