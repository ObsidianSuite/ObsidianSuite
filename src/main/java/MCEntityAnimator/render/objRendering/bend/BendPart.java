package MCEntityAnimator.render.objRendering.bend;

import java.util.ArrayList;
import java.util.List;

import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.bend.UVMap.PartUVMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
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
	
	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	public void updateTextureCoordinates(boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
	{		
		boolean useHighlightCoords = true;
		ResourceLocation texture;
		TextureCoordinate[] highlightCoords = new TextureCoordinate[]{
				new TextureCoordinate(0.0F, 0.0F), 
				new TextureCoordinate(0.5F, 0.0F), 
				new TextureCoordinate(0.0F, 0.5F)};
		if(mainHighlight)
			texture = ModelObj.pinkResLoc;
		else if(otherHighlight)
			texture = ModelObj.whiteResLoc;
		else
		{
			texture = modelObj.getTexture();
			useHighlightCoords = false;
		}
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);		
		
		for(int i = 0; i < 8; i++)
		{
			Face f = faces.get(i);
			if(useHighlightCoords)
				f.textureCoordinates = highlightCoords;
			else
				f.textureCoordinates = faceTextureCoords.get(i);
		}
	}

}