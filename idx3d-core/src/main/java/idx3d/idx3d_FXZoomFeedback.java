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
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------

package idx3d;


public class idx3d_FXZoomFeedback extends idx3d_FXPlugin
// Simple zoom/blur feedback
{
	
	// C O N S T R U C T O R S
	
		public idx3d_FXZoomFeedback(idx3d_Scene scene)
		{
			super(scene);
		}
		
		public void apply()
		// Applys the effect on the scene / screen
		{
			// ZOOM
			
				int[] source=screen.pixel;
				int width=screen.width;
				int height=screen.height;
				int[] pixel=new int[width*height];
				int border=5;
				int x1=border;
				int x2=width-border;
				int y1=border;
				int y2=height-border;
				
				int xStep=((x2-x1)<<16)/width;
				int yStep=((y2-y1)<<16)/height;
	
				int tx=x1<<16;
				int ty=y1<<16;
				int yoffset=0;
				int tyoffset;
	
				for(int y=0;y<height;y++)
				{
					tyoffset=(ty>>16)*width;
					for(int x=0;x<width;x++)
					{
						pixel[yoffset+x]=source[tyoffset+(tx>>16)];
						tx+=xStep;
					}
					tx=x1<<16;
					ty+=yStep;
					yoffset+=width;
				}		
				
				idx3d_Math.copyBuffer(pixel,screen.pixel);
				source=screen.pixel;
				
			// BLUR
			
				yoffset=width;
				for (int y=1;y<height-1;y++)
				{
					for (int x=1;x<width-1;x++) pixel[yoffset+x]=0xFF000000|(
					((source[yoffset+x-1]&0xFCFCFC)>>2)
					+((source[yoffset+x+1]&0xFCFCFC)>>2)
					+((source[yoffset+x-width]&0xFCFCFC)>>2)
					+((source[yoffset+x+width]&0xFCFCFC)>>2));
					yoffset+=width;
				}			
			
			// SET AS BACKGROUND
			
				scene.setBackground(new idx3d_Texture(width,height,pixel));
		}

}