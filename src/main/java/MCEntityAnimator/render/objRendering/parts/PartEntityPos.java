package MCEntityAnimator.render.objRendering.parts;

import MCEntityAnimator.render.objRendering.ModelObj;
import net.minecraft.entity.Entity;

public class PartEntityPos extends Part
{

	public PartEntityPos(ModelObj mObj) 
	{
		super(mObj, "entitypos");
	}

	@Override
	public void move(Entity entity) 
	{
		entity.posX = valueX;
		entity.posY = valueY;
		entity.posZ = valueZ;
	}

}
