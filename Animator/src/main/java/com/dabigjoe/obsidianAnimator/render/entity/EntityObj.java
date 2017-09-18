package com.dabigjoe.obsidianAnimator.render.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

public class EntityObj extends EntityMob
{
	private ItemStack heldItemRight = ItemStack.EMPTY;
	private ItemStack heldItemLeft = ItemStack.EMPTY;
	private String entityType;
	
	public EntityObj(World world)
	{
		super(world);
	}
	
	public EntityObj(World world, String type) 
	{
		super(world);
		entityType = type;
	}

	public void setCurrentItem(ItemStack itemStack, EnumHandSide handSide) 
	{
		if(handSide == EnumHandSide.RIGHT)
			heldItemRight = itemStack;
		else
			heldItemLeft = itemStack;
	}
	
    @Override
	public ItemStack getHeldItem(EnumHand hand)
	{
		return hand == EnumHand.MAIN_HAND ? getHeldItemMainhand() : getHeldItemOffhand();
	}
    
	@Override
	public ItemStack getHeldItemMainhand() {
		return heldItemRight;
	}

	@Override
	public ItemStack getHeldItemOffhand() {
		return heldItemLeft;
	}

	public String getType() 
	{
		return entityType;
	}

}
