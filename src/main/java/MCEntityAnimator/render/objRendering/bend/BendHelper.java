package MCEntityAnimator.render.objRendering.bend;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.Vertex;

public class BendHelper 
{	
	
	/**
	 * Returns the set of eight vertices for this part. 
	 * Removes duplicate vertices.
	 */
	public static Vertex[] getPartVertices(PartObj part)
	{
		List<Vertex> partVertices = new ArrayList<Vertex>();
		for(Face f : part.groupObj.faces)
		{
			for(Vertex v : f.vertices)
			{
				if(partVertices.size() > 0)
				{
					//Only add if a vertex doesn't exist with the same value.
					boolean add = true;
					for(Vertex w : partVertices)
					{
						if(areVerticesEqual(v, w))
						{
							add = false;
							break;
						}
					}
					if(add)
						partVertices.add(v);
				}
				else
					partVertices.add(v);
			}
		}
		return partVertices.toArray(new Vertex[8]);
	}

	/**
	 * Aligns two sets of vertices so that vertexA[0] is closest to vertexB[0].
	 * Explanation: http://imgur.com/Y7LbjQP
	 * @param fixed - This set of vertices will remain as they are.
	 * @param vertices - This set of vertices is the set to be aligned.
	 * @return - The vertices set, but aligned to the fixed set. 
	 */
	public static Vertex[] alignVertices(Vertex[] fixed, Vertex[] vertices)
	{
		Vertex[] alignedVertices = new Vertex[fixed.length];
		for(int i = 0; i < fixed.length; i++)
		{
			Vertex v = fixed[i];
			//Vertex that corresponds to v is the closest one. 
			alignedVertices[i] = orderVerticesOnDistance(vertices, v)[0];
		}
		return alignedVertices;
	}

	/**
	 * Orders the vertices in a relative fashion. Each consecutive vertex should
	 * only change in one dimension from the previous vertex.
	 * FIXME This assumes all vertices have the same y value, so we are only comparing x and z values. 
	 * Example: http://imgur.com/awsGX4f
	 * Start from the vertex with the greatest x and z values.  
	 */
	public static Vertex[] orderVerticesRelative(Vertex[] vertices)
	{
		Vertex[] orderedVertices = new Vertex[vertices.length];
		
		//Get starting vertex  - greatest x and z.
		Vertex startingVertex = vertices[0];
		for(int a = 1; a < 4; a++)
		{
			if(vertices[a].x >= startingVertex.x && vertices[a].z >= startingVertex.z)
				startingVertex = vertices[a];
		}
		orderedVertices[0] = startingVertex;
		
		
		for(int i = 0; i < 4; i++)
		{
			Vertex v = vertices[i];
			if(v.x != startingVertex.x && v.z == startingVertex.z)
				orderedVertices[1] = v;
			else if(v.x != startingVertex.x && v.z != startingVertex.z)
				orderedVertices[2] = v;
			else if(v.x == startingVertex.x && v.z != startingVertex.z)
				orderedVertices[3] = v;
		}
		return orderedVertices;
	}
	
	/**
	 * Orders the vertices with the ones closest to the target coming first.
	 */
	public static Vertex[] orderVerticesOnDistance(Vertex[] vertices, Vertex target)
	{
		List<VertexWithDistance> orderedVertices = new ArrayList<VertexWithDistance>();
		for(Vertex v : vertices)
			orderedVertices.add(new VertexWithDistance(v, getDistanceBetweenVertices(v, target)));
		Collections.sort(orderedVertices);
		Vertex[] orderedVerticesArray = new Vertex[orderedVertices.size()];
		for(int i = 0; i < orderedVertices.size(); i++)
			orderedVerticesArray[i] = orderedVertices.get(i).getVertex();
		return orderedVerticesArray;
	}

	/**
	 * Returns the distance between v and w. Order doesn't matter.
	 */
	public static float getDistanceBetweenVertices(Vertex v, Vertex w)
	{
		float dx = v.x - w.x;
		float dy = v.y - w.y;
		float dz = v.z - w.z;
		return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	/**
	 * Rotate a vertex by a given rotation around a given rotation point.
	 */
	public static void rotateVertex(Vertex v, float[] rotation, Vertex rotationPoint)
	{
		float[] vector = new float[]{v.x - rotationPoint.x, v.y - rotationPoint.y, v.z - rotationPoint.z};

		vector = zMatrix(vector, rotation[2]);
		vector = yMatrix(vector, rotation[1]);
		vector = xMatrix(vector, rotation[0]);

		v.x = vector[0] + rotationPoint.x;
		v.y = vector[1] + rotationPoint.y;
		v.z = vector[2] + rotationPoint.z;
	}

	/**
	 * Apply x rotation to a vector.
	 */
	private static float[] xMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = x;
		float ry = (float) (y*Math.cos(angle) - z*Math.sin(angle));
		float rz = (float) (y*Math.sin(angle) + z*Math.cos(angle));
		return new float[]{rx, ry, rz};
	}

	/**
	 * Apply y rotation to a vector.
	 */
	private static float[] yMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = (float) (x*Math.cos(angle) + z*Math.sin(angle));
		float ry = y;
		float rz = (float) (z*Math.cos(angle) - x*Math.sin(angle));
		return new float[]{rx, ry, rz};
	}

	/**
	 * Apply z rotation to a vector.
	 */
	private static float[] zMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = (float) (x*Math.cos(angle) - y*Math.sin(angle));
		float ry = (float) (x*Math.sin(angle) + y*Math.cos(angle));
		float rz = z;
		return new float[]{rx, ry, rz};
	}	
	
	public static boolean areVerticesEqual(Vertex v, Vertex w)
	{
		float dX = Math.abs(v.x - w.x);
		float dY = Math.abs(v.y - w.y);
		float dZ = Math.abs(v.z - w.z);
		return dX < 0.01 && dY < 0.01 && dZ < 0.01;
	}
		
	public static void outputVertexArray(Vertex[] ver, String vertexName)
	{
		System.out.println("--" + vertexName + "--");
		for(Vertex v : ver)
			System.out.println(getVertexAsString(v));
	}
	
	public static String getVertexAsString(Vertex v)
	{
		return v.x + ", " + v.y + ", " + v.z;
	}

}
