package com.dabigjoe.obsidianOverhaul;

import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.animation.wrapper.AIAnimationWrapper;
import com.dabigjoe.obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import com.dabigjoe.obsidianAPI.registry.AnimationRegistry;
import com.dabigjoe.obsidianOverhaul.entity.ai.EntityAIEat;
import com.dabigjoe.obsidianOverhaul.entity.ai.EntityAIPanicAnimation;
import com.dabigjoe.obsidianOverhaul.entity.saiga.EntitySaiga;

import net.minecraft.util.ResourceLocation;

public class CommonProxy
{	
	public void init() 
	{	
		ModEntities.registerEntities();
		registerRendering();
	}

	public void registerRendering() {}

	public void registerAnimations() 
	{
		AnimationRegistry.init();
		
		IsActiveFunction isWalking = (entity) -> { 
			return ObsidianAPIUtil.isEntityMoving(entity) && !entity.isSprinting() && !entity.isSneaking() && entity.onGround;
		};
		IsActiveFunction returnTrue = (entity) -> { 
			return true;
		};
		IsActiveFunction isCalling = (entity) -> { 
			return entity instanceof EntitySaiga ? ((EntitySaiga) entity).isCalling() : false;
		};
		
		AnimationRegistry.registerEntity(EntitySaiga.class, "saiga");
		AnimationRegistry.registerAnimation("saiga", "Walk", new ResourceLocation("mod_obsidian_animations:animations/saiga/Walk.oba"), 10, true, isWalking);
		AnimationRegistry.registerAnimation("saiga", "Eat", new AIAnimationWrapper(EntityAIEat.name, new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaEat.oba"), 50, true, 0.5F));
		AnimationRegistry.registerAnimation("saiga", "Panic", new AIAnimationWrapper(EntityAIPanicAnimation.name, new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaRun.oba"), 0, true));
		AnimationRegistry.registerAnimation("saiga", "Call", new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaCall.oba"), 70, false, isCalling);
		AnimationRegistry.registerAnimation("saiga", "Idle", new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaIdle.oba"), 100, true, returnTrue);

	}

}
