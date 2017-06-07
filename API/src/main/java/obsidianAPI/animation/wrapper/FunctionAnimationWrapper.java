package obsidianAPI.animation.wrapper;

import obsidianAPI.animation.AnimationSequence;

public class FunctionAnimationWrapper extends AnimationWrapper {

	private IsActiveFunction<Integer, Boolean> isActiveFunction;

	public static void main(String[] args) {
		IsActiveFunction<Integer, Boolean> returnTrue = (i) -> { return i > 10; };
		FunctionAnimationWrapper wrapper = new FunctionAnimationWrapper(null, 0, returnTrue);
		System.out.println(wrapper.isActive());
	}
		
	public FunctionAnimationWrapper(AnimationSequence animation, int priority, IsActiveFunction<Integer, Boolean> isActiveFunction) {
		super(animation, priority);
		this.isActiveFunction = isActiveFunction;
	}

	@Override
	public boolean isActive() {
		return isActiveFunction.apply(8);
	}

	@FunctionalInterface
	public interface IsActiveFunction <IntArg, BoolReturn> { 
		public BoolReturn apply (IntArg i);
	}

}
