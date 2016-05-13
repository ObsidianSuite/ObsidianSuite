package MCEntityAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;

public class DataHandler
{


	private final static File resourceFolder = MCEA_Main.resourceFolder;
	private final static File saveFile = new File(resourceFolder, "AnimationData.data");

	public void saveNBTData()
	{			
		NBTTagCompound compoundToSave = new NBTTagCompound();
		AnimationData.saveData(compoundToSave);
		try 
		{
			CompressedStreamTools.writeCompressed(compoundToSave, new FileOutputStream(saveFile));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void loadNBTData()
	{	
		try
		{
			AnimationData.loadData(CompressedStreamTools.readCompressed(new FileInputStream(saveFile)));
			//CompressedStreamTools.readCompressed(new FileInputStream(saveFile));
		}
		catch(FileNotFoundException e)
		{
			try 
			{
				saveFile.createNewFile();
			}
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		
//		if(!importFolder.exists())
//		{
//			importFolder.mkdirs();
//		}
//
//		String[] entityFolders = importFolder.list();
//
//		for(String entity : entityFolders)
//		{
//			File entityFolder = new File(importFolder, entity);
//			String[] animationFiles = entityFolder.list();
//			for(String animationName : animationFiles)
//			{
//				File animationFile = new File(entityFolder, animationName);
//				try 
//				{
//					if(animationName.contains("Stance"))
//					{
//						NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(animationFile));
//						AnimationStance stance = new AnimationStance();
//						stance.loadData(nbt);
//						AnimationData.addNewStance(entity, stance);
//					}
//					else
//					{
//						NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(animationFile));
//						AnimationSequence sequence = new AnimationSequence("");
//						sequence.loadData(entity, nbt);
//						AnimationData.addNewSequence(entity, sequence);
//					}
//				}
//				catch (IOException e) 
//				{
//					e.printStackTrace();
//				}
//			}
//
//		}
	}




}