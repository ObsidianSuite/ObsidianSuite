package MCEntityAnimator.render.objRendering.bend;

import java.util.ArrayList;
import java.util.List;

import MCEntityAnimator.render.objRendering.bend.UVMap.PartUVMap;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

public class BendPart extends GroupObject
{

	//Sets of four vertices for the top and bottom of the sections.
	private boolean inverted;

	public BendPart(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
	{
		super("", 4);
		this.inverted = inverted;
		setupVertices(topVertices, bottomVertices, uvMap);
	}

	private void setupVertices(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap)
	{
		this.faces.clear();

		for(int i = 0; i < 4; i++)
		{
			int j = i == 3 ? 0 : i + 1;

			Vertex vA = topVertices[j];
			Vertex vB = bottomVertices[j];
			Vertex vC = topVertices[i];
			Face f = new Face();
			f.vertices = new Vertex[]{vC, vB, vA};
			faces.add(f);

			Vertex vD = bottomVertices[i];
			Vertex vE = topVertices[i];
			Vertex vF = bottomVertices[j];
			Face g = new Face();
			g.vertices = new Vertex[]{vF, vE, vD};
			faces.add(g);

			Vertex faceNormal = inverted ? g.calculateFaceNormal() : f.calculateFaceNormal();
			f.faceNormal = faceNormal;
			g.faceNormal = faceNormal;

			uvMap.setupFaceTextureCoordinates(f);
			uvMap.setupFaceTextureCoordinates(g);
		}
	}

	/**
	 * Update the top and bottom vertices.
	 * Also generates the faces represented by these vertices.
	 */
	public void updateVertices(Vertex[] topVertices, Vertex[] bottomVertices)
	{
		this.faces.clear();

		for(int i = 0; i < 4; i++)
		{
			int j = i == 3 ? 0 : i + 1;

			Vertex vA = topVertices[j];
			Vertex vB = bottomVertices[j];
			Vertex vC = topVertices[i];
			Face f = new Face();
			f.vertices = new Vertex[]{vC, vB, vA};
			faces.add(f);

			Vertex vD = bottomVertices[i];
			Vertex vE = topVertices[i];
			Vertex vF = bottomVertices[j];
			Face g = new Face();
			g.vertices = new Vertex[]{vF, vE, vD};
			faces.add(g);

			Vertex faceNormal = inverted ? g.calculateFaceNormal() : f.calculateFaceNormal();
			f.faceNormal = faceNormal;
			g.faceNormal = faceNormal;
		}
	}

}
