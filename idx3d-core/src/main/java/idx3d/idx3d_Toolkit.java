package idx3d;

public class idx3d_Toolkit
{
	
	// C O N S T R U C T O R S

		private idx3d_Toolkit()
		{
		}

	// P U B L I C   M E T H O D S

		public static idx3d_Vertex interpolate(idx3d_Vertex a, idx3d_Vertex b)
		{
			idx3d_Vector p1,p2,n1,n2;
			p1=a.pos;
			p2=b.pos;
			n1=a.n;
			n2=b.n;
			
			idx3d_Vector v=new idx3d_Vector();
			
			float q1 = p2.x-p1.x;
			float q2 = p2.y-p1.y;
			float q3 = q1*n1.y-q2*n1.x;
			float q4 = p2.z-p1.z;
			float q5 = q4*n1.x-q1*n1.z;
			float q6 = q1*n2.y-q2*n2.x;
			float q7 = q4*n2.x-q1*n2.z;
			float q8 = q2*n1.z-q4*n1.y;
			float q9 = q2*n2.z-q4*n2.y;
			v.x = 0.5f*p1.x+0.125f*n1.y*q3-0.12f*n1.z*q5+0.5f*p2.x-0.125f*n2.y*q6+0.125f*n2.z*q7;
			v.y = 0.5f*p1.y+0.125f*n1.z*q8-0.125f*n1.x*q3+0.5f*p2.y-0.125f*n2.z*q9+0.125f*n2.x*q6;
			v.z = 0.5f*p1.z+0.125f*n1.x*q5-0.125f*n1.y*q8+0.5f*p2.z-0.125f*n2.x*q7+0.125f*n2.y*q9;
			
			return new idx3d_Vertex(v);
		}
		
		public static void meshSmooth(idx3d_Object obj)
		{
			obj.rebuild();
			int triangles=obj.triangles();
			int vertices=obj.vertices();
			
			idx3d_Vertex[][] newVertices=new idx3d_Vertex[vertices][vertices];
			
			// Create intermediate vertices

				for (int i=0; i<triangles; i++)
				{
					idx3d_Triangle tri=obj.triangle(i);
					if(newVertices[tri.p1.id][tri.p2.id]==null)
					{
						idx3d_Vertex v12=interpolate(tri.p1,tri.p2);
						newVertices[tri.p1.id][tri.p2.id]=v12;
						newVertices[tri.p2.id][tri.p1.id]=v12;
						v12.u=(tri.p1.u+tri.p2.u)/2;
						v12.v=(tri.p1.v+tri.p2.v)/2;
						obj.addVertex(v12);
					}
					if(newVertices[tri.p2.id][tri.p3.id]==null)
					{
						idx3d_Vertex v23=interpolate(tri.p2,tri.p3);
						newVertices[tri.p2.id][tri.p3.id]=v23;
						newVertices[tri.p3.id][tri.p2.id]=v23;
						v23.u=(tri.p2.u+tri.p3.u)/2;
						v23.v=(tri.p2.v+tri.p3.v)/2;
						obj.addVertex(v23);
					}
					if(newVertices[tri.p1.id][tri.p3.id]==null)
					{
						idx3d_Vertex v13=interpolate(tri.p1,tri.p3);
						newVertices[tri.p1.id][tri.p3.id]=v13;
						newVertices[tri.p3.id][tri.p1.id]=v13;
						v13.u=(tri.p1.u+tri.p3.u)/2;
						v13.v=(tri.p1.v+tri.p3.v)/2;
						obj.addVertex(v13);
					}
				}

			// Create new triangles
			
				for(int i=0; i<triangles; i++)
				{
					idx3d_Triangle tri=obj.triangle(i);
					
					obj.addTriangle(newVertices[tri.p1.id][tri.p2.id],tri.p2,newVertices[tri.p2.id][tri.p3.id]);
					obj.addTriangle(newVertices[tri.p2.id][tri.p3.id],tri.p3,newVertices[tri.p1.id][tri.p3.id]);
					obj.addTriangle(newVertices[tri.p1.id][tri.p2.id],newVertices[tri.p2.id][tri.p3.id],newVertices[tri.p1.id][tri.p3.id]);
					
					idx3d_Vertex p2=newVertices[tri.p1.id][tri.p2.id];
					idx3d_Vertex p3=newVertices[tri.p1.id][tri.p3.id];
					
					tri.p2=p2;
					tri.p3=p3;
				}
				obj.rebuild();
		}
}