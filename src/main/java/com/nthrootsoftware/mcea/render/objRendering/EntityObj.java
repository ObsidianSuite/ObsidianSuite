package com.nthrootsoftware.mcea.render.objRendering;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class EntityObj extends EntityMob
{
	private ItemStack heldItem;
	private String entityType;
	
	public EntityObj(World world, String type) 
	{
		super(world);
		entityType = type;
	}

	public void setCurrentItem(ItemStack par1ItemStack) 
	{
		 this.heldItem = par1ItemStack;
	}
	
    @Override
	public ItemStack getHeldItem()
	{
		return heldItem;
	}

	public String getType() 
	{
		return entityType;
	}

}
