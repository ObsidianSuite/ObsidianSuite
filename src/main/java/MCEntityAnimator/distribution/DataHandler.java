package MCEntityAnimator.distribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class DataHandler
{

	public void saveNBTData()
	{			
		if(ServerAccess.username != null)
		{
			List<String> entityNames = getEntities();
			//GUI
			writeNBTToFile(AnimationData.getGUISetupTag(entityNames), getGUIFile());
			//Entity data
			for(String entityName : entityNames)
			{
				//Parenting and part names
				writeNBTToFile(AnimationData.getEntityDataTag(entityName), getEntityDataFile(entityName));
				//Sequences
				for(AnimationSequence s : AnimationData.getSequences(entityName))
					writeNBTToFile(s.getSaveData(), getAnimationFile(entityName, s.getName()));
			}
		}
	}

	public void loadNBTData()
	{	
		System.out.println("----------- Loading animation data -----------");

		List<String> entityNames = getEntities();
		//GUI
		File guiDataFile = getGUIFile();
		if(guiDataFile.exists())
			AnimationData.loadGUISetup(getNBTFromFile(guiDataFile));
		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names)
			File entityDataFile = getEntityDataFile(entityName);
			if(entityDataFile.exists())
				AnimationData.loadEntityData(entityName, getNBTFromFile(entityDataFile));

			//Sequences
			for(File animationFile : getAnimationFiles(entityName))
			{
				AnimationSequence sequence = new AnimationSequence(entityName, getNBTFromFile(animationFile));
				AnimationData.addSequence(entityName, sequence);
			}

		}
	}

	private static void writeNBTToFile(NBTTagCompound nbt, File file)
	{
		try 
		{
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	private static NBTTagCompound getNBTFromFile(File file)
	{
		try 
		{
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		} 
		catch (FileNotFoundException e) {throw new RuntimeException(e);}
		catch (IOException e) {throw new RuntimeException(e);}
	}

	public static List<String> getEntities()
	{
		List<String> entities = new ArrayList<String>();
		File dataFolder = new File(MCEA_Main.animationPath + "/data/shared");
		for(File file : dataFolder.listFiles())
		{
			if(file.isDirectory())
				entities.add(file.getName());
		}
		return entities;
	}

	private static File getGUIFile()
	{
		return new File(MCEA_Main.animationPath + "/data/" + ServerAccess.username +  "/GuiData.data");
	}

	private static File getEntityDataFile(String entityName)
	{
		return new File(MCEA_Main.animationPath + "/data/shared/" + entityName +  "/" + entityName + ".data");
	}

	private static List<File> getAnimationFiles(String entityName)
	{
		List<File> animationFiles = new ArrayList<File>();
		File animationFolder = new File(MCEA_Main.animationPath + "/data/" + ServerAccess.username + "/" + entityName);
		for(File f : animationFolder.listFiles())
			animationFiles.add(f);
		return animationFiles;
	}
	
	private static File getAnimationFile(String entityName, String animationName)
	{
		return new File(MCEA_Main.animationPath + "/data/" + ServerAccess.username + "/" + entityName + "/" + animationName + ".data");
	}

}