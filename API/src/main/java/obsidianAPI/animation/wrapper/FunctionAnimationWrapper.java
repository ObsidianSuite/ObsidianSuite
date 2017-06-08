package obsidianAPI.animation.wrapper;

import net.minecraft.entity.Entity;
import obsidianAPI.animation.AnimationSequence;

public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction isActiveFunction;
		
	
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, IsActiveFunction isActiveFunction) {
		super(animation, priority);
		this.isActiveFunction = isActiveFunction;
		IsActiveFunction returnTrue = (swingTime, swingMax, clock, lookX, lookY, f5, entity) -> { return true; };
	}
	
	@Override
	public boolean isActive(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity) {
		return isActiveFunction.apply(swingTime, swingMax, clock, lookX, lookY, f5, entity);
	}

	@FunctionalInterface
	public interface IsActiveFunction { 
		public boolean apply (float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity);
	}
	
}
