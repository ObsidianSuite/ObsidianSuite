package obsidianAnimator.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.file.importer.FileLoader;
import obsidianAnimator.render.entity.ModelObj_Animator;
import obsidianAnimator.render.entity.RenderObj_Animator;

public class ModelHandler 
{

	private static Map<String, ModelObj_Animator> models = new HashMap<String, ModelObj_Animator>();

	public static RenderObj_Animator modelRenderer = new RenderObj_Animator();

	public static void addModel(ModelObj_Animator model)
	{
		models.put(model.entityName, model);
		updateRenderer(model.entityName);
	}

	public static void loadModelFromResource(String entityName)
	{
		ResourceLocation modelResource = generateInternalModelResourceLocation(entityName);
		ModelObj_Animator model = FileLoader.loadModelFromResources(entityName, modelResource, null, ModelObj_Animator.class);
		if(model != null) {
			addModel(model);
			Minecraft.getMinecraft().refreshResources();
		}
	}

	/**
	 * Generates a resource location for an obm model file that is in the internal
	 * assets folder, i.e. within in jar. 
	 */
	private static ResourceLocation generateInternalModelResourceLocation(String entityName)
	{
		return new ResourceLocation(String.format("mod_obsidian_animator:models/%s.obm", entityName));
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

}
