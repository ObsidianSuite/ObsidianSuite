package obsidianAPI.animation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.PartObj;

import java.util.Set;

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
			if (!children.isEmpty())
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
		}
	}
}
