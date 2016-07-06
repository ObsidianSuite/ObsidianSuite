package MCEntityAnimator.render.objRendering.bend.UVMap;

import MCEntityAnimator.render.objRendering.bend.BendHelper;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

/**
 * Maps a vertices to texturecoordinates.
 */
public class UVMap 
{
	
	//Vertices[0] corresponds to textureCoordinate[0].
	//Order topLeft, topRight, bottomRight, bottomLeft.
	private Point[] points;
	private TextureCoordinate[] textureCoordinate;
	
	//True if the x coords are the ones that change along with y, z if not.
	private boolean xDominant;
	
	public UVMap(Vertex[] vertices, TextureCoordinate[] textureCoordinate)
	{
		this.textureCoordinate = textureCoordinate;
				
		//Work out if x or z dominant.
		//DeltaX and deltaZ represent the total difference in the 
		//respective dimensions in comparison to one of the points (which point is arbitrary).
		//The greater delta is the dominant dimension.
		//One of these values should be zero, but due to minor differences in values it 
		//may be slightly above zero, hence using the total difference as opposed to looking
		//for one that equals zero.
		float deltaX = 0.0F;
		float deltaZ = 0.0F;
		Vertex comparisonVertex = vertices[0];
		for(Vertex v : vertices)
		{
			deltaX += Math.abs(comparisonVertex.x - v.x);
			deltaZ += Math.abs(comparisonVertex.z - v.z);
		}
		xDominant = deltaX > deltaZ;
		
		//Setup points - abstraction of vertices, where point.x = vertex.x or vertex.z, depending
		//on which of x and z is dominant. point.y = vertex.y
		//Since order of vertices is undisturbed, order of points is the same as vertices.
		points = new Point[vertices.length];
		for(int i = 0; i < vertices.length; i++)
		{
			Vertex v = vertices[i];
			float pX = xDominant ? v.x : v.z;
			float pY = v.y;
			Point p = new Point(pX, pY);
			points[i] = p;
		}
	}
	
	/**
	 * Get the texture coordinate for this vertex.
	 * Calculates the proportional difference between this vertex and
	 * the top left vertex, then uses that proportion to get the
	 * texture u and v relative to the top left texture coordinate.
	 */
	public TextureCoordinate getTextureCoordinateFromVertex(Vertex v)
	{
		//Top left.
		Point controlPoint = points[0];
		
		//Take xDominant into account.
		float x = xDominant ? v.x : v.z;
		float y = v.y;
		
		//Calculate difference between v and control point.
		float dX = x - controlPoint.x;
		float dY = y - controlPoint.x;
		
		//Convert differences to proportion. Should be between 0 and 1. 
		float pX = dX/(points[1].x - controlPoint.x);
		float pY = dY/(points[3].y - controlPoint.y);
		
		//Calculate texture coords. Top left coord + proportion*total texture difference.
		float texU = textureCoordinate[0].u + pX*(textureCoordinate[1].u - textureCoordinate[0].u);
		float texV = textureCoordinate[0].v + pY*(textureCoordinate[3].v - textureCoordinate[0].v);
		
		return new TextureCoordinate(texU, texV);
	}
	
	private class Point
	{
		
		private float x,y;
		
		private Point(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
		
	}

}
