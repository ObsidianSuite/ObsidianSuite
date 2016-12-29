package com.nthrootsoftware.mcea.distribution;


import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser
{

	private static JFileChooser fc = new JFileChooser();

	public static File loadModelFile(Component parent)
	{
		File modelFile = getLoadLocationFromUser(parent, FileHandler.lastModelDirectory, FileHandler.modelFilter, JFileChooser.FILES_ONLY);
		FileHandler.lastModelDirectory = fc.getCurrentDirectory();
		return modelFile;
	}
	
	public static File loadEntityFile(Component parent)
	{
		File entityFile = getLoadLocationFromUser(parent, FileHandler.lastEntityDirectory, FileHandler.entityFilter, JFileChooser.FILES_ONLY);
		FileHandler.lastEntityDirectory = fc.getCurrentDirectory();
		return entityFile;
	}
	
	public static File loadAnimationFile(Component parent)
	{
		File animationFile = getLoadLocationFromUser(parent, FileHandler.lastAnimationDirectory, FileHandler.animationFilter, JFileChooser.FILES_ONLY);
		FileHandler.lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFile;
	}
	
	public static File chooseAnimationFolder(Component parentComponent)
	{
		File animationFolder = getLoadLocationFromUser(parentComponent, FileHandler.lastAnimationDirectory, null, JFileChooser.DIRECTORIES_ONLY);
		FileHandler.lastAnimationDirectory = fc.getCurrentDirectory();
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
		
		if(FileHandler.lastAnimationDirectory != null)
			fc.setCurrentDirectory(FileHandler.lastAnimationDirectory);
		
		int returnVal = fc.showSaveDialog(parent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			animationFile = fc.getSelectedFile();
		
		if(animationFile != null)
			FileHandler.lastAnimationDirectory = animationFile.getParentFile();
		else
			FileHandler.lastAnimationDirectory = fc.getCurrentDirectory();
		
		return animationFile;
	}
	
}

