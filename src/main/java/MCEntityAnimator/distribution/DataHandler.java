package MCEntityAnimator.distribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class DataHandler
{
	
	//private static List<FileInfo> fileList = new ArrayList<FileInfo>();
	
	public static void downloadFileList()
	{
		try 
		{
			//System.out.println(ServerAccess.executeCommand("/home/shared/getFileData.sh dabigjoe"));
			ServerAccess.getFile("animation/user", "animation");
			ServerAccess.getFile("animation/shared", "/home/shared");
			MCEA_Main.dataHandler.loadNBTData();
		} 
		catch (IOException e) {e.printStackTrace();}
		catch (JSchException e) {e.printStackTrace();}
	}

	public void saveNBTData()
	{	
		List<String> entityNames = getEntities();
		//GUI
		//writeNBTToFile(AnimationData.getGUISetupTag(entityNames), getGUIFile());
		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names
			writeNBTToFile(AnimationData.getEntityDataTag(entityName), getEntityDataFile(entityName));
			//Sequences
						
			List<String> changeSequences = AnimationData.getChangedSequences(entityName);
			System.out.println(entityName + " " + changeSequences);
			for(AnimationSequence s : AnimationData.getSequences(entityName))
			{
//				if(changeSequences.contains(s.getName()))
				System.out.println("Saving " + getAnimationFile(entityName, s.getName()));
					writeNBTToFile(s.getSaveData(), getAnimationFile(entityName, s.getName()));
			}
		}
	}

	public void loadNBTData()
	{	
		List<String> entityNames = getEntities();
		//GUI
		//		File guiDataFile = getGUIFile();
		//		if(guiDataFile.exists())
		//			AnimationData.loadGUISetup(getNBTFromFile(guiDataFile));


		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names)
			File entityDataFile = getEntityDataFile(entityName);
			if(entityDataFile.exists())
				AnimationData.loadEntityData(entityName, getNBTFromFile(entityDataFile));
			
			System.out.println("Adding sequences for " + entityName);
			
			//Sequences
			for(File animationFile : getAnimationFiles(entityName))
			{
				AnimationSequence sequence = new AnimationSequence(entityName, getNBTFromFile(animationFile));
				System.out.println("  " + sequence.getName());
				AnimationData.addSequence(entityName, sequence);
			}
		}
	}

	/**
	 * Write an NBTTagCompound to a file.
	 */
	private static void writeNBTToFile(NBTTagCompound nbt, File file)
	{
		try 
		{
			if(!file.exists())
				file.createNewFile();
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Read an NBTTagCompound from a file.
	 */
	private static NBTTagCompound getNBTFromFile(File file)
	{
		try 
		{
			return CompressedStreamTools.readCompressed(new FileInputStream(file));
		} 
		catch (FileNotFoundException e) {throw new RuntimeException(e);}
		catch (IOException e) {throw new RuntimeException(e);}
	}

	/**
	 * Get the list of entities available for animation. Searches the /data/shared folder.
	 */
	public static List<String> getEntities()
	{
		List<String> entities = new ArrayList<String>();
		File dataFolder = new File(MCEA_Main.animationPath + "/shared/animation");
		for(File file : dataFolder.listFiles())
		{
			if(file.isDirectory())
				entities.add(file.getName());
		}
		return entities;
	}

	private static File getGUIFile()
	{
		return new File(MCEA_Main.animationPath + "/data/GuiData.data");
	}

	private static File getEntityDataFile(String entityName)
	{
		return new File(MCEA_Main.animationPath + "/shared/animation/" + entityName +  "/" + entityName + ".data");
	}

	private static List<File> getAnimationFiles(String entityName)
	{
		List<File> animationFiles = new ArrayList<File>();
		System.out.println(entityName);
		File animationFolder = new File(MCEA_Main.animationPath + "/user/" + entityName);
		animationFolder.mkdir();
		for(File f : animationFolder.listFiles())
			animationFiles.add(f);
		return animationFiles;
	}

	private static File getAnimationFile(String entityName, String animationName)
	{
		return new File(MCEA_Main.animationPath + "/user/" + entityName + "/" + animationName + ".anim");
	}

}