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
import idx3d.idx3d_Vector;

public interface ImplicitSurface
// Interface for implicit surfaces
{
	public boolean inside(idx3d_Vector v);
	// Decides whether a given point is inside the surface or not
	
	public float getLevel(idx3d_Vector v);
	// Returns the level for a given point
	
	public float getIsolevel();
	// Returns the isolevel of the surface
	
	public idx3d_Vector getCenter();
	// Returns the center of the work space
	
	public idx3d_Vector getDimension();
	// Returns the dimension of the work space
}