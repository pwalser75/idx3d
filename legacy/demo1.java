import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo1 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean halted=false;
	boolean antialias=false;
	
	public void init()
	{
		setNormalCursor();		
					
		// BUILD SCENE
		
		scene=new idx3d_Scene(size().width,size().height);
		
		// Create background image
			
			idx3d_Texture bkgrd=new idx3d_Texture(2,2);
			bkgrd.pixel[0]=0xFF;
			bkgrd.pixel[1]=0xFF00;
			bkgrd.pixel[2]=0xFF0000;
			bkgrd.pixel[3]=0xFFFF00;
			scene.environment.setBackground(idx3d_TextureFactory.bilinearResample(bkgrd,scene.width,scene.height));
			
		// Add materials
				
			idx3d_Material crystal=new idx3d_Material(getDocumentBase(),"materials/glass.material");
			crystal.setReflectivity(255);
			scene.addMaterial("Crystal",crystal);
			
			idx3d_Material plastic=new idx3d_Material(new idx3d_Texture(getDocumentBase(),"textures/texture.jpg"));
			scene.addMaterial("Plastic",plastic);
			
			idx3d_Material wireframe=new idx3d_Material(0x000000);
			wireframe.setWireframe(true);
			wireframe.setFlat(true);
			scene.addMaterial("Wireframe",wireframe);
			
			idx3d_Material flat=new idx3d_Material(0xFFFFFF);
			flat.setFlat(true);
			scene.addMaterial("Flat",flat);
			
			idx3d_Material blank=new idx3d_Material(0xFFFFFF);
			scene.addMaterial("Blank",blank);
		
		// Add lights
		
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.4f,0.4f,1f),0x666666,640,120));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0x996600,120,40));
			scene.addLight("Light3",new idx3d_Light(new idx3d_Vector(0.8f,-0.3f,1f),0x336699,240,80));			
			scene.addLight("Light4",new idx3d_Light(new idx3d_Vector(-0.5f,1f,1f),0x441166,200,80));
			
		// Create Torus as a Lattice Object from a circle path
			
			idx3d_Vector[] path=new idx3d_Vector[9];
			
			path[0]=new idx3d_Vector(0.4f,0.0f,0);
			path[1]=new idx3d_Vector(0.6f,0.3f,0);
			path[2]=new idx3d_Vector(0.8f,0.4f,0);
			path[3]=new idx3d_Vector(0.9f,0.3f,0);
			path[4]=new idx3d_Vector(1.0f,0.0f,0);
			path[5]=new idx3d_Vector(0.9f,-0.3f,0);
			path[6]=new idx3d_Vector(0.8f,-0.4f,0);
			path[7]=new idx3d_Vector(0.6f,-0.3f,0);
			path[8]=new idx3d_Vector(0.4f,0.0f,0);
	
			scene.addObject("Torus",idx3d_ObjectFactory.ROTATIONOBJECT(path,20));
			scene.object("Torus").rotate(4.2f,0.2f,-0.5f);
			scene.object("Torus").shift(-0.5f,0f,0f);
			scene.object("Torus").scale(0.72f);
			scene.object("Torus").setMaterial(scene.material("Plastic"));
			
		// Create Wineglass as a Lattice Object
		
			path=new idx3d_Vector[15];
			path[0]=new idx3d_Vector(0.0f,0.2f,0);
			path[1]=new idx3d_Vector(0.13f,0.25f,0);
			path[2]=new idx3d_Vector(0.33f,0.3f,0);
			path[3]=new idx3d_Vector(0.43f,0.6f,0);
			path[4]=new idx3d_Vector(0.48f,0.9f,0);
			path[5]=new idx3d_Vector(0.5f,0.9f,0);
			path[6]=new idx3d_Vector(0.45f,0.6f,0);
			path[7]=new idx3d_Vector(0.35f,0.3f,0);
			path[8]=new idx3d_Vector(0.25f,0.2f,0);
			path[9]=new idx3d_Vector(0.1f,0.15f,0);
			path[10]=new idx3d_Vector(0.1f,0.0f,0);
			path[11]=new idx3d_Vector(0.1f,-0.5f,0);
			path[12]=new idx3d_Vector(0.35f,-0.55f,0);
			path[13]=new idx3d_Vector(0.4f,-0.6f,0);
			path[14]=new idx3d_Vector(0.0f,-0.6f,0);

			scene.addObject("Wineglass",idx3d_ObjectFactory.ROTATIONOBJECT(path,16));
			scene.object("Wineglass").rotate(0.5f,0f,0f);
			scene.object("Wineglass").setMaterial(scene.material("Crystal"));
			scene.object("Wineglass").removeDuplicateVertices();
			
		
		
		scene.defaultCamera.setFov(80);
		scene.scale(0.88f);
			
	}

	public void paint(Graphics g)
	{
		repaint();
	}

	public void start()
	{
		if (idx_Thread == null)
		{
			idx_Thread = new Thread(this);
			idx_Thread.start();
		}
	}
	
	public void stop()
	{
		if (idx_Thread != null)
		{
			idx_Thread.stop();
			idx_Thread = null;
		}
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				idx_Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				System.out.println("idx://interrupted");
			}
		}
	}

	public void repaint()
	{
		
		if (!halted && autorotation)
		{
			scene.object("Torus").rotate(0f,0.05f,0.03f);
			scene.object("Wineglass").rotate(0f,-0.08f,-0.1f);
			scene.rotate(0.04f,0.02f,0.01f);
		}
		
		scene.render();
		getGraphics().drawImage(scene.getImage(),0,0,this);
	}		

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
		return true;
   	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		setMovingCursor();
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.1f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.1f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.1f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.1f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.1f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.1f,0f,0f); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }
		if ((char)key=='l') {new LightMapViewer(scene); return true; }
		
		if ((char)key=='s') halted=!halted;
		
		
		if ((char)key=='1')
		{
			scene.object("Wineglass").setMaterial(scene.material("Wireframe"));
			scene.object("Torus").setMaterial(scene.material("Wireframe"));
		}
		if ((char)key=='2')
		{
			scene.object("Wineglass").setMaterial(scene.material("Flat"));
			scene.object("Torus").setMaterial(scene.material("Flat"));
		}
		if ((char)key=='3')
		{
			scene.object("Wineglass").setMaterial(scene.material("Blank"));
			scene.object("Torus").setMaterial(scene.material("Blank"));
		}		
		if ((char)key=='4')
		{
			scene.object("Wineglass").setMaterial(scene.material("Crystal"));
			scene.object("Torus").setMaterial(scene.material("Plastic"));
		}	
		
		if ((char)key=='m')
		{
			idx3d_Toolkit.meshSmooth(scene.object("Wineglass"));
			idx3d_Toolkit.meshSmooth(scene.object("Torus"));
		}	
		
		return true;
	}
	
	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
		
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		if (evt.modifiers == Event.META_MASK) scene.shift(-dy,-dx,0);
		else scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}
	
	private void setMovingCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);
	}
	
	private void setNormalCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);		
	}
	
	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}
	
	
}
