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

public abstract class idx3d_FXPlugin
// fx plugin superclass
{
	// F I E L D S

		public  idx3d_Scene scene=null;
		public idx3d_Screen screen=null;


	// C O N S T R U C T O R    M E T H O D S
	
		public idx3d_FXPlugin(idx3d_Scene scene)
		{
			this.scene=scene;
			screen=scene.renderPipeline.screen;
		}
		
	// A B S T R A C T   M E T H O D S
	
		public abstract void apply();
		// Applys the effect on the scene / screen

}