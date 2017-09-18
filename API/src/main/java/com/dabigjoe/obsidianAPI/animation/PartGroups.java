package com.dabigjoe.obsidianAPI.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAPI.render.part.PartObj;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Parts can be grouped together, eg upper and lower arm -> arm.
 */
public class PartGroups 
{
	private ModelObj model;
	//A mapping of group names to a list of parts in that group.
	private Map<String, List<PartObj>> groups;

	public PartGroups(ModelObj modelObj)
	{
		this.model = modelObj;

		groups = new HashMap<String, List<PartObj>>();

		addGroup("Default");
		for(PartObj part : model.getPartObjs())
		{
			setPartGroup("Default", part);
		}
	}

	/**
	 * @return A list of the groups.
	 */
	public String[] getGroupListAsArray()
	{
		String[] strArr = new String[groups.keySet().size()];
		int i = 0;
		for(String group : groups.keySet())
		{
			strArr[i] = group;
			i++;
		}
		return strArr;
	}

	/**
	 * @return A csv string of the groups.
	 */
	public String getGroupListAsString()
	{
		String list = "";
		for(String group : groups.keySet())
		{
			list += group + ", ";
		}
		if(list.length() > 0)
			list = list.substring(0, list.length() - 2);
		return list;
	}

	/**
	 * Add a new group to the group map if it is unique. 
	 * Will assign an empty array list to the group name.
	 * @param groupName The new name of the group to be added.
	 */
	public void addGroup(String groupName)
	{
		if(!groups.keySet().contains(groupName))
			groups.put(groupName, new ArrayList<PartObj>());
	}

	public void setPartGroup(String groupName, PartObj part)
	{
		if(groups.containsKey(groupName))
		{
			//Remove part from existing group
			for(String group : groups.keySet())
			{
				List<PartObj> currentParts = groups.get(group);
				if(currentParts.contains(part))
					currentParts.remove(part);
				groups.put(group, currentParts);
			}

			//Add part to new group.
			List<PartObj> currentParts = groups.get(groupName);
			currentParts.add(part);
			groups.put(groupName, currentParts);
		}	
	}

	public String getPartGroup(PartObj part)
	{
		for(Entry<String, List<PartObj>> s : groups.entrySet())
		{
			if(s.getValue().contains(part))
				return s.getKey();
		}
		return "Default";
	}

	public void changeOrder(Part part, int change)
	{
		int currentPos = 0;
		for(int i = 0; i < model.parts.size(); i++)
		{
			if(model.parts.get(i).equals(part))
				currentPos = i;
		}
		boolean flag = true;
		if(currentPos == 0 && change < 0)
			flag = false;
		if(currentPos == model.parts.size() - 1 && change > 0)
			flag = false;
		if(flag)
		{
			List<Part> parts = model.parts;
			synchronized ( parts )
			{
				model.parts.remove(part);
				model.parts.add(currentPos + change, part);
			}
		}
	}

	public NBTTagCompound getSaveData() 
	{	
		NBTTagCompound nbtToReturn = new NBTTagCompound();
		NBTTagList partList = new NBTTagList();
		for(PartObj part : model.getPartObjs())
		{
			NBTTagCompound partCompound = new NBTTagCompound();
			partCompound.setString("Name", part.getName());
			partCompound.setString("DisplayName", part.getDisplayName());
			partCompound.setString("Group", getPartGroup(part));
			partList.appendTag(partCompound);
		}
		nbtToReturn.setTag("Groups", partList);
		nbtToReturn.setTag("PartOrder", model.getPartOrderAsList());
		return nbtToReturn;
	}

	public void loadData(NBTTagCompound compound, ModelObj model) 
	{	
		NBTTagList partList = compound.getTagList("Groups", 10);
		if (compound.hasKey("PartOrder", Constants.NBT.TAG_LIST))
			model.setPartOrderFromList(compound.getTagList("PartOrder", Constants.NBT.TAG_STRING));
		for (int i = 0; i < partList.tagCount(); i++)
		{
			NBTTagCompound partCompound = partList.getCompoundTagAt(i);
			PartObj part = model.getPartObjFromName(partCompound.getString("Name"));
			part.setDisplayName(partCompound.getString("DisplayName"));
			String group = partCompound.getString("Group");
			if(!groups.containsKey(group))
				addGroup(group);
			setPartGroup(group, part);
		}
	}


}
