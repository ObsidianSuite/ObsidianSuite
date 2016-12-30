package com.nthrootsoftware.mcea.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.nthrootsoftware.mcea.render.objRendering.ModelObj;
import com.nthrootsoftware.mcea.render.objRendering.RenderObj;

public class ModelHandler 
{

	private static Map<String, ModelObj> models = new HashMap<String, ModelObj>();

	public static RenderObj modelRenderer = new RenderObj();
	
	public static String importModelFile(File file)
	{
		copyFileToPersistentMemory(file);
		String fileName = file.getName();
		String entityName = fileName.substring(0,fileName.indexOf("."));
		ModelObj model = new ModelObj(entityName, file);
		importModel(model);
		updateRenderer(entityName);
		return entityName;
	}
	
	private static void importModel(ModelObj model)
	{
		models.put(model.entityName, model);
	}

	private static void updateRenderer(String entityName)
	{
		modelRenderer.setModel(models.get(entityName));
	}

	public static ModelObj getModel(String entityName)
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
			FileUtils.copyFile(file, copy);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
}
