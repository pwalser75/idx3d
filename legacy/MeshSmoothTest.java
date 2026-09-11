import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class MeshSmoothTest extends Applet 
{
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
			
			scene.setBackgroundColor(0xFFFFFF);
				
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
				
			// Create Object
				
				try{
					new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),getParameter("mesh")),scene);
					scene.rebuild();
					scene.normalize();
				}
				catch(Exception e)
				{
					scene.addObject("Object1",idx3d_ObjectFactory.createSphere(0));
					//scene.addObject("Object1",idx3d_ObjectFactory.CUBE(1));
				}
					
				scene.rebuild();
				for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Wireframe"));
				for(int i=0;i<scene.objects;i++) idx3d_TextureProjector.projectFrontal(scene.object[i]);
				scene.scale(0.88f);		
	}

	public synchronized void paint(Graphics g)
	{
		repaint();
	}

	public synchronized void update(Graphics g)
	{
		scene.render();
		g.drawImage(scene.getImage(),0,0,this);
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
		if (key==32) { System.out.println(scene.getFPS()+""); }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.1f); }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.1f); }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.1f,0f); }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.1f,0f); }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.1f,0f,0f); }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.1f,0f,0f); }
		if ((char)key=='+') {scene.scale(1.2f); }
		if ((char)key=='-') {scene.scale(0.8f); }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); }
		
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); }
		if ((char)key=='l') {new LightMapViewer(scene); }
		
		if ((char)key=='s') halted=!halted;
		if ((char)key=='t')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].tilt(0.05f);
		}
		
		if ((char)key=='1')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Wireframe"));
		}
		if ((char)key=='2')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Flat"));
		}
		if ((char)key=='3')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Blank"));
		}		
		if ((char)key=='4')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Plastic"));
		}
		if ((char)key=='5')
		{
			for(int i=0;i<scene.objects;i++) scene.object[i].setMaterial(scene.material("Crystal"));
		}	
		
		if ((char)key=='m')
		{
			for(int i=0;i<scene.objects;i++) idx3d_Toolkit.meshSmooth(scene.object[i]);
		}	
		
		repaint();
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
		repaint();
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
