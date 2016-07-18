package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import MCEntityAnimator.Util;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AnimationParenting 
{

	private Map<PartObj, List<PartObj>> parentingMap = new HashMap<PartObj, List<PartObj>>();
	
	/**
	 * Get the parent of a child, or null if it doesn't exist.
	 */
	public PartObj getParent(PartObj child)
	{
		for(Entry<PartObj, List<PartObj>> e : parentingMap.entrySet())
		{
			if(e.getValue().contains(child))
				return e.getKey();
		}
		return null;
	}

	/**
	 * Get the list of children belongning to this parent.
	 */
	public List<PartObj> getChildren(PartObj parent) 
	{
		return parentingMap.get(parent);
	}

	/**
	 * Add a relationship between parent and child.
	 */
	public void addParenting(PartObj parent, PartObj child)
	{
		List<PartObj> list = parentingMap.get(parent);
		if(list == null)
			list = new ArrayList<PartObj>();
		if(!list.contains(child))
			list.add(child);
		parentingMap.put(parent, list);
	}

	public boolean hasParent(PartObj child) 
	{
		for(List<PartObj> list : parentingMap.values())
		{
			if(list.contains(child))
				return true;
		}
		return false;
	}

	public boolean isParent(PartObj parent) 
	{
		return parentingMap.keySet().contains(parent);
	}

	public List<PartObj> getAllParents()
	{
		List<PartObj> allParents = new ArrayList<PartObj>();
		allParents.addAll(parentingMap.keySet());
		return allParents;
	}
	
	public List<PartObj> getAllChildren()
	{
		List<PartObj> allChildren = new ArrayList<PartObj>();
		for(List<PartObj> list : parentingMap.values())
		{
			allChildren.addAll(list);
		}
		return allChildren;
	}

	/**
	 * To check that a child is not related to a parent in anyway ie(not a grandchild or great grandchild etc.)
	 * Returns true is there is no relation
	 */
	public boolean areUnrelated(PartObj child, PartObj parent) 
	{
		PartObj c = child;
		while(hasParent(c))
		{
			PartObj p = getParent(c);
			if(p.equals(parent))
				return false;
			c = p;
		}
		return true;
	}

	/**
	 * Removes the relationship between parent and child. 
	 * Will also remove the bend if one exists.
	 */
	public void unParent(PartObj child)
	{
		if(hasParent(child))
		{
			PartObj p = getParent(child);
			List<PartObj> list = parentingMap.get(p);
			list.remove(child);
			if(list.isEmpty())
				parentingMap.remove(p);
			else
				parentingMap.put(p, list);
			if(child.hasBend())
				child.removeBend();
		}
	}

	public NBTTagCompound getSaveData(String entityName) 
	{	
		NBTTagCompound parentNBT = new NBTTagCompound();
		NBTTagList parentNBTList = new NBTTagList();
		for(PartObj parent : parentingMap.keySet())
		{
			NBTTagCompound parentCompound = new NBTTagCompound();
			parentCompound.setString("Parent", parent.getName());
			List<PartObj> children = this.getChildren(parent);
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
				PartObj parent = Util.getPartObjFromName(parentCompound.getString("Parent"), entityModel.parts);
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
					PartObj child = Util.getPartObjFromName(name, entityModel.parts);
					try 
					{
						entityModel.setParent(child, parent, hasBend);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					j++;
				}
			}
		}
	}

	public void clear() 
	{	
		Iterator childrenIterator = getAllChildren().iterator();

		while(childrenIterator.hasNext())
		{
			PartObj child = (PartObj) childrenIterator.next();
			unParent(child);
			childrenIterator.remove();
		}
		
	}
	
}
