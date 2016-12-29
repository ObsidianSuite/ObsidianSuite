package com.nthrootsoftware.mcea.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.jcraft.jsch.JSchException;
import com.nthrootsoftware.mcea.ClientProxy;
import com.nthrootsoftware.mcea.MCEA_Main;
import com.nthrootsoftware.mcea.animation.AnimationData;
import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.distribution.FileInfo.Status;
import com.nthrootsoftware.mcea.distribution.job.JobHandler;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class DataHandler
{

	public static final String userPath = MCEA_Main.animationPath + "/user";
	public static final String sharedPath = MCEA_Main.animationPath + "/shared";
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static JobHandler jobHandler = new JobHandler();
	private static List<FileInfo> fileList = new ArrayList<FileInfo>();

	public static void generateFileList(String username)
	{
		try 
		{
			DataHandler.saveNBTData();
			fileList.clear();

			List<String> existingPaths = new ArrayList<String>();
			if(ServerAccess.online)
			{
				String serverFileOutput = ServerAccess.executeCommand("/home/shared/animation/getFileData.sh " + username);
				String[] fileStrings = serverFileOutput.split("\\r?\\n");

				for(String s : fileStrings)
				{
					String[] fileData = s.split("=");
					String path = fileData[0];
					if(fileData.length == 1)
						addFileInfo(path, getDateModifiedLocal(path), null);
					else
						addFileInfo(path, getDateModifiedLocal(path), dateFormat.parse(fileData[1]));
					existingPaths.add(path);
				}
			}

			//Check local files to see if file exists locally but not remotely.			
			for(String entity : getEntities())
			{
				for(String animationName : getEntityAnimations(entity))
				{
					String path = String.format("%s/%s", entity, animationName);
					if(!existingPaths.contains(path))
						addFileInfo(path, getDateModifiedLocal(path), null);
				}

				String path = String.format("%s/%s", entity, entity);
				if(!existingPaths.contains(path))
					addFileInfo(path, getDateModifiedLocal(path), null);
			}

			Collections.sort(fileList);
		} 
		catch (IOException e) {e.printStackTrace();}
		catch (JSchException e) {e.printStackTrace();}
		catch (ParseException e) {e.printStackTrace();}
	}

	private static void addFileInfo(String path, Date lastModifiedLocal, Date lastModifiedRemote)
	{
		FileInfo fileInfo = new FileInfo(path, lastModifiedLocal, lastModifiedRemote);
		fileList.add(fileInfo);
	}

	public static List<FileInfo> getFileList()
	{
		return fileList;
	}

	public static List<FileInfo> getFilesForPushAll()
	{
		List<FileInfo> files = new ArrayList<FileInfo>();
		for(FileInfo fileInfo : fileList)
		{
			Status status = fileInfo.getStatus();
			if(status == Status.Ahead || status == Status.Local)
				files.add(fileInfo);
		}
		return files;
	}


	public static List<FileInfo> getFilesForPullAll()
	{
		List<FileInfo> files = new ArrayList<FileInfo>();
		for(FileInfo fileInfo : fileList)
		{
			Status status = fileInfo.getStatus();
			if(status == Status.Behind || status == Status.New)
				files.add(fileInfo);
		}
		return files;
	}

	public static void saveNBTData()
	{	
		List<String> entityNames = getEntities();
		//GUI
		//writeNBTToFile(AnimationData.getGUISetupTag(entityNames), getGUIFile());
		//Entity data
		for(String entityName : entityNames)
		{
			//Parenting and part names
			if(AnimationData.getEntitySetupChanged(entityName))
				writeNBTToFile(AnimationData.getEntityDataTag(entityName), getEntityFile(entityName, "data"));
			//Sequences

//			List<String> changeSequences = AnimationData.getChangedSequences(entityName);
//			for(AnimationSequence s : AnimationData.getSequences(entityName))
//			{
//				if(changeSequences.contains(s.getName()))
//				{
//					System.out.println("Saving " + s.getName());
//					writeNBTToFile(s.getSaveData(), getAnimationFile(entityName, s.getName()));
//				}
//			}
//			AnimationData.clearChangedSequences(entityName);
		}
		AnimationData.clearEntitySetupChanged();
	}

	public static void loadNBTData()
	{			
		List<String> entityNames = getEntities();
		//GUI
		//		File guiDataFile = getGUIFile();
		//		if(guiDataFile.exists())
		//			AnimationData.loadGUISetup(getNBTFromFile(guiDataFile));


		//Entity data
		for(String entityName : entityNames)
		{
			loadEntityData(entityName);

			//Sequences
			for(String animationName : getEntityAnimations(entityName))
				loadEntityAnimation(entityName, animationName.substring(0, animationName.indexOf(".")));
		}
	}

	public static void loadEntityData(String entityName)
	{
		ClientProxy.renderObj.loadModel(entityName);
		File entityDataFile = getEntityFile(entityName, "data");
		if(entityDataFile.exists())
			AnimationData.loadEntityData(entityName, getNBTFromFile(entityDataFile));
	}

	public static void loadEntityAnimation(String entityName, String animationName)
	{
//		AnimationSequence sequence = new AnimationSequence(entityName, getNBTFromFile(getAnimationFile(entityName, animationName)));
//		AnimationData.addSequence(entityName, sequence);
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
		File sharedFolder = new File(sharedPath);
		if(!sharedFolder.exists())
			sharedFolder.mkdirs();
		for(File file : sharedFolder.listFiles())
		{
			if(file.isDirectory())
				entities.add(file.getName());
		}
		return entities;
	}

	private static Date getDateModifiedLocal(String path) 
	{
		Date lastModifiedLocal = null;
		if(path.contains("."))
		{
			//If path points to animation file, simply get last modified date of animation file, null if it doesn't exist.
			File localFile = new File(String.format("%s/%s", userPath, path));
			lastModifiedLocal = localFile.exists() ? new Date(localFile.lastModified()) : null;
		}
		else
		{
			//If path points to shared entity folder, get folder and then get the most recently modified file in it.
			File folder = new File(String.format("%s/%s", sharedPath, path.substring(path.lastIndexOf("/") + 1))); 
			if(folder.exists())
			{
				Date mostRecent = null;
				File[] files = folder.listFiles();
				if(files.length != 4)
					return null;
				for(File f : files)
				{
					Date lastModified = new Date(f.lastModified());
					if(mostRecent == null || lastModified.after(mostRecent))
						mostRecent = lastModified;
				}
				lastModifiedLocal = mostRecent;
			}
		}

		if(lastModifiedLocal != null)
		{
			long roundedTime = (long) (Math.floor(lastModifiedLocal.getTime()/1000)*1000);
			lastModifiedLocal = new Date(roundedTime);
		}

		return lastModifiedLocal;
	}

	private static File getGUIFile()
	{
		return new File(MCEA_Main.animationPath + "/data/GuiData.data");
	}
	
	public static void stopJobHandler()
	{
		if(jobHandler != null)
			jobHandler.dispose();
	}

	/**
	 * Get an entity file from the shared folder
	 * @param entityName - Name of entity.
	 * @param ext - File extension (data, pxy, png or obj). No dot!
	 * @return Entity file.
	 */
	public static File getEntityFile(String entityName, String ext)
	{
		return new File(String.format("%s/%s/%s.%s", sharedPath, entityName, entityName, ext));
	}

	public static ResourceLocation getEntityResourceLocation(String entityName)
	{
		return new ResourceLocation(String.format("animation:shared/%s/%s.png", entityName, entityName));
	}

	/**
	 * Get all the animations files for an entity.
	 * @param entityName - Name of entity.
	 * @return List of all animation files.
	 */
	private static List<String> getEntityAnimations(String entityName)
	{
		List<String> animationFiles = new ArrayList<String>();
		File animationFolder = new File(String.format("%s/%s", userPath, entityName));
		if(!animationFolder.exists())
			animationFolder.mkdirs();
		for(File f : animationFolder.listFiles())
			animationFiles.add(f.getName());
		return animationFiles;
	}

	/**
	 * Get the file for a single animation.
	 * @param entityName - Name of entity.
	 * @param animationName - Name of animation.
	 * @return Animation file.
	 */
	private static File getAnimationFile(String entityName, String animationName)
	{
		return new File(String.format("%s/%s/%s.mcea", userPath, entityName, animationName));
	}

	public static void writeUserData(String username, String password) throws IOException
	{
		File f = new File(MCEA_Main.animationPath + "/user.txt");
		if(!f.exists())
			f.createNewFile();
		FileWriter fileWriter = new FileWriter(f);
		fileWriter.write(String.format("Username: %s\r\nPassword: %s", username, password));
		fileWriter.close();
	}

	public static Boolean canLoginOffline(String username, String password)
	{
		File f = new File(MCEA_Main.animationPath + "/user.txt");
		if(!f.exists())
		{
			JOptionPane.showMessageDialog(null, "Offline login information not found. Please login with an internet connection.");
			return null;
		}
		String offlineUsername = getOfflineUsername();
		if(offlineUsername == null)
			return false;
		String offlinePassword = getOfflinePassword();
		if(offlinePassword == null)
			return false;
		return username.equals(offlineUsername) && password.equals(offlinePassword);
	}

	private static String getOfflineUsername()
	{
		try
		{
			File f = new File(MCEA_Main.animationPath + "/user.txt");
			FileReader fileReader = new FileReader(f);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.contains("Username:"))
				{
					reader.close();
					return line.substring(line.indexOf(":") + 2, line.length());
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static String getOfflinePassword()
	{
		try
		{
			File f = new File(MCEA_Main.animationPath + "/user.txt");
			FileReader fileReader = new FileReader(f);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.contains("Password:"))
				{
					reader.close();
					return line.substring(line.indexOf(":") + 2, line.length());
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void clearDataIfDifferentUser(String username)
	{
		File f = new File(MCEA_Main.animationPath + "/user.txt");
		if(f.exists())
		{
			String offlineUsername = getOfflineUsername();
			if(offlineUsername != null && username.equals(offlineUsername))
				return;
		}

		System.out.println("Remove");
		removeDirectory(new File(userPath));
		AnimationData.clear();
		DataHandler.loadNBTData();
	}

	private static boolean removeDirectory(File dir)
	{
		if(!dir.exists())
			return true;
		if(!dir.isDirectory())
			return dir.delete();
		File[] files = dir.listFiles();
		if(files.length == 0)
			return dir.delete();
		boolean allDeleted = true;
		for(File f : files)
		{
			if(f.isDirectory())
				removeDirectory(f);
			else if(!f.delete())
			{
				allDeleted = false;
				System.out.println("Failed to delete " + f.getAbsolutePath());
			}
		}
		return true;
	}

}