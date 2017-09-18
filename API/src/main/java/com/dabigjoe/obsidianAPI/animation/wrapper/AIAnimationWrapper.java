package com.dabigjoe.obsidianAPI.animation.wrapper;

import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.render.ModelAnimated;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class AIAnimationWrapper extends AnimationWrapper {

	private String aiName;
	
	public AIAnimationWrapper(String aiName, ResourceLocation resource, int priority, boolean loops) {
		this(aiName, resource, priority, loops, ModelAnimated.DEF_TRANSITION_TIME);
	}
	
	public AIAnimationWrapper(String aiName, ResourceLocation resource, int priority, boolean loops, float transitionTime) {
		super(resource, priority, loops, transitionTime);
		this.aiName = aiName;
	}
	
	@Override
	public boolean isActive(EntityLivingBase entity) {
		if(entity instanceof EntityLiving) {
			EntityLiving entityLiving = (EntityLiving) entity;
			return ObsidianAPIUtil.isEntityAITaskActive(entityLiving, aiName);
		}
		return false;
	}

}
