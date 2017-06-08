package obsidianAPI.animation.wrapper;

import net.minecraft.entity.Entity;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.ModelAnimated;

public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction isActiveFunction;
	
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, boolean loops, float transitionTime, IsActiveFunction isActiveFunction) {
		super(animation, priority, loops, transitionTime);
		this.isActiveFunction = isActiveFunction;
	}
	
	@Override
	public boolean isActive(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, ModelAnimated model, Entity entity) {
		return isActiveFunction.apply(swingTime, swingMax, clock, lookX, lookY, f5, model, entity);
	}

	@FunctionalInterface
	public interface IsActiveFunction { 
		public boolean apply (float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, ModelAnimated model, Entity entity);
	}
	
}
