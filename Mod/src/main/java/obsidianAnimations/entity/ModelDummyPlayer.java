package obsidianAnimations.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.render.ModelAnimated;

public class ModelDummyPlayer extends ModelAnimated
{

	//private PartObj head, chestLw;
	
	public ModelDummyPlayer(String entityName, WavefrontObject object, ResourceLocation texture)
	{
		super(entityName, object, texture);
		//head = getPartObjFromName("head");
		//chestLw = getPartObjFromName("chestLw");
	}
	
	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);
		//head.setValue((float) (lookY/180F*Math.PI), 0);
		//chestLw.setValue((float) (-lookX/180F*Math.PI), 1);
	}

	@Override
	protected void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps) {
		
	}	

}
