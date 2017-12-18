package com.dabigjoe.obsidianOverhaul.entity.ai;

import com.dabigjoe.obsidianAPI.animation.ai.IEntityAIAnimation;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;

public class EntityAIPanicAnimation extends EntityAIPanic implements IEntityAIAnimation {

	public static String name = "Panic";
	private boolean isExecuting;
	
	public EntityAIPanicAnimation(EntityCreature entity, double speedMulitplier) {
		super(entity, speedMulitplier);
		isExecuting = false;
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		isExecuting = true;
	}
	
	@Override
	public void resetTask() {
		super.resetTask();
		isExecuting = false;
	}


	@Override
	public String getAIName() {
		return name;
	}

	@Override
	public boolean isExecuting() {
		return isExecuting;
	}

}
