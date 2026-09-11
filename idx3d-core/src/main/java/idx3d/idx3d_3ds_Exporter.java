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

import java.io.*;
import java.net.*;

public class idx3d_3ds_Exporter
// Exports a scene to a 3ds (3d Studio Max) Ressource
{
	// F I E L D S

	// C O N S T R U C T O R S

		private idx3d_3ds_Exporter()
		{
		}


	// P U B L I C   M E T H O D S

		public static void exportToStream(OutputStream outStream, idx3d_Scene source)
		{
			System.out.println(">> Exporting scene to 3ds stream ...");
			BufferedOutputStream out=new BufferedOutputStream(outStream);
			try{ 
				exportScene(source,out);
				outStream.close();
			}
			catch (Throwable ignored){System.out.println(ignored+"");}
		}



	// P R I V A T E   M E T H O D S

		private static String getString(String outString)
		{
			return outString+(char)0;
		}

		private static String getInt(int outInt)
		{
			return (char)(outInt&255)+""+(char)((outInt>>8)&255)+""+(char)((outInt>>16)&255)+""+(char)((outInt>>24)&255);
		}

		private static String getShort(int outShort)
		{
			return (char)(outShort&255)+""+(char)((outShort>>8)&255);
		}

		private static String getFloat(float outFloat)
		{
			return getInt(Float.floatToIntBits(outFloat));
		}


	// J U N K   E X P O R T
	
		private static void writeToStream(String outString, OutputStream out) throws IOException
		{
			byte[] data=new byte[(int)(outString.length())];
			outString.getBytes(0,(int)outString.length(),data,0);
			out.write(data);
			out.flush();
		}
		

		private static void exportScene(idx3d_Scene scene, OutputStream out) throws IOException
		{
			StringBuffer buffer=new StringBuffer();
			scene.rebuild();
			
			String meshData=meshBlock(scene);
			buffer.append(getShort(0x4D4D));
			buffer.append(getInt(12+meshData.length()));
			buffer.append(getShort(0x3D3D));
			buffer.append(getInt(6+meshData.length()));
			buffer.append(meshData);
			writeToStream(buffer.toString(),out);
		}
		
		private static String meshBlock(idx3d_Scene scene)
		{
			StringBuffer buffer=new StringBuffer();
			String objectBlock;
			for (int i=0;i<scene.objects;i++)
			{
				objectBlock=objectBlock(scene.object[i]);
				buffer.append(getShort(0x4000));
				buffer.append(getInt(6+objectBlock.length()));
				buffer.append(objectBlock);
			}
			
			return buffer.toString();
		}
		
		private static String objectBlock(idx3d_Object obj)
		{
			StringBuffer buffer=new StringBuffer();
			String triangleMeshBlock=triangleMeshBlock(obj);
			
			buffer.append(getString(obj.name));
			buffer.append(getShort(0x4100));
			buffer.append(getInt(6+triangleMeshBlock.length()));
			buffer.append(triangleMeshBlock);
			
			return buffer.toString();
		}
		
		private static String triangleMeshBlock(idx3d_Object obj)
		{
			StringBuffer buffer=new StringBuffer();
			
			String vertices=vertexBlock(obj);
			buffer.append(getShort(0x4110));
			buffer.append(getInt(6+vertices.length()));
			buffer.append(vertices);
			
			String triangles=triangleBlock(obj);
			buffer.append(getShort(0x4120));
			buffer.append(getInt(6+triangles.length()));
			buffer.append(triangles);
			
			if (hasMappingCoordinates(obj))
			{
				String mappingcoords=uvBlock(obj);
				buffer.append(getShort(0x4140));
				buffer.append(getInt(6+mappingcoords.length()));
				buffer.append(mappingcoords);
			}

			return buffer.toString();
		}
		
		private static boolean hasMappingCoordinates(idx3d_Object obj)
		{
			for (int i=0;i<obj.vertices;i++)
			{
				if (obj.vertex[i].u !=0) return true;
				if (obj.vertex[i].v !=0) return true;
			}
			return false;
		}
				
		private static String vertexBlock(idx3d_Object obj)
		{
			StringBuffer buffer=new StringBuffer();
			buffer.append(getShort(obj.vertices));
			for (int i=0;i<obj.vertices;i++)
			{
				buffer.append(getFloat(obj.vertex[i].pos.x));
				buffer.append(getFloat(-obj.vertex[i].pos.y));
				buffer.append(getFloat(obj.vertex[i].pos.z));
			}
			return buffer.toString();
		}
		
		private static String triangleBlock(idx3d_Object obj)
		{
			StringBuffer buffer=new StringBuffer();
			buffer.append(getShort(obj.triangles));
			for (int i=0;i<obj.triangles;i++)
			{
				buffer.append(getShort(obj.triangle[i].p1.id));
				buffer.append(getShort(obj.triangle[i].p2.id));
				buffer.append(getShort(obj.triangle[i].p3.id));
				buffer.append(getShort(0));				
			}
			return buffer.toString();
		}
		
		private static String uvBlock(idx3d_Object obj)
		{
			StringBuffer buffer=new StringBuffer();
			buffer.append(getShort(obj.vertices));
			for (int i=0;i<obj.vertices;i++)
			{
				buffer.append(getFloat(obj.vertex[i].u));
				buffer.append(getFloat(1-obj.vertex[i].v));
			}
			return buffer.toString();
		}
}
