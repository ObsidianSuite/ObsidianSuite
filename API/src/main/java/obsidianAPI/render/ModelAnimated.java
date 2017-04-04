package obsidianAPI.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.Quaternion;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelAnimated extends ModelObj
{
	public static final float ANIM_MERGE_DURATION = 0.125f;

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
			animateToDefault(null);
		}
		else
		{
			updateMoveAnimation(entity,animProps);

			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq != null)
			{
				float time = animProps.getAnimationFrameTime();
				animProps.updateFrameTime(this,time);

				animateToPartValues(animProps, seq.getPartValuesAtTime(this, time));
			}
			else
			{
				animateToDefault(animProps);
			}
		}
	}

	protected void updateMoveAnimation(Entity entity, EntityAnimationProperties animProps)
	{
		boolean isMoving = isMoving(entity);
		if (isMoving && animProps.getActiveAnimation() == null)
		{
			animProps.setActiveAnimation(this,"WalkF", true);
		} else if (!isMoving && animProps.getActiveAnimation() != null && animProps.getActiveAnimation().getName().equals("WalkF"))
		{
			animProps.clearAnimation(this);
		}
	}

	protected boolean isMoving(Entity parEntity)
	{
		return parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ) > 0.15D;
	}

	private void animateToPartValues(EntityAnimationProperties animProps, Map<String, float[]> partValues)
	{
		Map<String, float[]> prevValues = animProps == null ? getDefaultPartValues() : animProps.getPrevValues();
		if (prevValues == null)
			prevValues = getDefaultPartValues();

		double dt = animProps == null ? Double.POSITIVE_INFINITY : (System.nanoTime() - animProps.getAnimationStartTime()) / 1000000000.0;

		float t = (float) Math.min(dt / ANIM_MERGE_DURATION, 1f);
		for (Part part : parts)
		{
			float[] prevVals = prevValues.get(part.getName());
			float[] targetVals = partValues.get(part.getName());

			if (t >= 1f || !prevValues.containsKey(part.getName()))
			{
				part.setValues(partValues.get(part.getName()));
			} else
			{
				Quaternion prevQuart = Quaternion.fromEuler(prevVals[0], prevVals[1], prevVals[2]);
				Quaternion targetQuart = Quaternion.fromEuler(targetVals[0], targetVals[1], targetVals[2]);
				Quaternion interpolatedQ = Quaternion.slerp(prevQuart, targetQuart, t);
				part.setValues(interpolatedQ.toEuler());
			}
		}
	}

	private void animateToDefault(EntityAnimationProperties animProps)
	{
		animateToPartValues(animProps, getDefaultPartValues());
	}

	private Map<String, float[]> getDefaultPartValues()
	{
		Map<String, float[]> defaultState = new HashMap<String, float[]>();
		for(Part part : parts)
			defaultState.put(part.getName(), part.getOriginalValues());
		return defaultState;
	}
}
