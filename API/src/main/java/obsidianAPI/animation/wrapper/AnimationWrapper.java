package obsidianAPI.animation.wrapper;

import obsidianAPI.animation.AnimationSequence;

public abstract class AnimationWrapper implements IAnimationWrapper {

	private AnimationSequence animation;
	private final int priority;
	private final boolean loops;
	private final float transitionTime;
	
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
