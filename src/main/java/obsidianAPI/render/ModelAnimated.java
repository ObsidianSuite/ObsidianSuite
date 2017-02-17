package obsidianAPI.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationSequence;
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

				animateToPartValues(seq.getPartValuesAtTime(this, time));
			}
			else
			{
				animateToDefault();
			}
		}
	}

	protected boolean isMoving(Entity parEntity) 
	{
		return parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ) != 0;
	}
	
	private void animateToPartValues(Map<String, float[]> partValues)
	{
		//Set part rotations to state - lag
		for(Part part : parts)
			part.setValues(partValues.get(part.getName()));
		
		//Reduce lag
		
	}
	
	private void animateToDefault()
	{
		Map<String, float[]> defaultState = new HashMap<String, float[]>();
		for(Part part : parts)
			defaultState.put(part.getName(), part.getOriginalValues());
		animateToPartValues(defaultState);
	}
}
