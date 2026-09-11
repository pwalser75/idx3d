import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class torus extends Applet implements Runnable
{
	private Thread thread;
	private idx3d_Scene scene;

	private int oldx=0;
	private int oldy=0;
	private boolean autorotation=true;
	private boolean antialias=false;
	private idx3d_Material metal, wireframe;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(size().width,size().height);
			
			idx3d_Object obj=idx3d_ObjectFactory.TORUSKNOT(2,3,0.32f,1.2f,0.48f,1.2f,100,16);
			obj.removeDuplicateVertices();
			
			metal=new idx3d_Material();
			metal.setEnvmap(new idx3d_Texture(getDocumentBase(),"chrome.jpg"));
			metal.setReflectivity(255);
			metal.setColor(0x223344);
			scene.addMaterial("Metal",metal);
			
			wireframe=new idx3d_Material();
			wireframe.setWireframe(true);
			wireframe.setColor(0x66FF00);
			wireframe.setFlat(true);
			scene.addMaterial("Wireframe",wireframe);
			
			idx3d_TextureProjector.projectFrontal(obj);
			
			obj.setMaterial(metal);
			scene.addObject("Torus Knot",obj);
			
			scene.setAmbient(0x111111);
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.5f,0.5f,1f),0xFFFFFF,200,80));			
			scene.normalize();
			scene.scale(0.82f);
			
			scene.setBackground(new idx3d_Texture(getDocumentBase(),"back.jpg"));
	}

	public synchronized void paint(Graphics g)
	{
	}

	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}
	
	public void stop()
	{
		if (thread != null)
		{
			thread.stop();
			thread = null;
		}
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				thread.sleep(40);
			}
			catch (InterruptedException e){}
		}
	}

	public synchronized void update(Graphics g)
	{		
		if (autorotation)
		{
			float speed=1f;
			
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/1000)/20;
			scene.rotate(-speed*dx,speed*dy,speed*0.04f);
			
		}
				
		scene.render();
		g.drawImage(scene.getImage(),0,0,null);
	}		

	/*public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}*/

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
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='m') {for (int i=0;i<scene.objects;i++) idx3d_Toolkit.meshSmooth(scene.object[i]); return true; }
		
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }
		
		if ((char)key=='1') {scene.object("Torus Knot").setMaterial(metal); return true; }
		if ((char)key=='2') {scene.object("Torus Knot").setMaterial(wireframe); return true; }
		
		
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
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
