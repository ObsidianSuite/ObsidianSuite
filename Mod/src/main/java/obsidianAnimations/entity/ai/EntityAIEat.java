package obsidianAnimations.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIEat extends EntityAIBase {

	private EntityCreature entity;
	
	public EntityAIEat(EntityCreature entity) {
		this.entity = entity;
	}
	
	
	@Override
	public boolean shouldExecute() {
		return entity.getRNG().nextInt(50) == 0;
	}
	
	public boolean continueExecuting()
	{
		System.out.println("Eating!");
		return super.continueExecuting();
	}

}
