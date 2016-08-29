package com.nthrootsoftware.mcea.animation;

import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Maps;

public class AnimationStance 
{
	
	private Map<String, float[]> partRotations = Maps.newHashMap();
	private String name;	

	public AnimationStance()
	{

	}
	
	public void setRotation(String partName, float[] rotation)
	{
		partRotations.put(partName, rotation);
	}

	public float[] getRotation(String partName) 
	{
		return partRotations.containsKey(partName) ? partRotations.get(partName) : new float[]{0, 0, 0};
	}
	
	public void setName(String par0Str) 
	{
		name = par0Str;
	}
	

	public String getName() 
	{
		return name;
	}


	public NBTBase getSaveData() 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Name", name);
		
		NBTTagList rotationList = new NBTTagList();
		for(String part : partRotations.keySet())
		{				
			NBTTagCompound rotationNBT = new NBTTagCompound();
			rotationNBT.setString("Part", part);
			rotationNBT.setFloat("X", partRotations.get(part)[0]);
			rotationNBT.setFloat("Y", partRotations.get(part)[1]);			
			rotationNBT.setFloat("Z", partRotations.get(part)[2]);
			rotationList.appendTag(rotationNBT);
		}		
		
		nbt.setTag("Rotation", rotationList);
		return nbt;
	}

	public void loadData(NBTTagCompound nbt) 
	{
		this.name = nbt.getString("Name");
		
		NBTTagList rotationList = nbt.getTagList("Rotation", 10);
		for(int i = 0; i < rotationList.tagCount(); i++)
		{
			NBTTagCompound rotationNBT = rotationList.getCompoundTagAt(i);
			float[] rotation = new float[3];
			rotation[0] = rotationNBT.getFloat("X");
			rotation[1] = rotationNBT.getFloat("Y");
			rotation[2] = rotationNBT.getFloat("Z");
			partRotations.put(rotationNBT.getString("Part"), rotation);
		}	
	}



	
}
