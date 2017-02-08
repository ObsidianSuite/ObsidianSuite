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

	public ModelAnimated(String entityName, ResourceLocation modelLocation, ResourceLocation textureLocation) throws IOException
	{			
		super(entityName, Minecraft.getMinecraft().getResourceManager().getResource(modelLocation).getInputStream(), textureLocation);
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) 
	{				
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

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
