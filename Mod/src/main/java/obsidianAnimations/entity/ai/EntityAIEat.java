package obsidianAnimations.entity.ai;

import net.minecraft.entity.EntityCreature;
import obsidianAPI.animation.ai.EntityAIAnimationBase;

public class EntityAIEat extends EntityAIAnimationBase {

	private EntityCreature entity;
	public static String name = "Eat";
	private final int limit = 50;
	private int counter = 0;
	
	public EntityAIEat(EntityCreature entity) {
		super("Eat");
		this.entity = entity;
		this.setMutexBits(7);
	}
	
	@Override
	public void startExecuting() {
		super.startExecuting();
		counter = 0;
	}
	
	@Override
	public void resetTask() {
		super.resetTask();
		counter = 0;
	}
	
	@Override
	public boolean shouldExecute() {
		return entity.getRNG().nextInt(50) == 0;
	}
	
	public boolean continueExecuting()
	{
		if(counter < limit) {
			counter++;
			return true;
		}
		return entity.getRNG().nextInt(200) == 0;
	}

}
