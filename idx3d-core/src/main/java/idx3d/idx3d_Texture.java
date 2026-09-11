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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

public class idx3d_Texture
// defines a texture
{
	// F I E L D S

		public int width;
		public int height;
		public int bitWidth;
		public int bitHeight;
		public int pixel[];
		
		public String path=null;

	// C O N S T R U C T O R S

		public idx3d_Texture(int w, int h)
		{
			height=h;
			width=w;
			pixel=new int[w*h];
			cls();
		}

		public idx3d_Texture(int w, int h, int data[])
		{
			height=h;
			width=w;
			pixel=data;
		}

		public idx3d_Texture(Image img)
		{
			loadTexture(img);
		}

		public idx3d_Texture(URL docURL, String filename)
		// Call from legacy Applet code: assets are now resolved from the classpath
		{
			this(filename);
		}

		public idx3d_Texture(String filename)
		{
			path=new java.io.File(filename).getName();
			loadTexture(idx3d_Resources.image(filename));
		}
		

	// P U B L I C   M E T H O D S

		public void resize()
		{
			double log2inv=1/Math.log(2);
			int w=(int)Math.pow(2,bitWidth=(int)(Math.log(width)*log2inv));
			int h=(int)Math.pow(2,bitHeight=(int)(Math.log(height)*log2inv));
			bilinearResize(w,h);
		}
		
		public idx3d_Texture put(idx3d_Texture newData)
		// assigns new data for the texture
		{
			System.arraycopy(newData.pixel,0,pixel,0,width*height);
			return this;
		}

		public idx3d_Texture mix(idx3d_Texture newData)
		// mixes the texture with another one
		{
			for (int i=width*height-1;i>=0;i--)
				pixel[i]=idx3d_Color.mix(pixel[i],newData.pixel[i]);
			return this;
		}

		public idx3d_Texture add(idx3d_Texture additive)
		// additive blends another texture with this
		{
			for (int i=width*height-1;i>=0;i--)
				pixel[i]=idx3d_Color.add(pixel[i],additive.pixel[i]);
			return this;
		}

		public idx3d_Texture sub(idx3d_Texture subtractive)
		// subtractive blends another texture with this
		{
			for (int i=width*height-1;i>=0;i--)
				pixel[i]=idx3d_Color.sub(pixel[i],subtractive.pixel[i]);
			return this;
		}

		public idx3d_Texture inv()
		// inverts the texture
		{
			for (int i=width*height-1;i>=0;i--)
				pixel[i]=idx3d_Color.inv(pixel[i]);
			return this;
		}
		
		public idx3d_Texture multiply(idx3d_Texture multiplicative)
		// inverts the texture
		{
			for (int i=width*height-1;i>=0;i--)
				pixel[i]=idx3d_Color.multiply(pixel[i],multiplicative.pixel[i]);
			return this;
		}


		public void cls()
		// clears the texture
		{
			idx3d_Math.clearBuffer(pixel,0);
		}

		public idx3d_Texture toAverage()
		// builds the averidge of the channels
		{
			for (int i=width*height-1;i>=0;i--) 
				pixel[i]=idx3d_Color.getAverage(pixel[i]);
			return this;
		}

		public idx3d_Texture toGray()
		// converts this texture to gray
		{
			for (int i=width*height-1;i>=0;i--) 
				pixel[i]=idx3d_Color.getGray(pixel[i]);
			return this;
		}
		
		public idx3d_Texture valToGray()
		{
			int intensity;
			for (int i=width*height-1;i>=0;i--)
			{
				intensity=idx3d_Math.crop(pixel[i],0,255);
				pixel[i]=idx3d_Color.getColor(intensity,intensity,intensity);
			}
			
			return this;
		}
			

	// P R I V A T E   M E T H O D S


		private void loadTexture(Image img)
		// grabs the pixels out of an image (modernized: BufferedImage/ImageIO)
		{
			if (img==null)
			{
				width=1; height=1; pixel=new int[]{0};
				return;
			}

			BufferedImage bi;
			if (img instanceof BufferedImage)
				bi=(BufferedImage)img;
			else
			{
				int w=img.getWidth(null);
				int h=img.getHeight(null);
				if (w<1||h<1) { width=1; height=1; pixel=new int[]{0}; return; }
				bi=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g=bi.createGraphics();
				g.drawImage(img,0,0,null);
				g.dispose();
			}

			width=bi.getWidth();
			height=bi.getHeight();
			if (width<1||height<1) { width=1; height=1; pixel=new int[]{0}; return; }
			pixel=new int[width*height];
			bi.getRGB(0,0,width,height,pixel,0,width);
		}

		public void resize(int w, int h)
		// resizes the texture
		{
			int offset=w*h;
			int offset2;
			if (w*h!=0)
			{
				int newpixels[]=new int[w*h];
				for(int j=h-1;j>=0;j--)
				{
					offset-=w;
					offset2=(j*height/h)*width;
					for (int i=w-1;i>=0;i--)
						newpixels[i+offset]=pixel[(i*width/w)+offset2];
				}
				width=w; height=h; pixel=newpixels;
			}
		}
		
		private void bilinearResize(int w, int h)
		// resizes the texture bilinear
		{
			idx3d_Texture t=idx3d_TextureFactory.bilinearResample(this,w,h);
			this.pixel=t.pixel;
			this.width=w;
			this.height=h;
		}

		private boolean inrange(int a, int b, int c)
		{
			return (a>=b)&(a<c);
		}
		
		public idx3d_Texture getClone()
		{
			idx3d_Texture t=new idx3d_Texture(width,height);
			idx3d_Math.copyBuffer(pixel,t.pixel);
			return t;
		}
		
		public void setCircleAlpha()
		{
			float w2=(float)width/2;
			float h2=(float)height/2;
			int pos=0;
			for (int y=0;y<height;y++)
			{
				for (int x=0;x<width;x++)
				{
					if (idx3d_Math.pythagoras(((float)x-w2)/w2,((float)y-h2)/h2)<1)
						pixel[pos]=pixel[pos]|0xFF000000;
					else
						pixel[pos]=pixel[pos]&0x00FFFFFF;
					pos++;
				}
			}
		}
		
		public Image asImage()
		{
			return new idx3d_Screen(this).getImage();
		}
		
		public void flipVertical()
		{
			int[] temp=new int[width*height];
			for(int y=0;y<height;y++)
				for(int x=0;x<width;x++)
					temp[x+(height-1-y)*width]=pixel[x+y*width];
			pixel=temp;
		}
		
		public void flipHorizontal()
		{
			int[] temp=new int[width*height];
			for(int y=0;y<height;y++)
				for(int x=0;x<width;x++)
					temp[(width-1-x)+y*width]=pixel[x+y*width];
			pixel=temp;
		}
		
		public int getBilinearPixel(float x, float y)
		{
			int xpos=(int)x;
			int ypos=(int)y;
			float xrel=x-xpos;
			float yrel=y-ypos;
			int colorA=pixel[xpos+ypos*width];
			int colorB=pixel[xpos+1+ypos*width];
			int colorC=pixel[xpos+(ypos+1)*width];
			int colorD=pixel[xpos+1+(ypos+1)*width];
			int weightA=(int)(255*(1-xrel)*(1-yrel));
			int weightB=(int)(255*xrel*(1-yrel));
			int weightC=(int)(255*(1-xrel)*yrel);
			int weightD=(int)(255*xrel*yrel);
			int r=((((colorA>>16)&255)*weightA)>>8)
				+((((colorB>>16)&255)*weightB)>>8)
				+((((colorC>>16)&255)*weightC)>>8)
				+((((colorD>>16)&255)*weightD)>>8);
			int g=((((colorA>>8)&255)*weightA)>>8)
				+((((colorB>>8)&255)*weightB)>>8)
				+((((colorC>>8)&255)*weightC)>>8)
				+((((colorD>>8)&255)*weightD)>>8);
			int b=(((colorA&255)*weightA)>>8)
				+(((colorB&255)*weightB)>>8)
				+(((colorC&255)*weightC)>>8)
				+(((colorD&255)*weightD)>>8);
			return 0xFF000000|(r<<16)|(g<<8)|b;
		}
			
		
}