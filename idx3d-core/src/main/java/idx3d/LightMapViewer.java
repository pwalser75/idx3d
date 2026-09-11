package idx3d;

import java.awt.*;

public class LightMapViewer extends Frame
{
	Image diffuse, specular;
	
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	
	public LightMapViewer(idx3d_Scene scene)
	{
		super("idx3d_LightmapViewer");
		idx3d_Texture diff=new idx3d_Texture(256,256,scene.renderPipeline.lightmap.diffuse);
		idx3d_Texture spec=new idx3d_Texture(256,256,scene.renderPipeline.lightmap.specular);
		diff.flipHorizontal();
		spec.flipHorizontal();
		diffuse=diff.asImage();
		specular=spec.asImage();	
		resize(insets().left+insets().right+512,insets().top+insets().bottom+256);
		setResizable(false);
		show();
	}
	
	public void paint(Graphics g)
	{
		repaint();
	}
	
	public void update(Graphics g)
	{
		g.drawImage(diffuse,insets().left,insets().top,null);
		g.drawImage(specular,insets().left+256,insets().top,null);
		
	}
	
	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY) dispose(); 		
		return super.handleEvent(evt);
	}
}
	