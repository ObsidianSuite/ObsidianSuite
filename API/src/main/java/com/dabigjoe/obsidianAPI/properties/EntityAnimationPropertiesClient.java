package com.dabigjoe.obsidianAPI.properties;

import java.util.Map;

import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.registry.AnimationRegistry;
import com.dabigjoe.obsidianAPI.render.ModelAnimated;
import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;

public class EntityAnimationPropertiesClient implements IAnimationProperties
{
	
	private String entityName;
	private EntityLivingBase entity;
	private float prevEntityPosX, prevEntityPosZ;

	private AnimationSequence activeAnimation;
	private long animationStartTime;
	private boolean loop;
	private float frameTime = 0f;	
	
	private Runnable onFinished;

	@Override
	public void init(Entity entity)
	{
		this.entity = (EntityLivingBase) entity;
		entityName = AnimationRegistry.getEntityName(entity.getClass());
	}

	public void updateFrameTime()
	{
		if (activeAnimation == null)
			frameTime = 0f;
		else
			frameTime = ObsidianAPIUtil.getAnimationFrameTime(System.currentTimeMillis(), animationStartTime, 0, activeAnimation.getFPS(), 1.0f);
	}

	public void setActiveAnimation(ModelAnimated model, String animationName, long animationStartTime, boolean loopAnim, float transitionTime)
	{    	
		AnimationSequence sequence = AnimationRegistry.getAnimation(entityName, animationName);

		this.animationStartTime = animationStartTime;
		prevEntityPosX = 0f;
		prevEntityPosZ = 0f;
		if (transitionTime > 0.001f)
		{
			updateFrameTime();
			Map<String, float[]> currentValues = activeAnimation != null ? getCurrentValues(model) : getOriginalValues(model);
			loop = false;

			if(sequence != null)
				activeAnimation = ObsidianAPIUtil.createTransition(model, sequence.getName(), currentValues, sequence.getPartValuesAtTime(model,0f),transitionTime);
			else
				activeAnimation = ObsidianAPIUtil.createTransition(model, "idle", currentValues, getOriginalValues(model), transitionTime);
			onFinished = () ->
			{
				onFinished = null;
				loop = sequence != null ? loopAnim : false;
				activeAnimation = sequence;
				this.animationStartTime = System.currentTimeMillis();
			};
		}
		else
		{
			this.loop = loopAnim;
			activeAnimation = sequence;
		}
	}

	public void returnToIdle(ModelAnimated model, float transitionTime)
	{
		if(activeAnimation == null || activeAnimation.getName().equals("Idle"))
			return;
		setActiveAnimation(model, "Idle", System.currentTimeMillis(), true, transitionTime);
	}

	public void returnToIdle(ModelAnimated model)
	{
		returnToIdle(model, ModelAnimated.DEF_TRANSITION_TIME);
	}

	private Map<String, float[]> getOriginalValues(ModelObj model)
	{
		Map<String, float[]> values = Maps.newHashMap();

		for (Part part : model.parts)
		{
			values.put(part.getName(), part.getOriginalValues());
		}

		return values;
	}

	private Map<String, float[]> getCurrentValues(ModelObj model)
	{
		Map<String, float[]> values = Maps.newHashMap();

		float time = getAnimationFrameTime();
		for (Part part : model.parts)
		{
			values.put(part.getName(), activeAnimation.getPartValueAtTime(part, time));
		}

		return values;
	}

	public float getAnimationFrameTime()
	{
		return frameTime;
	}

	public AnimationSequence getActiveAnimation()
	{
		return activeAnimation;
	}

	public void runAnimationTick(ModelAnimated model)
	{
		if (activeAnimation != null)
		{
			Part entityPos = model.getPartFromName("entitypos");
			if (entityPos != null && this.entity.equals(Minecraft.getMinecraft().player))
			{            	
				float entityPosX = entityPos.getValue(0);
				float entityPosZ = entityPos.getValue(2);
				
				float strafe = entityPosX - prevEntityPosX;
				float forward = entityPosZ - prevEntityPosZ;

				float f4 = MathHelper.sin(entity.rotationYaw * (float)Math.PI / 180.0F);
				float f5 = MathHelper.cos(entity.rotationYaw * (float)Math.PI / 180.0F);
				entity.setPosition(entity.posX + (double)(strafe * f5 - forward * f4), entity.posY,entity.posZ + (double)(forward * f5 + strafe * f4));

				prevEntityPosX = entityPosX;
				prevEntityPosZ = entityPosZ;
			}

			if (frameTime > activeAnimation.getTotalTime())
			{
				if (loop)
					setActiveAnimation(model, activeAnimation.getName(), System.currentTimeMillis(), true, 0f);
				else if (onFinished != null)
					onFinished.run();
				else
					returnToIdle(model);
			}
		}
	}
	
	public static EntityAnimationPropertiesClient get(Entity entity) {
		return (EntityAnimationPropertiesClient) EntityAnimationPropertiesProvider.get(entity, Side.CLIENT);
	}

}
