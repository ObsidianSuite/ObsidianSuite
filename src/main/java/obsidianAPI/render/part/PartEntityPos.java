package obsidianAPI.render.part;

import net.minecraft.entity.Entity;
import obsidianAnimator.render.objRendering.ModelObj;

/**
 * Part for tracking the position of the model.
 */
public class PartEntityPos extends Part
{

	public PartEntityPos(ModelObj mObj) 
	{
		super(mObj, "entitypos");
	}

	public void move(Entity entity) 
	{
		entity.posX = valueX;
		entity.posY = valueY;
		entity.posZ = valueZ;
	}

}
