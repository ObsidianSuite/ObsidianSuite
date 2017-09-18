package com.dabigjoe.obsidianOverhaul.entity.saiga;

import com.dabigjoe.obsidianAPI.animation.wrapper.IEntityAnimated;
import com.dabigjoe.obsidianOverhaul.entity.ai.EntityAIEat;
import com.dabigjoe.obsidianOverhaul.entity.ai.EntityAIPanicAnimation;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.world.World;

public class EntitySaiga extends EntityCreature implements IEntityAnimated
{
	
	private boolean calling;
	
	public EntitySaiga(World world) 
	{
		super(world);
	}
	
	@Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(1, new EntityAIEat(this));
        this.tasks.addTask(2, new EntityAIPanicAnimation(this, 2.0D));
    }

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
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

