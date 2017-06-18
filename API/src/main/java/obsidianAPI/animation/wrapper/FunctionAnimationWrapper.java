package obsidianAPI.animation.wrapper;

import net.minecraft.entity.EntityLivingBase;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.ModelAnimated;

public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction isActiveFunction;
	
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, boolean loops, float transitionTime, IsActiveFunction isActiveFunction) {
		super(animation, priority, loops, transitionTime);
		this.isActiveFunction = isActiveFunction;
	}
	
	@Override
	public boolean isActive(EntityLivingBase entity, ModelAnimated model) {
		return isActiveFunction.apply(entity, model);
	}

	@FunctionalInterface
	public interface IsActiveFunction { 
		public boolean apply (EntityLivingBase entity, ModelAnimated model);
	}
	
}
