package com.dabigjoe.obsidianAPI.animation.wrapper;

import java.io.IOException;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.registry.AnimationRegistry;

import net.minecraft.util.ResourceLocation;

/**
 * A basic implementation of IAnimationWrapper
 */
public abstract class AnimationWrapper implements IAnimationWrapper {

	private AnimationSequence animation;
	private final int priority;
	private final boolean loops;
	private final float transitionTime;
	
	public AnimationWrapper(ResourceLocation resource, int priority, boolean loops, float transitionTime) {
		try {
			animation = AnimationRegistry.loadAnimation(resource);
		} catch (IOException e) {
			System.out.println("Unable to load animation from " + resource.getResourceDomain() + " " + resource.getResourcePath());
			e.printStackTrace();
			animation = null;
		}
		this.priority = priority;
		this.loops = loops;
		this.transitionTime = transitionTime;
	}
	
	public AnimationWrapper(AnimationSequence animation, int priority, boolean loops, float transitionTime) {
		this.animation = animation;
		this.priority = priority;
		this.loops = loops;
		this.transitionTime = transitionTime;
	}

	@Override
	public AnimationSequence getAnimation() {
		return animation;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public boolean getLoops() {
		return loops;
	}

	@Override
	public float getTransitionTime() {
		return transitionTime;
	}
	
	
}
