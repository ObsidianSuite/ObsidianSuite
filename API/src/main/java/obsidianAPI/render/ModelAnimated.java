package obsidianAPI.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

import java.util.Map;

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
			parts.forEach(Part::setToOriginalValues);
		}
		else
		{
			updateMoveAnimation(entity, animProps);

			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq == null)
			{
				animProps.setActiveAnimation(this,"Idle",true);
				seq = animProps.getActiveAnimation();
			}

			animProps.updateFrameTime();

			float time = animProps.getAnimationFrameTime();
			animateToPartValues(animProps, seq.getPartValuesAtTime(this, time));
			animProps.updateAnimation(this, time);
		}
	}

	protected void updateMoveAnimation(Entity entity, EntityAnimationProperties animProps)
	{
		boolean isMoving = isMoving(entity);
		if (isMoving && isIdle(animProps))
		{
			animProps.setActiveAnimation(this,"WalkF", true);
		} else if (!isMoving && !isIdle(animProps) && animProps.getActiveAnimation().getName().equals("WalkF"))
		{
			animProps.clearAnimation(this);
		}
	}

	protected boolean isIdle(EntityAnimationProperties animProps)
	{
		return animProps.getActiveAnimation() == null || animProps.getActiveAnimation().getName().equals("Idle");
	}

	protected boolean isMoving(Entity parEntity)
	{
		return parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ) > 0.15D;
	}

	private void animateToPartValues(EntityAnimationProperties animProps, Map<String, float[]> partValues)
	{
		parts.forEach(p -> p.setValues(partValues.get(p.getName())));
	}
}
