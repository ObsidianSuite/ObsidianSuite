package com.nthrootsoftware.mcea.distribution;


import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser
{
	
	private static final FileNameExtensionFilter modelFilter = new FileNameExtensionFilter("MCEA Models", ".obj");
	private static final FileNameExtensionFilter entityFilter = new FileNameExtensionFilter("MCEA Entities", ".mce");
	private static final FileNameExtensionFilter animationFilter = new FileNameExtensionFilter("MCEA Animations", ".mca");

	private static File lastModelDirectory;
	private static File lastEntityDirectory;
	private static File lastAnimationDirectory;

	private static JFileChooser fc = new JFileChooser();

	public static File loadModelFile(Component parent)
	{
		File modelFile = getLoadLocationFromUser(parent, lastModelDirectory, modelFilter, JFileChooser.FILES_ONLY);
		lastModelDirectory = fc.getCurrentDirectory();
		return modelFile;
	}
	
	public static File loadEntityFile(Component parent)
	{
		File entityFile = getLoadLocationFromUser(parent, lastEntityDirectory, entityFilter, JFileChooser.FILES_ONLY);
		lastEntityDirectory = fc.getCurrentDirectory();
		return entityFile;
	}
	
	public static File loadAnimationFile(Component parent)
	{
		File animationFile = getLoadLocationFromUser(parent, lastAnimationDirectory, animationFilter, JFileChooser.FILES_ONLY);
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFile;
	}
	
	public static File chooseAnimationFolder(Component parentComponent)
	{
		File animationFolder = getLoadLocationFromUser(parentComponent, lastAnimationDirectory, null, JFileChooser.DIRECTORIES_ONLY);
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFolder;
	}
	
	private static File getLoadLocationFromUser(Component parentComponent, File parentDirectory, FileNameExtensionFilter fileExtensionFilter, int fileSelectionMode)
	{
		File file = null;
		
		if(parentDirectory != null)
			fc.setCurrentDirectory(parentDirectory);
		
		fc.setFileFilter(fileExtensionFilter);
		fc.setFileSelectionMode(fileSelectionMode);
		
		int returnVal = fc.showOpenDialog(parentComponent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			file = fc.getSelectedFile();
		
		return file;
	}

	private static File getSaveLocationFromUser(Component parent, File parentDirectory, FileNameExtensionFilter fileExtensionFilter)
	{
		File animationFile = null;
		
		if(lastAnimationDirectory != null)
			fc.setCurrentDirectory(lastAnimationDirectory);
		
		int returnVal = fc.showSaveDialog(parent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			animationFile = fc.getSelectedFile();
		
		if(animationFile != null)
			lastAnimationDirectory = animationFile.getParentFile();
		else
			lastAnimationDirectory = fc.getCurrentDirectory();
		
		return animationFile;
	}
	
}

