package MCEntityAnimator.render.objRendering.bend;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;

public class BendPart extends GroupObject
{
	
	//Sets of four vertices for the top and bottom of the sections.
	private Vertex[] topVertices;
	private Vertex[] bottomVertices;
	
	public BendPart()
	{
		super("", 2);
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
		
		boolean upsideDown = bottomVertices[0].y > topVertices[0].y;
		
		for(int i = 0; i < 4; i++)
		{
			int j = i == 3 ? 0 : i + 1;
			
			Vertex vA = topVertices[i];
			Vertex vB = topVertices[j];
			Vertex vC = bottomVertices[i];
//			if(!Double.isNaN(vA.y))
//				BendHelper.outputVertex(vA);
			Face f = new Face();
			f.vertices = new Vertex[]{vA, vB, vC};
			faces.add(f);

			Vertex vD = topVertices[j];
			Vertex vE = bottomVertices[i];
			Vertex vF = bottomVertices[j];
			Face g = new Face();
			g.vertices = new Vertex[]{vD, vE, vF};
			faces.add(g);
			
			//Flip face normal if upside parenting
			Vertex faceNormal = upsideDown ? g.calculateFaceNormal() : f.calculateFaceNormal();
			f.faceNormal = faceNormal;
			g.faceNormal = faceNormal;
		}
	}

}
