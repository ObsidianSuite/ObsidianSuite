package obsidianAPI.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelAnimated extends ModelObj
{
	
	private static double lagMin = 0.1D;
	private static double lagDelta = 0.05D;
	
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
				float time = Util.getAnimationFrameTime(animProps.getAnimationStartTime(), 0, seq.getFPS(), 1.0F);
				animProps.updateFrameTime(time);

				animateToPartValues(seq.getPartValuesAtTime(this, time), animProps.getPartLagMap());
			}
			else
			{
				animateToDefault(animProps.getPartLagMap());
			}
		}
	}

	protected void updateMoveAnimation(Entity entity, EntityAnimationProperties animProps)
	{
		boolean isMoving = isMoving(entity);
		if (isMoving && animProps.getActiveAnimation() == null)
		{
			animProps.setActiveAnimation("WalkF");
			animProps.setPartLagMap(generatePartLagMap(animProps.getActiveAnimation().getPartValuesAtTime(this, 0)));
		} else if (!isMoving && animProps.getActiveAnimation() != null && animProps.getActiveAnimation().getName().equals("WalkF"))
		{
			animProps.clearAnimation();
			animProps.setPartLagMap(generatePartLagMap(getDefaultPartValues()));
		}
	}

	protected boolean isMoving(Entity parEntity) 
	{
		return parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ) > 0.15D;
	}
	
	private void animateToPartValues(Map<String, float[]> partValues, Map<String, float[]> partLagMap)
	{
		//Set part rotations to state - lag
		for(Part part : parts)
		{
			//Get lag
			float[] lag = new float[]{0,0,0};
			if(partLagMap != null)
				lag = partLagMap.get(part.getName());
			
			//Generate new part rotation
			float[] targetRot = partValues.get(part.getName());
			float[] newRot = new float[3];
			for(int i = 0; i < 3; i++)
			{
				newRot[i] = targetRot[i] - lag[i];
				//Reduce lag
				if(lag[i] > 0)
				{
					if(lag[i] < lagMin)
						lag[i] = 0;
					else
						lag[i] -= lagDelta;
				}
				else if(lag[i] < 0)
				{
					if(lag[i] > -lagMin)
						lag[i] = 0;
					else
						lag[i] += lagDelta;
				}
			}
			
			//Actually set part rotation
			part.setValues(newRot);
			
			//Set new lag
			if(partLagMap != null)
				partLagMap.put(part.getName(), lag);
		}
		
		
	}
	
	private void animateToDefault(Map<String, float[]> partLagMap)
	{
		animateToPartValues(getDefaultPartValues(), partLagMap);
	}
	
	private Map<String, float[]> getDefaultPartValues()
	{
		Map<String, float[]> defaultState = new HashMap<String, float[]>();
		for(Part part : parts)
			defaultState.put(part.getName(), part.getOriginalValues());
		return defaultState;
	}
	
	
	private Map<String, float[]> generatePartLagMap(Map<String, float[]> partValues)
	{
		Map<String, float[]> partLagMap = new HashMap<String, float[]>();
		for(Part p : parts)
		{
			float[] target = partValues.get(p.getName());
			float[] lag = new float[3];
			for(int i = 0; i < 3; i++)
				lag[i] = target[i] - p.getValue(i);
			partLagMap.put(p.getName(), lag);
		}
		
		return partLagMap;
		
	}
}
