package com.nthrootsoftware.mcea.distribution;

import java.util.HashMap;
import java.util.Map;

import com.nthrootsoftware.mcea.render.objRendering.ModelObj;
import com.nthrootsoftware.mcea.render.objRendering.RenderObj;

public class ModelHandler 
{

	private static Map<String, ModelObj> models = new HashMap<String, ModelObj>();

	public static RenderObj modelRenderer = new RenderObj();
	
	public static void importModel(ModelObj model)
	{
		models.put(model.entityName, model);
	}

	public static void updateRenderer(String entityName)
	{
		modelRenderer.setModel(models.get(entityName));
	}

	public static ModelObj getModel(String entityName)
	{
		return models.get(entityName);
	}
	
}
