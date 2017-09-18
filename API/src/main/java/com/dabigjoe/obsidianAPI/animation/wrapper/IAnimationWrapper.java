package com.dabigjoe.obsidianAPI.animation.wrapper;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;

import net.minecraft.entity.EntityLivingBase;

/**
 * A wrapper for animations. 
 * Contains extra information that {@link AnimationSequence} does not:
 *   Priority, loop, transition time, and a method to determine if the animation should be played.
 */
public interface IAnimationWrapper {

	/**
	 * @return The animation sequence this wrapper pertains to. 
	 */
	public AnimationSequence getAnimation();
	
	/**
	 * Work out if the current animation should be played.
	 */
	public boolean isActive(EntityLivingBase entity);
	
	/**
	 * @return Priority of this animation. Higher number = lower priority. 
	 */
	public int getPriority();
	
	/**
	 * @return Whether the given animation loops or not.
	 */
	public boolean getLoops();
	
	/**
	 * @return The amount of time given to transition to this animation.
	 */
	public float getTransitionTime();
	
}
