package MCEntityAnimator.render.objRendering;

import java.util.ArrayList;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

public class UVMap 
{

	//The top left vertex of the side. 
	private Vertex topLeftVertex;

	//The width and height of the side of the part.
	private float sideWidth;
	private float sideHeight;

	//True if x changes and not z.
	private boolean xDominant = true;

	//The top left uv coord. 
	private TextureCoordinate topLeftUVCoord;

	//The width and height of the uv map.
	private float uvwidth;
	private float uvheight;

	//Setup the UVMap from the top face of the side. 
	public UVMap(Face topFace)
	{
		Vertex[] orderedVertices = TextureUtil.orderVertices(topFace.vertices);
		topLeftVertex = new Vertex(orderedVertices[1].x, orderedVertices[1].y, orderedVertices[1].z);
		sideWidth = orderedVertices[0].x - orderedVertices[1].x;

		if(sideWidth == 0.0F)
		{
			sideWidth = orderedVertices[0].z - orderedVertices[1].z;
			xDominant = false;
		}

		sideHeight = orderedVertices[1].y - orderedVertices[2].y;

		TextureCoordinate[] orderedTCs = TextureUtil.orderCoords(topFace.textureCoordinates);
		topLeftUVCoord = new TextureCoordinate(orderedTCs[1].u, orderedTCs[1].v);
		uvwidth = orderedTCs[0].u - orderedTCs[1].u;
		uvheight = orderedTCs[1].v - orderedTCs[2].v;
		
	}

	public void setTextureCoordinatesForFace(Face f)
	{
		f.textureCoordinates = new TextureCoordinate[3];

		for(int i = 0; i < 3; i++)
		{
			f.textureCoordinates[i] = getUVCoordFromVertex(f.vertices[i]);
		}
	}

	private TextureCoordinate getUVCoordFromVertex(Vertex v)
	{
		//The difference in the x and y coordinates of the vertex and the top left vertex.
		float dx = v.x - topLeftVertex.x;
		float dz = v.z - topLeftVertex.z;
		
		float dw = Math.abs(dx) > Math.abs(dz) ? dx : dz;

		float dy = v.y - topLeftVertex.y;

		//Proportionally, how far the vertex is along the width and the height of the side.
		float xProportion = dw/sideWidth;
		float yProportion = dy/sideHeight;

		float newU = topLeftUVCoord.u + uvwidth*xProportion;
		float newV = topLeftUVCoord.v + uvheight*yProportion;

		return new TextureCoordinate(newU, newV);
	}

	
	public boolean isMapInCorrectPlaneForFace(Face f)
	{
		Vertex[] orderedVertices = TextureUtil.orderVertices(f.vertices);

		//True if x changes and not z.
		boolean faceXDom = true; 

		//Generates the horizontal difference for the face, and works out if it is in the correct plane for this uvmap.
		float xDif = Math.abs(orderedVertices[0].x - orderedVertices[1].x);
		float zDif = Math.abs(orderedVertices[0].z - orderedVertices[1].z);

		if(zDif > xDif)
		{
			faceXDom = false;
		}
		
		return (faceXDom && xDominant) || (!faceXDom && !xDominant);
		
	}
	
	public float getDistanceFromPlane(Vertex v)
	{
		if(xDominant)
		{
			return Math.abs(v.z - topLeftVertex.z);
		}
		
		return Math.abs(v.x - topLeftVertex.x);
	}

}