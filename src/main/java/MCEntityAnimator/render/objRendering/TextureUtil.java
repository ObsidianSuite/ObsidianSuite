package MCEntityAnimator.render.objRendering;

import java.util.ArrayList;

import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

public class TextureUtil 
{

	/**
	 * Orders the vertices according to this order - horizontal, corner, vertical.
	 */
	public static Vertex[] orderVertices(Vertex[] vertices)
	{
		//Vertices
		//1-Create an array list of all the combinations of vertices - (size 3).
		//2-Work out with axis the vertices are orientated in (x or z) - this will be called w.
		//3-Calculate dW and dY for all combinations.
		//4-Work out the two vertical vertices (least dW) - the other vertex is then the 'side' one.
		//5-Work out which of the two vertical vertices has the minimum dY compared the 'side' vertex, this is the 'corner' vertex.
		//6-The remaining vertex is then the 'top' vertex.

		Vertex topVertex = null, cornerVertex = null, sideVertex = null;
		char wAxis;
		ArrayList<VertexCombination> combinations = new ArrayList<VertexCombination>();

		//1-Combinations of vertices
		for(Vertex v1 : vertices)
		{
			for(Vertex v2 : vertices)
			{
				if(!v1.equals(v2))
				{
					combinations.add(new VertexCombination(v1, v2));
				}
			}	
		}


		//2-Work out w axis
		float totalDX = 0.0F, totalDZ = 0.0F;

		for(VertexCombination vc : combinations)
		{
			totalDX += getDX(vc.v1, vc.v2);
			totalDZ += getDZ(vc.v1, vc.v2);
		}

		if(totalDX > totalDZ)
		{
			wAxis = 'x';
		}
		else
		{
			wAxis = 'z';
		}

		//3 - dW and dY for all combinations
		for(VertexCombination vc : combinations)
		{
			switch(wAxis)
			{
			case 'x':
				vc.dW = getDX(vc.v1, vc.v2);
				break;
			case 'z':
				vc.dW = getDZ(vc.v1, vc.v2);
				break;
			}
			vc.dY = getDY(vc.v1, vc.v2);
		}

		//4 - Side vertex
		float minDW = -10.0F;
		VertexCombination verticalVertices = null;
		for(VertexCombination vc : combinations)
		{
			if(vc.dW < minDW || minDW == -10.0F)
			{
				minDW = vc.dW;
				verticalVertices = vc;
			}
		}
		try
		{
			sideVertex = getOtherVertex(vertices, verticalVertices);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//5 - Corner vertex
		float minDY = -10.0F;
		for(Vertex v : verticalVertices.getVertices())
		{
			float dY = getDY(sideVertex, v);
			if(dY < minDY || minDY == -10.0F)
			{
				cornerVertex = v;
				minDY = dY;
			}
		}

		//6 - Top Vertex
		for(Vertex v : vertices)
		{
			if(!v.equals(sideVertex) && !v.equals(cornerVertex))
			{
				topVertex = v;
				break;
			}
		}

		//topVertex cornerVertex sideVertex
		return new Vertex[]{sideVertex, cornerVertex, topVertex};
	}
	
	/**
	 * Orders the texture coordinates according to this order - horizontal, corner, vertical.
	 */
	public static TextureCoordinate[] orderCoords(TextureCoordinate[] textureCoordinates)
	{
		//Texture Coordinates
		//1-Create an array list of all the combinations of texture coordinates - (size 3).
		//2-Calculate dU and dV for all combinations.
		//3-Work out the two vertical texture coordinates (least dX) - the other coordinate is then the 'side' one.
		//4-Work out which of the two vertical coordinates has the minimum dY compared the 'side' vertex, this is the 'corner' coordinate.
		//5-The remaining coordinate is then the 'top'.

		TextureCoordinate topTC = null, cornerTC = null, sideTC = null;
		ArrayList<TextureCombination> textureCombinations = new ArrayList<TextureCombination>();

		//1-Combinations of coordinates
		for(TextureCoordinate tc1 : textureCoordinates)
		{
			for(TextureCoordinate tc2 : textureCoordinates)
			{
				if(!tc1.equals(tc2))
				{
					textureCombinations.add(new TextureCombination(tc1, tc2));
				}
			}	
		}

		//2 - dU and dV for all combinations
		for(TextureCombination tc : textureCombinations)
		{
			tc.dU = getDU(tc.tc1, tc.tc2);
			tc.dV = getDV(tc.tc1, tc.tc2);
		}

		//3- Side coord
		float minDU = -10.0F;
		TextureCombination verticalCoords = null;
		for(TextureCombination tc : textureCombinations)
		{
			if(tc.dU < minDU || minDU == -10.0F)
			{
				minDU = tc.dU;
				verticalCoords = tc;
			}
		}
		try
		{
			sideTC = getOtherCoordinate(textureCoordinates, verticalCoords);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//4 - Corner coord
		float minDV = -10.0F;
		for(TextureCoordinate tc : verticalCoords.getCoords())
		{
			float dV = getDV(sideTC, tc);
			if(dV < minDV || minDV == -10.0F)
			{
				cornerTC = tc;
				minDV = dV;
			}
		}

		//5 - Top coord
		for(TextureCoordinate tc : textureCoordinates)
		{
			if(!tc.equals(sideTC) && !tc.equals(cornerTC))
			{
				topTC = tc;
				break;
			}
		}
		
		//topTC cornerTC sideTC
		return new TextureCoordinate[]{sideTC, cornerTC, topTC};
	}
	
	/**
	 * Returns the absolute distance between the two vertices.
	 */
	public static float getDistanceBetween3DPoints(Vertex v1, Vertex v2)
	{
		float x = v1.x - v2.x;
		float y = v1.y - v2.y;
		float z = v1.z - v2.z;

		return (float) Math.sqrt(x*x + y*y + z*z);
	}

	/**
	 * Returns the vertex from the array vertices that is not part of the vertex combination.
	 */
	private static Vertex getOtherVertex(Vertex[] vertices, VertexCombination vc) throws Exception 
	{
		if(vc == null)
		{
			throw new Exception("No vertex combination to compare to.");	
		}
		for(Vertex v : vertices)
		{
			if(!v.equals(vc.v1) && !v.equals(vc.v2))
			{
				return v;
			}
		}
		throw new Exception("Can't find other vertex.");
	}

	/**
	 * Returns the vertex from the array vertices that is not part of the vertex combination.
	 */
	private static TextureCoordinate getOtherCoordinate(TextureCoordinate[] coords, TextureCombination tc) throws Exception 
	{
		if(tc == null)
		{
			throw new Exception("No texture coordinate combination to compare to.");	
		}
		for(TextureCoordinate c : coords)
		{
			if(!c.equals(tc.tc1) && !c.equals(tc.tc2))
			{
				return c;
			}
		}
		throw new Exception("Can't find other coordinate.");
	}

	private static float getDX(Vertex v1, Vertex v2)
	{
		return Math.abs(v1.x - v2.x);
	}

	private static float getDY(Vertex v1, Vertex v2)
	{
		return Math.abs(v1.y - v2.y);
	}

	private static float getDZ(Vertex v1, Vertex v2)
	{
		return Math.abs(v1.z - v2.z);
	}

	private static float getDU(TextureCoordinate tc1, TextureCoordinate tc2)
	{
		return Math.abs(tc1.u - tc2.u);
	}

	private static float getDV(TextureCoordinate tc1, TextureCoordinate tc2)
	{
		return Math.abs(tc1.v - tc2.v);
	}
	
	public double getDistanceBetween3DPoints(float[] point1, float[] point2)
	{
		float x = point1[0] - point2[0];
		float y = point1[1] - point2[1];
		float z = point1[2] - point2[2];

		return (float) Math.sqrt(x*x + y*y + z*z);
	}
}
