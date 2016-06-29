package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import MCEntityAnimator.render.objRendering.ModelObj;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/*
 * Contains all the data for animations and models.
 * 
 * Also holds information regarding the setup of GUIs (zoom, rotation, position etc.)
 */

public class AnimationData 
{	

	//All sequences, stances and parenting data.
	private static Map<String, List<AnimationSequence>> sequences = Maps.newHashMap();	
	private static Map<String, List<AnimationStance>> stances = Maps.newHashMap();	
	private static Map<String, AnimationParenting> parenting = Maps.newHashMap();

	//Setup for GUIs
	private static Map<String, String> animationSetup = Maps.newHashMap();
	private static Map<String, String> stanceSetup = Maps.newHashMap();
	private static Map<String, String> parentingSetup = Maps.newHashMap();
	private static Map<String, Integer> animationItems = Maps.newHashMap();

	//List of part names and groupings. 
	private static Map<String, PartGroupsAndNames> partGroupsAndNames = Maps.newHashMap();

	/**
	 * Get the animation parenting that applies to this model.
	 * Will return a new one if one doesn't already exist.
	 */
	public static AnimationParenting getAnipar(String model) 
	{
		if(!parenting.containsKey(model) || parenting.get(model) == null)
			parenting.put(model, new AnimationParenting());
		return parenting.get(model);
	}

	/**
	 * Adds the sequence to the list of sequences for the given entity.
	 * Will overwrite any sequence with the same name.
	 */
	public static void addSequence(String entityName, AnimationSequence sequence)
	{
		List<AnimationSequence> sqs = sequences.get(entityName);
		if(sqs == null)
			sqs = new ArrayList<AnimationSequence>();
		AnimationSequence existingSeq = getSequenceFromName(entityName, sequence.getName());
		if(existingSeq != null)
			sqs.remove(existingSeq);
		sqs.add(sequence);
		sequences.put(entityName, sqs);
	}

	public static AnimationSequence getSequenceFromName(String entityName, String animationName)
	{
		if(sequences.get(entityName) != null)
		{
			for(AnimationSequence s : sequences.get(entityName))
			{
				System.out.println(s.getName());
				if(s.getName().equals(animationName))
				{
					return s;
				}
			}
		}
		return null;
	}

	public static List<AnimationSequence> getSequences(String entityName) 
	{
		return sequences.get(entityName) == null ? new ArrayList<AnimationSequence>() : sequences.get(entityName);
	}
	
	public static boolean sequenceExists(String entityName, String animationName)
	{
		List<AnimationSequence> seqs = getSequences(entityName);
		for(AnimationSequence seq : seqs)
		{
			if(seq.getName().equals(animationName))
				return true;
		}
		return false;
	}

	public static void addNewStance(String entityName, AnimationStance stance)
	{
		List<AnimationStance> sts = stances.get(entityName);
		if(sts == null){sts = new ArrayList<AnimationStance>();}
		sts.add(stance);
		stances.put(entityName, sts);
	}

	public static List<AnimationStance> getStances(String entityName) 
	{
		return stances.get(entityName) == null ? new ArrayList<AnimationStance>() : stances.get(entityName);
	}

	public static void deleteSequence(String entityName, AnimationSequence sequence) 
	{
		List<AnimationSequence> temp = sequences.get(entityName);
		temp.remove(sequence);
		sequences.put(entityName, temp);
	}

	public static void deleteStance(String entityName, AnimationStance stance) 
	{
		List<AnimationStance> temp = stances.get(entityName);
		temp.remove(stance);
		stances.put(entityName, temp);
	}

	public static void setAnimationSetup(String entityName, String setup)
	{
		if(!setup.equals(""))
		{
			animationSetup.put(entityName, setup);
		}
	}

	public static String getAnimationSetup(String entityName)
	{

		return animationSetup.get(entityName);
	}

	public static void setStanceSetup(String entityName, String setup)
	{
		if(!setup.equals(""))
		{
			stanceSetup.put(entityName, setup);
		}
	}

	public static String getStanceSetup(String entityName)
	{
		return stanceSetup.get(entityName);
	}

	public static void setParentingSetup(String entityName, String setup)
	{
		if(!setup.equals(""))
		{
			parentingSetup.put(entityName, setup);
		}
	}

	public static String getParentingSetup(String entityName)
	{
		return parentingSetup.get(entityName);
	}

	public static PartGroupsAndNames getPartGroupsAndNames(String entityName, ModelObj model)
	{
		if(partGroupsAndNames.containsKey(entityName) && partGroupsAndNames.get(entityName) != null)
			return partGroupsAndNames.get(entityName);
		PartGroupsAndNames p = new PartGroupsAndNames(model);
		partGroupsAndNames.put(entityName, p);
		return p;
	}

	public static ItemStack getAnimationItem(String animationName)
	{
		Integer id;
		if((id = animationItems.get(animationName)) != null)
		{
			Item item;
			Block block;
			if((item = Item.getItemById(id)) != null)
				return new ItemStack(item);
			else if((block = Block.getBlockById(id)) != null)
				return new ItemStack(block);
			else
				throw new RuntimeException("Unable to get item or block for id " + id);
		}
		return null;
	}
	
	public static void setAnimationItem(String animationName, int id)
	{
		animationItems.put(animationName, id);
	}
	
	public static NBTTagCompound getGUISetupTag(List<String> entities)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList entityList = new NBTTagList();
		for(String entity : entities)
		{ 
			NBTTagCompound guiSetupCompound = new NBTTagCompound();
			guiSetupCompound.setString("EntityName", entity);
			if(animationSetup.get(entity) != null)
				guiSetupCompound.setString("AnimationSetup", animationSetup.get(entity));
			if(parentingSetup.get(entity) != null)
				guiSetupCompound.setString("ParentingSetup", parentingSetup.get(entity));
			if(stanceSetup.get(entity) != null)
				guiSetupCompound.setString("StanceSetup", stanceSetup.get(entity));
			entityList.appendTag(guiSetupCompound);
		}
		nbt.setTag("GuiSetup", entityList);
		
		NBTTagList animationItemList = new NBTTagList();
		for(Entry<String, Integer> e : animationItems.entrySet())
		{
			NBTTagCompound animationItem = new NBTTagCompound();
			animationItem.setString("name", e.getKey());
			animationItem.setInteger("id", e.getValue());
			animationItemList.appendTag(animationItem);
		}
		nbt.setTag("AnimationItems", animationItemList);
		
		return nbt;
	}	

	public static void loadGUISetup(NBTTagCompound nbt)
	{
		System.out.println("Loading gui setup...");
		NBTTagList entityList = nbt.getTagList("GuiSetup", 10);
		for(int i = 0; i < entityList.tagCount(); i++)
		{
			NBTTagCompound guiSetupCompound = entityList.getCompoundTagAt(i);
			String entityName = guiSetupCompound.getString("EntityName");
			setAnimationSetup(entityName, guiSetupCompound.getString("AnimationSetup"));
			setStanceSetup(entityName, guiSetupCompound.getString("ParentingSetup"));
			setParentingSetup(entityName, guiSetupCompound.getString("StanceSetup"));
		}
		
		NBTTagList animationItemList = nbt.getTagList("AnimationItems", 10);
		for(int i = 0; i < animationItemList.tagCount(); i++)
		{
			NBTTagCompound animationItem = animationItemList.getCompoundTagAt(i);
			setAnimationItem(animationItem.getString("name"), animationItem.getInteger("id"));
		}
		
		System.out.println(" Done");
	}	

	public static NBTTagCompound getEntityDataTag(String entityName) 
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("Parenting", getAnipar(entityName).getSaveData(entityName));
		if(partGroupsAndNames.get(entityName) != null)
			compound.setTag("GroupsAndName", partGroupsAndNames.get(entityName).getSaveData(entityName));
		return compound;
	}

	public static void loadEntityData(String entityName, NBTTagCompound compound)
	{
		AnimationParenting anipar = getAnipar(entityName);
		anipar.loadData(compound.getCompoundTag("Parenting"), entityName);

		PartGroupsAndNames p = partGroupsAndNames.get(entityName);
		p.loadData(compound.getCompoundTag("GroupsAndName"), entityName);
	}

}
