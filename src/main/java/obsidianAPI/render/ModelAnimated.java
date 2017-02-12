package obsidianAPI.render;

import java.io.IOException;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.part.Part;
import obsidianAnimator.Util;

public class ModelAnimated extends ModelObj
{

	long animStartTime = System.nanoTime();
	
	public ModelAnimated(String entityName, ResourceLocation modelLocation, ResourceLocation textureLocation)
	{			
		super(entityName, modelLocation, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);

		if(isMoving(entity))
		{
			EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
			if(animProps != null)
			{
				AnimationSequence seq = animProps.getActiveAnimation();			
				//float time = Util.getAnimationFrameTime(animProps.getAnimationStartTime(), 0, seq.getFPS(), 1.0F);
				float time = Util.getAnimationFrameTime(animStartTime, 0, seq.getFPS(), 1.0F);

				if(time > seq.getTotalTime())
					animStartTime = System.nanoTime();


				seq.animateAll(time, this);	
			}
		}
		else
			animateToDefault();
		
		
		//System.out.println(lookX);
		
//		for(Part p : parts)
//		{
//			if(p.getDisplayName().equals("head"))
//			{
//				p.setValue((float) (-lookX/180F*Math.PI), 1);
//				p.setValue((float) (lookY/180F*Math.PI), 0);
//			}
//			else if(p.getDisplayName().equals("chestLw"))
//			{
//				p.setValue((float) (-lookX/180F*Math.PI), 1);
//			}
//		}
	}

	private boolean isMoving(Entity parEntity) 
	{
		return parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ) != 0;
	}
	
	private void animateToDefault()
	{
		for(Part part : parts)
			part.setValues(part.getOriginalValues());
	}
}
