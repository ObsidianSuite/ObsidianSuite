package obsidianAnimator.file;


import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import obsidianAPI.file.FileHandler;

public class FileChooser
{

	public static final FileNameExtensionFilter allModelFilter = new FileNameExtensionFilter("All Models", FileHandler.obsidianModelExtension, FileHandler.tabulaModelExtension);
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
		File animationFile = getLoadLocationFromUser(parent, lastAnimationDirectory, animationFilter, JFileChooser.FILES_ONLY);
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFile;
	}
	
	public static File loadImportFile(Component parent, FileNameExtensionFilter filter) throws FileNotChosenException
	{
		File modelFile = getLoadLocationFromUser(parent, lastModelDirectory, filter, JFileChooser.FILES_ONLY);
		lastModelDirectory = fc.getCurrentDirectory();
		return modelFile;
	}
	
	public static File chooseAnimationFolder(Component parentComponent) throws FileNotChosenException
	{
		File animationFolder = getLoadLocationFromUser(parentComponent, lastAnimationDirectory, null, JFileChooser.DIRECTORIES_ONLY);
		lastAnimationDirectory = fc.getCurrentDirectory();
		return animationFolder;
	}
	
	private static File getLoadLocationFromUser(Component parentComponent, File parentDirectory, FileNameExtensionFilter fileExtensionFilter, int fileSelectionMode) throws FileNotChosenException
	{
		File file = null;
		
		if(parentDirectory != null)
			fc.setCurrentDirectory(parentDirectory);
		
		fc.setFileFilter(fileExtensionFilter);
		fc.setFileSelectionMode(fileSelectionMode);
		
		int returnVal = fc.showOpenDialog(parentComponent);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			file = fc.getSelectedFile();
		else
			throw new FileNotChosenException();
		
		return file;
	}
	
}

