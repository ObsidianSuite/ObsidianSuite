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
	private static Map<String, List<String>> changedSequences = Maps.newHashMap();	
	
	private static Map<String, List<AnimationStance>> stances = Maps.newHashMap();	
	private static Map<String, AnimationParenting> parenting = Maps.newHashMap();
	private static List<String> changedEntitySetups = new ArrayList<String>();

	//Setup for GUIs
	private static Map<String, String> guiSetup = Maps.newHashMap();
	private static Map<String, Integer> animationItems = Maps.newHashMap();

	//List of part names and groupings. 
	private static Map<String, PartGroups> partGroups= Maps.newHashMap();

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

	public static boolean getEntitySetupChanged(String entityName)
	{
		return changedEntitySetups.contains(entityName);
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
	
	public static void addChangedSequence(String entityName, String sequenceName)
	{
		List<String> sqs = changedSequences.get(entityName);
		if(sqs == null)
			sqs = new ArrayList<String>();
		if(!sqs.contains(sequenceName))	
			sqs.add(sequenceName);
		changedSequences.put(entityName, sqs);
	}	
	
	public static List<String> getChangedSequences(String entityName)
	{
		List<String> sqs = changedSequences.get(entityName);
		if(sqs == null)
			sqs = new ArrayList<String>();
		return sqs;
	}
	
	public static void clearChangedSequences(String entityName)
	{
		changedSequences.put(entityName, new ArrayList<String>());
	}

	public static AnimationSequence getSequenceFromName(String entityName, String animationName)
	{
		if(sequences.get(entityName) != null)
		{
			for(AnimationSequence s : sequences.get(entityName))
			{
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

	public static void setGUISetup(String entityName, String setup)
	{
		if(!setup.equals(""))
		{
			guiSetup.put(entityName, setup);
		}
	}

	public static String getGUISetup(String entityName)
	{

		return guiSetup.get(entityName);
	}

	public static PartGroups getPartGroups(String entityName, ModelObj model)
	{
		if(partGroups.containsKey(entityName) && partGroups.get(entityName) != null)
			return partGroups.get(entityName);
		PartGroups p = new PartGroups(model);
		partGroups.put(entityName, p);
		return p;
	}

	public static ItemStack getAnimationItem(String animationName)
	{
		Integer id;
		if((id = animationItems.get(animationName)) != null && id != -1)
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
			String setup = guiSetup.get(entity);
			if(setup != null && !setup.equals(""))
				guiSetupCompound.setString("GUISetup", setup);
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
			setGUISetup(entityName, guiSetupCompound.getString("GUISetup"));
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
		if(partGroups.get(entityName) != null)
			compound.setTag("Groups", partGroups.get(entityName).getSaveData(entityName));
		return compound;
	}

	public static void loadEntityData(String entityName, NBTTagCompound compound)
	{
		AnimationParenting anipar = getAnipar(entityName);
		anipar.loadData(compound.getCompoundTag("Parenting"), entityName);

		PartGroups p = partGroups.get(entityName);
		p.loadData(compound.getCompoundTag("Groups"), entityName);
	}

}
