package com.dabigjoe.obsidianAPI.file;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHandler 
{

	public static final String obsidianModelExtension = "obm";
	public static final String tabulaModelExtension = "tbl";
	public static final String animationExtension = "oba";
	public static final String textureExtension = "png";
	public static final String qubbleModelExtension = "qbl";

	public static final String homePath = generateHomePath();
	public static final String animationPath = homePath + "mods/animation";
	public static final File tabulaModelsDir = new File(homePath + "mods/tabula/saves");
	public static final File modelFolder = new File(animationPath + "/models");
	public static final File binFolder = new File(animationPath + "/bin");

	private static String generateHomePath() {
		String mcDataDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
		if(mcDataDir.endsWith("."))
			mcDataDir = mcDataDir.substring(0, mcDataDir.length() - 1);
		if(!mcDataDir.endsWith("/"))
			mcDataDir += "/";
		return mcDataDir;
	}
	
	public static AnimationSequence getAnimationFromFile(File animationFile)
	{
		try 
		{
			return new AnimationSequence(CompressedStreamTools.readCompressed(new FileInputStream(animationFile)));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		return null;
	}

	public static void saveAnimationSequence(File animationFile, AnimationSequence sequence)
	{
		try 
		{
		    if(!animationFile.exists())
	            animationFile.createNewFile();
			CompressedStreamTools.writeCompressed(sequence.getSaveData(), new FileOutputStream(animationFile));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	public static void copyFileToPersistentMemory(ObsidianFile obsidianFile) {
		modelFolder.mkdirs();
		try 
		{
			File modelZipFile = new File(modelFolder, obsidianFile.getEntityName() + "." + obsidianModelExtension);
			if(!modelZipFile.exists())
				modelZipFile.createNewFile();
			FileUtils.addEntryToExistingZip(modelZipFile, ModelFileHandler.MODEL_NAME, obsidianFile.getModelStream());
			FileUtils.addEntryToExistingZip(modelZipFile, ModelFileHandler.TEXTURE_NAME, obsidianFile.getTextureStream());
		}
		catch (IOException e) {e.printStackTrace();}
	}

	public static void copyFileToPersistentMemory(File file)
	{			
		modelFolder.mkdirs();
		File copy = new File(modelFolder, file.getName());
		try 
		{
			if(copy.exists())
				copy.delete();
			org.apache.commons.io.FileUtils.copyFile(file, copy);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static void copyFileToPersistentMemory(String fileName, InputStream inputStream)
	{			
		modelFolder.mkdirs();
		File copy = new File(modelFolder, fileName);
		try 
		{
			if(copy.exists())
				copy.delete();
			FileOutputStream outputStream = new FileOutputStream(copy);
			IOUtils.copy(inputStream, outputStream);
		} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void copyFileToBin(String fileName, InputStream inputStream) {
		binFolder.mkdirs();
		File copy = new File(binFolder, fileName);
		try 
		{
			if(copy.exists())
				copy.delete();
			FileOutputStream outputStream = new FileOutputStream(copy);
			IOUtils.copy(inputStream, outputStream);
		} 
		catch (IOException e) {e.printStackTrace();}		
	}

	/**
	 * Generates a resource location for a png texture file that is in the external
	 * animation folder.
	 */
	public static ResourceLocation generateTextureResourceLocation(String entityName)
	{
		return new ResourceLocation(String.format("animation:bin/%s.png", entityName));
	}





}
