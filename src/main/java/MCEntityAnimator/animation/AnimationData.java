package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Maps;

public class AnimationData 
{	
	private static Map<String, ArrayList<AnimationSequence>> sequences = Maps.newHashMap();	
	private static Map<String, ArrayList<AnimationStance>> stances = Maps.newHashMap();	
	private static Map<String, AnimationParenting> parenting = Maps.newHashMap();
	private static Map<String, String> animationSetup = Maps.newHashMap();
	private static Map<String, String> stanceSetup = Maps.newHashMap();
	private static Map<String, String> parentingSetup = Maps.newHashMap();


	public static AnimationParenting getAnipar(String par0Str) 
	{
		if(!parenting.containsKey(par0Str) || parenting.get(par0Str) == null)
		{
			parenting.put(par0Str, new AnimationParenting());
		}
		return parenting.get(par0Str);
	}

	/**
	 * Adds a new sequence to the existing list of sequences for the entity.
	 * @return false if there already exists a sequence of the same name (sequence won't be added in this case), true otherwise.
	 */
	public static boolean addNewSequence(String entityName, AnimationSequence sequence)
	{
		ArrayList sqs = sequences.get(entityName);
		if(sqs == null)
			sqs = new ArrayList<AnimationSequence>();
		if(!doesSequenceExistForEntity(sqs, sequence))
		{
			sqs.add(sequence);
			sequences.put(entityName, sqs);
			return true;
		}
		return false;
	}

	private static boolean doesSequenceExistForEntity(ArrayList<AnimationSequence> entitySequences, AnimationSequence sequenceToCheck)
	{
		for(AnimationSequence seq : entitySequences)
		{
			if(seq.getName().toLowerCase().equals(sequenceToCheck.getName().toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}

	public static ArrayList<AnimationSequence> getSequences(String entityName) 
	{
		return sequences.get(entityName) == null ? new ArrayList<AnimationSequence>() : sequences.get(entityName);
	}

	public static void addNewStance(String entityName, AnimationStance stance)
	{
		ArrayList sts = stances.get(entityName);
		if(sts == null){sts = new ArrayList<AnimationSequence>();}
		sts.add(stance);
		stances.put(entityName, sts);
	}

	public static ArrayList<AnimationStance> getStances(String entityName) 
	{
		return stances.get(entityName) == null ? new ArrayList<AnimationStance>() : stances.get(entityName);
	}



	public static void deleteSequence(String entityName, AnimationSequence sequence) 
	{
		ArrayList<AnimationSequence> temp = sequences.get(entityName);
		temp.remove(sequence);
		sequences.put(entityName, temp);
	}


	public static void deleteStance(String entityName, AnimationStance stance) 
	{
		ArrayList<AnimationStance> temp = stances.get(entityName);
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

	public static void saveData(NBTTagCompound compound) 
	{
		NBTTagList entityList = new NBTTagList();
		for(String entity : parenting.keySet())
		{
			NBTTagCompound entityCompound = new NBTTagCompound();
			entityCompound.setString("EntityName", entity);
			String setup;
			if((setup = getAnimationSetup(entity)) != null)
			{
				entityCompound.setString("AnimationSetup", setup);
			}
			if((setup = getStanceSetup(entity)) != null)
			{
				entityCompound.setString("StanceSetup", setup);
			}
			if((setup = getParentingSetup(entity)) != null)
			{
				entityCompound.setString("ParentingSetup", setup);
			}
			entityList.appendTag(entityCompound);
			compound.setTag(entity + "Parenting", getAnipar(entity).getSaveData(entity));

			if(sequences.containsKey(entity))
			{
				NBTTagList sequenceList = new NBTTagList();
				for(AnimationSequence sequence : sequences.get(entity))
				{				
					sequenceList.appendTag(sequence.getSaveData());
				}		
				compound.setTag(entity + "Sequences", sequenceList);		
			}

			if(stances.containsKey(entity))
			{
				NBTTagList stanceList = new NBTTagList();
				for(AnimationStance stance : stances.get(entity))
				{				
					stanceList.appendTag(stance.getSaveData());
				}		
				compound.setTag(entity + "Stances", stanceList);		
			}
		}
		compound.setTag("Entities", entityList);
	}

	public static void loadData(NBTTagCompound compound) 
	{
		NBTTagList entityList = compound.getTagList("Entities", 10);
		for(int i = 0; i < entityList.tagCount(); i++)
		{
			NBTTagCompound entityCompound = entityList.getCompoundTagAt(i);
			String entity = entityCompound.getString("EntityName");
			setAnimationSetup(entity, entityCompound.getString("AnimationSetup"));
			setStanceSetup(entity, entityCompound.getString("StanceSetup"));
			setParentingSetup(entity, entityCompound.getString("ParentingSetup"));
			AnimationParenting anipar = getAnipar(entity);
			anipar.loadData(compound.getCompoundTag(entity + "Parenting"), entity);

			ArrayList<AnimationSequence> sqs = new ArrayList<AnimationSequence>();
			NBTTagList sequenceList = compound.getTagList(entity + "Sequences", 10);
			for(int j = 0; j < sequenceList.tagCount(); j++)
			{
				AnimationSequence sequence = new AnimationSequence("");
				sequence.loadData(entity, sequenceList.getCompoundTagAt(j));
				sqs.add(sequence);
			}
			sequences.put(entity, sqs);

			ArrayList<AnimationStance> sts = new ArrayList<AnimationStance>();
			NBTTagList stanceList = compound.getTagList(entity + "Stances", 10);
			for(int j = 0; j < stanceList.tagCount(); j++)
			{
				AnimationStance stance = new AnimationStance();
				stance.loadData(stanceList.getCompoundTagAt(j));
				sts.add(stance);
			}
			stances.put(entity, sts);
		}
	}



}
