package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import MCEntityAnimator.render.objRendering.ModelObj;
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

	/**
	 * Saves all the data for animations and gui setup.
	 */
	public static void saveData() 
	{
		//			if(sequences.containsKey(entity))
		//			{
		//				NBTTagList sequenceList = new NBTTagList();
		//				for(AnimationSequence sequence : sequences.get(entity))
		//				{				
		//					sequenceList.appendTag(sequence.getSaveData());
		//				}		
		//				compound.setTag(entity + "Sequences", sequenceList);		
		//			}
		//
		//			//Save stances
		//			if(stances.containsKey(entity))
		//			{
		//				NBTTagList stanceList = new NBTTagList();
		//				for(AnimationStance stance : stances.get(entity))
		//				{				
		//					stanceList.appendTag(stance.getSaveData());
		//				}		
		//				compound.setTag(entity + "Stances", stanceList);		
		//			}
		//		}
	}

	/**
	 * Loads all the data
	 */
	public static void loadData(NBTTagCompound compound) 
	{
		//			//Load sequences
		//			ArrayList<AnimationSequence> sqs = new ArrayList<AnimationSequence>();
		//			NBTTagList sequenceList = compound.getTagList(entityName + "Sequences", 10);
		//			for(int j = 0; j < sequenceList.tagCount(); j++)
		//			{
		//				AnimationSequence sequence = new AnimationSequence("");
		//				sequence.loadData(entityName, sequenceList.getCompoundTagAt(j));
		//				sqs.add(sequence);
		//			}
		//			sequences.put(entityName, sqs);
		//
		//			//Load stances
		//			ArrayList<AnimationStance> sts = new ArrayList<AnimationStance>();
		//			NBTTagList stanceList = compound.getTagList(entityName + "Stances", 10);
		//			for(int j = 0; j < stanceList.tagCount(); j++)
		//			{
		//				AnimationStance stance = new AnimationStance();
		//				stance.loadData(stanceList.getCompoundTagAt(j));
		//				sts.add(stance);
		//			}
		//			stances.put(entityName, sts);
		//		}
	}




}
