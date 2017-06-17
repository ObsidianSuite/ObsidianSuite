package obsidianAnimations.entity.saiga;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import obsidianAnimations.entity.ai.EntityAIWanderAlways;

public class EntitySaiga extends EntityCreature
{
	
	public EntitySaiga(World world) 
	{
		super(world);
		this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIWanderAlways(this, 1.0D));
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
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.18D);
    }
	
}

