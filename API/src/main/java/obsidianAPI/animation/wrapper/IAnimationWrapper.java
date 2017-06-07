package obsidianAPI.animation.wrapper;

import obsidianAPI.animation.AnimationSequence;

public interface IAnimationWrapper {

	/**
	 * @return The animation sequence this wrapper pertains to. 
	 */
	public AnimationSequence getAnimation();
	
	/**
	 * Work out if the current animation should be played.
	 */
	public boolean isActive();
	
	/**
	 * @return Priority of this animation. 0 highest, 100 lowest. 
	 */
	public int getPriority();
	
}
