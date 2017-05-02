package obsidianAnimator.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import obsidianAPI.exceptions.MissingImporterException;
import obsidianAPI.file.FileHandler;
import obsidianAPI.file.ModelFileHandler;
import obsidianAPI.file.importer.FileLoader;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class Persistence 
{

	private static final File saveFile = new File(FileHandler.animationPath, "save.data");

	private static FilenameFilter modelFileFilter = new FilenameFilter() 
	{
		public boolean accept(File dir, String name) 
		{
			return name.toLowerCase().endsWith("." + FileHandler.obsidianModelExtension);
		}
	};

	public static void save()
	{
		for(String entityName : ModelHandler.getModelList()) {
			File f = new File(FileHandler.modelFolder, entityName + "." + FileHandler.obsidianModelExtension);
			if(f.exists())
				ModelFileHandler.saveModelDataToObsidianFile(ModelHandler.getModel(entityName), f);
		}

		NBTTagCompound nbt = new NBTTagCompound();
		if(FileChooser.lastModelDirectory != null)
			nbt.setString("lastModelDir", FileChooser.lastModelDirectory.getAbsolutePath());
		if(FileChooser.lastAnimationDirectory != null)
			nbt.setString("lastAnimationDir", FileChooser.lastAnimationDirectory.getAbsolutePath());
		writeNBTToFile(nbt, saveFile);
	}

	public static void load()
	{
		loadModels();

		if(saveFile.exists())
		{
			NBTTagCompound nbt = getNBTFromFile(saveFile);
			if(nbt.hasKey("lastModelDir"))
				FileChooser.lastModelDirectory = new File(nbt.getString("lastModelDir"));
			if(nbt.hasKey("lastAnimationDir"))
				FileChooser.lastAnimationDirectory = new File(nbt.getString("lastAnimationDir"));
		}
	}

	/**
	 * Load cached models
	 */
	private static void loadModels()
	{
		if(FileHandler.modelFolder.exists())
		{
			for(File f : FileHandler.modelFolder.listFiles(modelFileFilter)) {
				ModelObj_Animator model = FileLoader.fromFile(f, ModelObj_Animator.class);
				ModelHandler.addModel(model);
			}
		}
		else
			FileHandler.modelFolder.mkdirs();
	}


	/**
	 * Write an NBTTagCompound to a file.
	 */
	public static void writeNBTToFile(NBTTagCompound nbt, File file)
	{
		try 
		{
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
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
