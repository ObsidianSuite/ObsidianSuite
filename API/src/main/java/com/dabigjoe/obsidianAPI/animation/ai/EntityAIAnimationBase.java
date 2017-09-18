package com.dabigjoe.obsidianAPI.animation.ai;

import net.minecraft.entity.ai.EntityAIBase;

public abstract class EntityAIAnimationBase extends EntityAIBase implements IEntityAIAnimation {

	private String name;
	private boolean isExecuting;
	
	public EntityAIAnimationBase(String name) {
		this.name = name;
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
