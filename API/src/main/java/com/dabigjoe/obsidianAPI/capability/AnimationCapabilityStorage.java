package com.dabigjoe.obsidianAPI.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class AnimationCapabilityStorage implements IStorage {

	@Override
	public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
		return null;
	}

	@Override
	public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {}

}
