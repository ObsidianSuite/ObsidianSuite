package MCEntityAnimator.render.objRendering;

import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

public class TextureCombination
{
	TextureCoordinate tc1;
	TextureCoordinate tc2;

	float dU;
	float dV;

	TextureCombination(TextureCoordinate tc1, TextureCoordinate tc2)
	{
		this.tc1 = tc1;
		this.tc2 = tc2;
	}
	
	public TextureCoordinate[] getCoords()
	{
		return new TextureCoordinate[]{tc1, tc2};
	}

}
