package obsidianAPI.render;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

public abstract class ModelAnimated extends ModelObj
{
	public ModelAnimated(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation)
	{
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity)
	{
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);

		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
		{
			doDefaultAnimations(swingTime, swingMax, f2, lookX, lookY, f5, entity);
		}
		else
		{
			updateAnimation(swingTime, entity, animProps);
			
			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq == null)
			{
				animProps.setActiveAnimation(this,"Idle",true);
				seq = animProps.getActiveAnimation();
			}
			
			if(seq != null) {
				animProps.updateFrameTime();
				float time = animProps.getAnimationFrameTime();
				animateToPartValues(animProps, seq.getPartValuesAtTime(this, time));
				animProps.updateAnimation(this, time);
			}
		}
	}

	protected abstract void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps);
	
	protected void doDefaultAnimations(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity)
	{
		parts.forEach(Part::setToOriginalValues);
	}
	
//	protected void updateMoveAnimation(Entity entity, EntityAnimationProperties animProps)
//	{
//		boolean isMoving = isMoving(entity);
//		if (isMoving && isIdle(animProps))
//		{
//			animProps.setActiveAnimation(this,"WalkF", true);
//		} else if (!isMoving && !isIdle(animProps) && animProps.getActiveAnimation().getName().equals("WalkF"))
//		{
//			animProps.clearAnimation(this);
//		}
//	}

	protected boolean isIdle(EntityAnimationProperties animProps)
	{
		return animProps.getActiveAnimation() == null || animProps.getActiveAnimation().getName().equals("Idle");
	}

	protected void animateToPartValues(EntityAnimationProperties animProps, Map<String, float[]> partValues)
	{
		parts.forEach(p -> p.setValues(partValues.get(p.getName())));
	}
}
