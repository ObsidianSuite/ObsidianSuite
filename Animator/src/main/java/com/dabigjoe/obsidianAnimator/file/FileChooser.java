package com.dabigjoe.obsidianAnimator.file;


import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dabigjoe.obsidianAPI.file.FileHandler;

import java.awt.Component;
import java.io.File;

public class FileChooser
{

	public static final FileNameExtensionFilter allModelFilter = new FileNameExtensionFilter("All Models", FileHandler.obsidianModelExtension, FileHandler.tabulaModelExtension, FileHandler.qubbleModelExtension);
	public static final FileNameExtensionFilter obsidianModelFilter = new FileNameExtensionFilter("Obsidian Models", FileHandler.obsidianModelExtension);
	public static final FileNameExtensionFilter animationFilter = new FileNameExtensionFilter("Obsidian Animations", FileHandler.animationExtension);
	public static final FileNameExtensionFilter textureFilter = new FileNameExtensionFilter("PNG", FileHandler.textureExtension);
	
	public static File lastModelDirectory;
	public static File lastAnimationDirectory;
	
	private static JFileChooser fc = new JFileChooser();

//	public static File loadModelFile(Component parent) throws FileNotChosenException
//	{
//		File modelFile = getLoadLocationFromUser(parent, FileHandler.lastModelDirectory, FileHandler.modelFilter, JFileChooser.FILES_ONLY);
//		FileHandler.lastModelDirectory = fc.getCurrentDirectory();
//		return modelFile;
//	}
	
	public static File loadAnimationFile(Component parent) throws FileNotChosenException
	{
		File animationFile = getFile(parent, lastAnimationDirectory, animationFilter, JFileChooser.FILES_ONLY, false, null);
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFile;
	}
	
	public static File loadImportFile(Component parent, FileNameExtensionFilter filter) throws FileNotChosenException
	{
		File modelFile = getFile(parent, lastModelDirectory, filter, JFileChooser.FILES_ONLY, false, null);
		lastModelDirectory = fc.getCurrentDirectory();
		return modelFile;
	}
	
	public static File getAnimationSaveLocation(Component parentComponent, String suggestedFileName) throws FileNotChosenException
	{
		File animationFile = getFile(parentComponent, lastAnimationDirectory, animationFilter, JFileChooser.FILES_ONLY, true, suggestedFileName);
		if(animationFile == null)
			throw new FileNotChosenException();
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFile;
	}
	
	public static File getModelSaveLocation(Component parentComponent, String suggestedFileName) throws FileNotChosenException
	{
		File modelFile = getFile(parentComponent, lastModelDirectory, obsidianModelFilter, JFileChooser.FILES_ONLY, true, suggestedFileName);
		if(modelFile == null)
			throw new FileNotChosenException();
		lastModelDirectory = fc.getCurrentDirectory();
		return modelFile;
	}
	
	private static File getFile(Component parentComponent, File parentDirectory, FileNameExtensionFilter fileExtensionFilter, int fileSelectionMode, boolean saveDialog, String suggestedFileName) throws FileNotChosenException
	{
		File file = null;
		
		if(parentDirectory != null)
			fc.setCurrentDirectory(parentDirectory);

		fc.setSelectedFile(suggestedFileName != null ? new File(suggestedFileName) : new File(""));			
		fc.setFileFilter(fileExtensionFilter);
		fc.setFileSelectionMode(fileSelectionMode);
		
		int returnVal;
		if(saveDialog)
			returnVal = fc.showSaveDialog(parentComponent);
		else
			returnVal = fc.showOpenDialog(parentComponent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			file = fc.getSelectedFile();
		else
			throw new FileNotChosenException();
		
		return file;
	}
	
}

