package obsidianAPI.render;

import java.io.IOException;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.part.Part;

public abstract class ModelAnimated extends ModelObj
{
	public ModelAnimated(String entityName, ResourceLocation modelLocation, ResourceLocation textureLocation)
	{			
		super(entityName, modelLocation, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);

		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
		{
			animateToDefault();
		}
		else
		{
			boolean isMoving = isMoving(entity);
			if (isMoving && animProps.getActiveAnimation() == null)
			{
				animProps.setActiveAnimation("WalkF");
			}
			else if (!isMoving && animProps.getActiveAnimation() != null && animProps.getActiveAnimation().getName().equals("WalkF"))
			{
				animProps.clearAnimation();
			}

			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq != null)
			{
				float time = Util.getAnimationFrameTime(animProps.getAnimationStartTime(), 0, seq.getFPS(), 1.0F);
				animProps.updateFrameTime(time);

				seq.animateAll(time, this);
			}
			else
			{
				animateToDefault();
			}
		}
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
