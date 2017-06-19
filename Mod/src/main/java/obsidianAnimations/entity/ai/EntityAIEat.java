package obsidianAnimations.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.wrapper.IEntityAIAnimation;

public class EntityAIEat extends EntityAIBase implements IEntityAIAnimation {

	private EntityCreature entity;
	private boolean isExecuting;
	public static final String name = "Eat";
	
	public EntityAIEat(EntityCreature entity) {
		this.entity = entity;
		this.isExecuting = false;
	}

	@Override
	public String getAIName() {
		return name;
	}

	@Override
	public boolean isExecuting() {
		return isExecuting;
	}
	
	@Override
	public boolean shouldExecute() {
		isExecuting = entity.getRNG().nextInt(50) == 0;
		return isExecuting;
	}
	
	public boolean continueExecuting()
	{
		return !EntityAnimationProperties.get(entity).getActiveAnimation().equals("Eat");
	}

}
