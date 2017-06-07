package obsidianAPI.animation.wrapper;

import obsidianAPI.animation.AnimationSequence;

public abstract class AnimationWrapper implements IAnimationWrapper {

	private AnimationSequence animation;
	private final int priority;
	
	public AnimationWrapper(AnimationSequence animation, int priority) {
		this.animation = animation;
		this.priority = priority;
	}

	@Override
	public AnimationSequence getAnimation() {
		return animation;
	}

	@Override
	public int getPriority() {
		return priority;
	}
	
}
