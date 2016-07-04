package MCEntityAnimator.render.objRendering.bend;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;

public class BendPart extends GroupObject
{
	
	//Sets of four vertices for the top and bottom of the sections.
	private Vertex[] topVertices;
	private Vertex[] bottomVertices;
	private Vertex[] prevFaceNormal;
	
	public BendPart()
	{
		super("", 2);
		prevFaceNormal = new Vertex[4];
	}
	
	/**
	 * Update the top and bottom vertices.
	 * Also generates the faces represented by these vertices.
	 */
	public void updateVertices(Vertex[] topVertices, Vertex[] bottomVertices)
	{
		this.topVertices = topVertices;
		this.bottomVertices = bottomVertices;
		this.faces.clear();
		
		this.glDrawingMode = 4;
						
		for(int i = 0; i < 4; i++)
		{
			int j = i == 3 ? 0 : i + 1;
			
			Vertex vA = topVertices[j];
			Vertex vB = bottomVertices[j];
			Vertex vC = topVertices[i];
			Face f = new Face();
			f.vertices = new Vertex[]{vA, vB, vC};
			faces.add(f);

			Vertex vD = bottomVertices[i];
			Vertex vE = topVertices[i];
			Vertex vF = bottomVertices[j];
			Face g = new Face();
			g.vertices = new Vertex[]{vD, vE, vF};
			faces.add(g);
			
			Vertex faceNormal =  f.calculateFaceNormal();
			f.faceNormal = faceNormal;
			g.faceNormal = faceNormal;
		}
	}

}
