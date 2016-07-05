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
	private Vertex[] vertices;
	private TextureCoordinate[] textureCoordinate;
	
	//True if the x coords are the ones that change along with y, z if not.
	private boolean xDominant;
	
	public UVMap(Vertex[] vertices, TextureCoordinate[] textureCoordinate)
	{
		this.vertices = vertices;
		this.textureCoordinate = textureCoordinate;
		
		BendHelper.outputVertexArray(vertices, "Test");
		
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
		
		System.out.println("dX: " + deltaX);
		System.out.println("dZ: " + deltaZ);
		System.out.println(xDominant);
	}
	
	private void getTextureCoordinateFromVertex(Vertex v)
	{
		
	}

}
