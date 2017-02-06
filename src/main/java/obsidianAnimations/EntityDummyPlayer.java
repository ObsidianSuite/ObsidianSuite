package obsidianAnimations;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.world.World;

public class EntityDummyPlayer extends EntityCreature
{
	
	public EntityDummyPlayer(World world) 
	{
		super(world);
		this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIWander(this, 1.0D));
	}
	
	@Override
	protected boolean isAIEnabled()
	{
	   return true;
	}

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22D);
    }
	
}
