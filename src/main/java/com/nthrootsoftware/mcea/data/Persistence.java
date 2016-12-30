package com.nthrootsoftware.mcea.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nthrootsoftware.mcea.MCEA_Main;
import com.nthrootsoftware.mcea.file.FileHandler;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class Persistence 
{

	public static final File saveFile = new File(MCEA_Main.animationPath, "save.data");
	public static final File modelFolder = new File(MCEA_Main.animationPath + "/models");

	public static void save()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		if(FileHandler.lastModelDirectory != null)
			nbt.setString("lastModelDir", FileHandler.lastModelDirectory.getAbsolutePath());
		if(FileHandler.lastAnimationDirectory != null)
			nbt.setString("lastAnimationDir", FileHandler.lastAnimationDirectory.getAbsolutePath());
		writeNBTToFile(nbt, saveFile);
	}

	public static void load()
	{
		loadModels();

		NBTTagCompound nbt = getNBTFromFile(saveFile);
		if(saveFile.exists())
		{
			if(nbt.hasKey("lastModelDir"))
				FileHandler.lastModelDirectory = new File(nbt.getString("lastModelDir"));
			if(nbt.hasKey("lastAnimationDir"))
				FileHandler.lastAnimationDirectory = new File(nbt.getString("lastAnimationDir"));
		}
	}

	private static void loadModels()
	{
		if(modelFolder.exists())
		{
			for(File f : modelFolder.listFiles())
				ModelHandler.loadFileFromPersistence(f);
		}
		else
			modelFolder.mkdirs();
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

}
