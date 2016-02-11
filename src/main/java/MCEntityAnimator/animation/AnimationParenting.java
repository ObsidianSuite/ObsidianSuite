package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import MCEntityAnimator.Util;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;

public class AnimationParenting 
{
	private ArrayList<PartObj> children = new ArrayList<PartObj>();
	private ArrayList<PartObj> parents = new ArrayList<PartObj>();

	public AnimationParenting()
	{
	}

	public PartObj getParent(PartObj child)
	{
		return parents.get(children.indexOf(child));
	}

	public ArrayList<PartObj> getChildren(PartObj parent) 
	{
		ArrayList<PartObj> childrenToReturn = new ArrayList<PartObj>();
		for(int i = 0; i < parents.size(); i++)
		{
			PartObj mr = parents.get(i);
			if(mr.equals(parent))
			{
				childrenToReturn.add(children.get(i));
			}
		}
		return childrenToReturn;
	}

	public void addParent(PartObj parent, PartObj child)
	{
		parents.add(parent);
		children.add(child);
		parent.addChild(child);
	}

	public boolean hasParent(PartObj child) 
	{
		return children.contains(child);
	}

	public boolean isParent(PartObj parent) 
	{
		return parents.contains(parent);
	}

	public ArrayList<PartObj> getAllParents()
	{
		return parents;
	}
	
	public ArrayList<PartObj> getAllChildren()
	{
		return children;
	}

	/**
	 * To check that a child is not related to a parent in anyway ie(not a grandchild or great grandchild etc.)
	 * Returns true is there is no relation
	 */
	public boolean areUnrelated(PartObj child, PartObj parent) 
	{
		PartObj mr = child;
		while(hasParent(mr))
		{
			PartObj mr2 = getParent(mr);
			if(mr2.equals(parent))
			{
				return false;
			}
			mr = mr2;
		}
		return true;
	}

	public void unParent(PartObj child)
	{
		int index = this.children.indexOf(child);
		this.parents.get(index).removeChild(child);
		this.parents.remove(index);
		if(child.hasBend())
		{
			child.removeBend();
		}
	}

	public NBTTagCompound getSaveData(String entityName) 
	{	
		NBTTagCompound parentNBT = new NBTTagCompound();
		NBTTagList parentNBTList = new NBTTagList();
		ArrayList<PartObj> temp = Util.removeDuplicates(parents);
		for(PartObj parent : temp)
		{
			NBTTagCompound parentCompound = new NBTTagCompound();
			parentCompound.setString("Parent", parent.getName());
			ArrayList<PartObj> children = this.getChildren(parent);
			for(int i = 0; i < children.size(); i++)
			{
				String name = children.get(i).getName();
				if(children.get(i).hasBend())
				{
					name = name + "*";
				}
				parentCompound.setString("Child" + i, name);
			}
			parentNBTList.appendTag(parentCompound);
		}
		parentNBT.setTag("Parenting", parentNBTList);
		return parentNBT;
	}

	public void loadData(NBTTagCompound compound, String entityName) 
	{	
		EntityObj entity = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		if(entity != null)
		{
			ModelObj entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entity)).getModel(entityName);
			NBTTagList parentNBTList = compound.getTagList("Parenting", 10);	

			for (int i = 0; i < parentNBTList.tagCount(); i++)
			{
				NBTTagCompound parentCompound = parentNBTList.getCompoundTagAt(i);
				PartObj parent = Util.getPartFromName(parentCompound.getString("Parent"), entityModel.parts);
				int j = 0;
				while(parentCompound.hasKey("Child" + j))
				{
					String name = parentCompound.getString("Child" + j);
					boolean hasBend = false;
					if(name.endsWith("*"))
					{
						name = name.substring(0, name.length() - 1);
						hasBend = true;
					}
					PartObj child = Util.getPartFromName(name, entityModel.parts);
					entityModel.setParent(child, parent, hasBend);
					j++;
				}
			}
		}
	}

	public void clear(EntityObj entity) 
	{
//		ModelObj model = ((RenderObj) RenderManager.instance.getEntityRenderObject(entity)).getModel(entity.getType());
//
//		for(PartObj parent : parents)
//		{
//			ArrayList<PartObj> temp = this.getChildren(parent);
//			for(PartObj mr : temp)
//			{
//				float[] arr = model.getDefaults(mr);
//				mr.setRotationPoint(new float[]{arr[0], arr[1], arr[2]});
//			}
//			parent.clearChildModels();
//		}
//
//		children.clear();
//		parents.clear();
//		//((GuiAnimationParenting) Minecraft.getMinecraft().currentScreen).popUp("Restart Minecraft to clear.", 0xffff0000);
		
		
		Iterator childrenIterator = children.iterator();

		while(childrenIterator.hasNext())
		{
			PartObj child = (PartObj) childrenIterator.next();
			unParent(child);
			childrenIterator.remove();
		}
		
	}
	
}
