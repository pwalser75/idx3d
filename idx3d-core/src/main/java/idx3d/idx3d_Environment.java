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

public class idx3d_Environment
{
	// F I E L D S

		public int ambient=0;

		public int fogcolor=0;
		public int fogfact=0;
		public int bgcolor=0xFF000000;
		public idx3d_Texture background=null;

	// P U B L I C   M E T H O D S


		public void setBackground(idx3d_Texture t)
		{
			background=t;
		}
	
}