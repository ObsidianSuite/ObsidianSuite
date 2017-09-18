package com.dabigjoe.obsidianAPI.animation;

import java.util.List;
import java.util.Set;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.PartObj;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AnimationParenting
{
	/**
	 * To check that a child is not related to a parent in anyway ie(not a grandchild or great grandchild etc.)
	 * Returns true is there is no relation
	 */
	public static boolean areUnrelated(PartObj child, PartObj parent)
	{
		PartObj c = child;
		while(c.hasParent())
		{
			PartObj p = c.getParent();
			if(p.equals(parent))
				return false;
			c = p;
		}
		return true;
	}

	public static NBTTagCompound getSaveData(ModelObj model)
	{
		NBTTagCompound parentNBT = new NBTTagCompound();
		NBTTagList parentNBTList = new NBTTagList();

		for (PartObj part : model.getPartObjs())
		{
			Set<PartObj> children = part.getChildren();
			List<PartObj> mergeParts = part.getMergedParts();
			if (!children.isEmpty() || !mergeParts.isEmpty())
			{
				NBTTagCompound parentCompound = new NBTTagCompound();
				parentCompound.setString("Parent", part.getName());

				int i = 0;
				for (PartObj child : children)
				{
					String name = child.getName();
					if (child.hasBend())
					{
						name += "*";
					}
					parentCompound.setString("Child" + i, name);
					i++;
				}
				
				for(int j = 0; j < mergeParts.size(); j++)
				{
					PartObj mergedPart = mergeParts.get(j);
					parentCompound.setString("Merged" + j, mergedPart.getName());
				}

				parentNBTList.appendTag(parentCompound);
			}

		}

		parentNBT.setTag("Parenting", parentNBTList);
		return parentNBT;
	}

	public static void loadData(NBTTagCompound compound, ModelObj model)
	{
		NBTTagList parentNBTList = compound.getTagList("Parenting", 10);

		for (int i = 0; i < parentNBTList.tagCount(); i++)
		{
			NBTTagCompound parentCompound = parentNBTList.getCompoundTagAt(i);
			PartObj parent = model.getPartObjFromName(parentCompound.getString("Parent"));
			int j = 0;
			while (parentCompound.hasKey("Child" + j))
			{
				String name = parentCompound.getString("Child" + j);
				boolean hasBend = false;
				if (name.endsWith("*"))
				{
					name = name.substring(0, name.length() - 1);
					hasBend = true;
				}
				PartObj child = model.getPartObjFromName(name);

				model.setParent(child, parent, hasBend);
				j++;
			}
			
			j = 0;
			while (parentCompound.hasKey("Merged" + j))
			{
				String name = parentCompound.getString("Merged" + j);
				PartObj mergedPart = model.getPartObjFromName(name);
				model.addMerge(parent, mergedPart);
				j++;
			}	
		}
	}
}
