package com.nthrootsoftware.mcea;

import javax.activation.DataHandler;

import com.nthrootsoftware.mcea.data.Persistence;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPropertiesAnimation implements IExtendedEntityProperties
{

	@Override
	public void init(Entity entity, World world){}

	@Override
	public void saveNBTData(NBTTagCompound nbtTagCompound)
	{
		Persistence.save();
	}

	@Override
	public void loadNBTData(NBTTagCompound nbtTagCompound)
	{
		Persistence.load();
	}
}
