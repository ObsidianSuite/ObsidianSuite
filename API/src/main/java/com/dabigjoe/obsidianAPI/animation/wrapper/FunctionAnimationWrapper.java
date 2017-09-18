package com.dabigjoe.obsidianAPI.animation.wrapper;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;

import net.minecraft.entity.EntityLivingBase;

/**
 * An implementation of AnimationWrapper/IAnimationWrapper that uses lambdas to determine if the function is active. 
 */
public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction isActiveFunction;
	
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, boolean loops, float transitionTime, IsActiveFunction isActiveFunction) {
		super(animation, priority, loops, transitionTime);
		this.isActiveFunction = isActiveFunction;
	}
	
	@Override
	public boolean isActive(EntityLivingBase entity) {
		return isActiveFunction.apply(entity);
	}

	/**
	 * Lambda entity -> boolean
	 * Used to determine is the animation should play.
	 */
	@FunctionalInterface
	public interface IsActiveFunction { 
		public boolean apply (EntityLivingBase entity);
	}
	
}
