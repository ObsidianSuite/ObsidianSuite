package com.nthrootsoftware.mcea.distribution;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.nthrootsoftware.mcea.animation.AnimationSequence;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class FileHandler 
{

	public static final String modelExtension = "mcm";
	public static final String entityExtension = "mce";
	public static final String animationExtension = "mca";
	
	public static final FileNameExtensionFilter modelFilter = new FileNameExtensionFilter("MCEA Models", modelExtension);
	public static final FileNameExtensionFilter entityFilter = new FileNameExtensionFilter("MCEA Entities", entityExtension);
	public static final FileNameExtensionFilter animationFilter = new FileNameExtensionFilter("MCEA Animations", animationExtension);

	public static File lastModelDirectory;
	public static File lastEntityDirectory;
	public static File lastAnimationDirectory;
	
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
			CompressedStreamTools.writeCompressed(sequence.getSaveData(), new FileOutputStream(animationFile));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static File generateAnimationFile(File animationFolder, String animationName)
	{
		return new File(animationFolder, animationName + "." + animationExtension);
	}
	
	public static boolean animationFileExists(File animationFile)
	{
		return animationFile.exists();
	}
}
