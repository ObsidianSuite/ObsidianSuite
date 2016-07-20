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
	private List<TextureCoordinate[]> faceTextureCoords;
	private boolean inverted;

	public BendPart(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
	{
		super("", 4);
		this.inverted = inverted;
		faceTextureCoords = new ArrayList<TextureCoordinate[]>();
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

			faceTextureCoords.add(f.textureCoordinates);
			faceTextureCoords.add(g.textureCoordinates);
		}
	}

	/**
	 * Update the top and bottom vertices.
	 * Also generates the faces represented by these vertices.
	 */
	public void updateVertices(Vertex[] topVertices, Vertex[] bottomVertices, boolean highlighted)
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

			if(highlighted)
			{
				TextureCoordinate cd = new TextureCoordinate(0.0F, 0.0F, 0.0F);
				f.textureCoordinates = new TextureCoordinate[]{cd, cd, cd};
				g.textureCoordinates = new TextureCoordinate[]{cd, cd, cd};
			}
			else
			{
				f.textureCoordinates = faceTextureCoords.get(i*2);
				g.textureCoordinates = faceTextureCoords.get(i*2 + 1);
			}
		}
	}

}
