package obsidianAPI.animation.wrapper;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;

public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction isActiveFunction;
		
	public FunctionAnimationWrapper(ResourceLocation resourceLocation, int priority, IsActiveFunction isActiveFunction) throws IOException {
		this(AnimationRegistry.loadAnimation(resourceLocation), priority, isActiveFunction);
	}
	
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, IsActiveFunction isActiveFunction) {
		super(animation, priority);
		this.isActiveFunction = isActiveFunction;
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
