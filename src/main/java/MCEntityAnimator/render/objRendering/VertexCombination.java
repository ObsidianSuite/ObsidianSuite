package MCEntityAnimator.render.objRendering;

import net.minecraftforge.client.model.obj.Vertex;

public class VertexCombination
{
	Vertex v1;
	Vertex v2;

	float dW;
	float dY;

	VertexCombination(Vertex v1, Vertex v2)
	{
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public Vertex[] getVertices()
	{
		return new Vertex[]{v1, v2};
	}

}
