package obsidianAnimations.entity.saiga;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.world.World;
import obsidianAPI.animation.wrapper.IEntityAnimated;
import obsidianAnimations.entity.ai.EntityAIEat;
import obsidianAnimations.entity.ai.EntityAIPanicAnimation;

public class EntitySaiga extends EntityCreature implements IEntityAnimated
{
	
	private boolean calling;
	
	public EntitySaiga(World world) 
	{
		super(world);
		this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(1, new EntityAIPanicAnimation(this, 2.0D));
        this.tasks.addTask(2, new EntityAIEat(this));
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

	@Override
	public boolean isMoving() {
		return limbSwingAmount > 0.02F;
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if(!calling && this.rand.nextFloat() < 0.01) 
			calling = true;
	}
	
	public void setCalling(boolean calling) {
		this.calling = calling;
	}

	public boolean isCalling() {
		return calling;
	}
	
}

