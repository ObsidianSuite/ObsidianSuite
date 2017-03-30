package obsidianAnimator.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.render.entity.ModelObj_Animator;
import obsidianAnimator.render.entity.RenderObj_Animator;

public class ModelHandler 
{

	private static Map<String, ModelObj_Animator> models = new HashMap<String, ModelObj_Animator>();

	public static RenderObj_Animator modelRenderer = new RenderObj_Animator();

	public static String importModel(File modelFile, File textureFile)
	{
		copyFileToPersistentMemory(modelFile);
		copyFileToPersistentMemory(textureFile);
		ModelObj_Animator model = loadModel(modelFile);
		updateRenderer(model.entityName);
		return model.entityName;
	}

	public static void loadFileFromPersistence(File file)
	{
		loadModel(file);
	}

	private static ModelObj_Animator loadModel(File modelFile)
	{
		String fileName = modelFile.getName();
		String entityName = fileName.substring(0,fileName.indexOf("."));
		ModelObj_Animator model = new ModelObj_Animator(entityName, modelFile, generateTextureResourceLocation(entityName));
		models.put(model.entityName, model);
		return model;
	}

	private static ResourceLocation generateTextureResourceLocation(String entityName)
	{
		return new ResourceLocation(String.format("animation:models/%s.png", entityName));
	}

	public static void updateRenderer(String entityName)
	{
		modelRenderer.setModel(models.get(entityName));
	}

	public static boolean isModelImported(String entityName)
	{
		return models.containsKey(entityName);
	}

	public static ModelObj_Animator getModel(String entityName)
	{
		return models.get(entityName);
	}

	public static Set<String> getModelList()
	{
		return models.keySet();
	}

	private static void copyFileToPersistentMemory(File file)
	{			
		File copy = new File(Persistence.modelFolder, file.getName());
		try 
		{
			if(copy.exists())
				copy.delete();
			FileUtils.copyFile(file, copy);
		} 
		catch (IOException e) {e.printStackTrace();}
	}

	public static void saveFiles()
	{
		for(String s : models.keySet())
			makeModelFile(s);
	}

	private static void makeModelFile(String entityName)
	{
		File f = new File(Persistence.modelFolder, entityName + "." + FileHandler.modelExtension);
		String textAfterNBT = getTextAfterNBT(f);

		try 
		{
			CompressedStreamTools.write(ModelHandler.getModel(entityName).createNBTTag(), f);
			FileWriter fw = new FileWriter(f, true);
			fw.write(textAfterNBT);
			fw.close();
		} 
		catch (IOException e) {e.printStackTrace();}
	}

	private static String getTextAfterNBT(File f)
	{
		String text = "\n";

		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(f));

			boolean nbtFinished = false;
			String currentLine;
			while((currentLine = reader.readLine()) != null)
			{
				if(currentLine.contains("# Model #"))
					nbtFinished = true;

				if(nbtFinished)
					text += currentLine + "\n";
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}

		return text;
	}

}
