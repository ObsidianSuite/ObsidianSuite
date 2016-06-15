package MCEntityAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.ServerAccess;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class DataHandler
{

	public void saveNBTData()
	{			
        try 
        {
			ServerAccess.uploadAll(new File(MCEA_Main.animationPath + "/data"), "");
		} catch (IOException e) {e.printStackTrace();}
		
		List<String> entityNames = getEntities();

		//GUI
		writeNBTToFile(AnimationData.getGUISetupTag(entityNames), MCEA_Main.animationPath + "/data/GuiData.data");
		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names
			writeNBTToFile(AnimationData.getEntityDataTag(entityName), MCEA_Main.animationPath + "/data/" + entityName + "/" + entityName + ".data");
			//Sequences
			for(AnimationSequence s : AnimationData.getSequences(entityName))
			{
				writeNBTToFile(s.getSaveData(), MCEA_Main.animationPath + "/data/" + entityName + "/animation/" + s.getName() + ".data");
			}
		}

	}

	public void loadNBTData()
	{	
        try 
        {
			ServerAccess.downloadData();
		} catch (IOException e) {e.printStackTrace();}
		
		List<String> entityNames = getEntities();
		//GUI
		File guiDataFile = new File(MCEA_Main.animationPath + "/data/GuiData.data");
		if(guiDataFile.exists())
			AnimationData.loadGUISetup(getNBTFromFile(guiDataFile));
		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names)
			File entityDataFile = new File(MCEA_Main.animationPath + "/data/" + entityName + "/" + entityName + ".data");
			if(entityDataFile.exists())
				AnimationData.loadEntityData(entityName, getNBTFromFile(entityDataFile));

			//Sequences
			for(File animationFile : getAnimationFiles(entityName))
			{
				AnimationSequence sequence = new AnimationSequence("");
				sequence.loadData(entityName, getNBTFromFile(animationFile));
				AnimationData.addNewSequence(entityName, sequence);
			}

		}
	}

	private static void writeNBTToFile(NBTTagCompound nbt, String path)
	{
		try 
		{
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(new File(path)));
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
		File dataFolder = new File(MCEA_Main.animationPath + "/data");
		for(File file : dataFolder.listFiles())
		{
			if(file.isDirectory())
				entities.add(file.getName());
		}
		return entities;
	}

	private static List<File> getAnimationFiles(String entityName)
	{
		List<File> animationFiles = new ArrayList<File>();
		File animationFolder = new File(MCEA_Main.animationPath + "/data/" + entityName + "/animation");
		for(File f : animationFolder.listFiles())
			animationFiles.add(f);
		return animationFiles;
	}

}