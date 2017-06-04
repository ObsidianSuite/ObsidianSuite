package obsidianAPI.file.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.file.FileHandler;
import obsidianAPI.file.ModelFileHandler;
import obsidianAPI.file.ObsidianFile;
import obsidianAPI.render.ModelObj;

public class FileLoader {

	public static <T extends ModelObj> T fromFile(ObsidianFile file, Class<T> clazz)
	{
		T model = null;

		try
		{
			String entityName = file.getEntityName();
						
			WavefrontObject obj = readObj(entityName, file.getModelStream());
			ResourceLocation texture = readTexture(entityName, file.getTextureStream());

			//Create model instance of specific class
			Constructor<T> ctor = clazz.getConstructor(String.class, WavefrontObject.class, ResourceLocation.class);
			model = (T) ctor.newInstance(new Object[] {entityName, obj, texture});
		}
		catch (Exception e1)
		{
			System.err.println("Failed to load from file: " + file.getEntityName());
			e1.printStackTrace();
		}
		return model;
	}
	
	/**
	 * Create a model instance of a specific class from an Obsidian Model file.
	 * @param file File to create from
	 * @param clazz Class to create, must extend ModelObj. 
	 * 	Must also have constructor String.class, WavefrontObject.class, ResourceLocation.class
	 * @return Model instance of class clazz.
	 */
	public static <T extends ModelObj> T fromFile(File file, Class<T> clazz)
	{
		T model = null;

		try
		{
			String entityName = file.getName().substring(0,file.getName().indexOf("."));
			
			ZipFile zipFile = new ZipFile(file);

			ZipEntry modelEntry, textureEntry, dataEntry;
			modelEntry = zipFile.getEntry(ModelFileHandler.MODEL_NAME);
			textureEntry = zipFile.getEntry(ModelFileHandler.TEXTURE_NAME);
			dataEntry = zipFile.getEntry(ModelFileHandler.SETUP_NAME);
			
			WavefrontObject obj = readObj(entityName, zipFile.getInputStream(modelEntry));
			ResourceLocation texture = readTexture(entityName, zipFile.getInputStream(textureEntry));

			//Create model instance of specific class
			Constructor<T> ctor = clazz.getConstructor(String.class, WavefrontObject.class, ResourceLocation.class);
			model = (T) ctor.newInstance(new Object[] {entityName, obj, texture});
			
			if(dataEntry != null)
				model.loadSetup(zipFile.getInputStream(dataEntry));
						
			zipFile.close();
		}
		catch (Exception e1)
		{
			System.err.println("Failed to load from file: " + file.getName());
			e1.printStackTrace();
		}
		return model;
	}
	
	public static <T extends ModelObj> T loadModelFromResource(String entityName, ResourceLocation resLoc, Class<T> clazz)
	{
		T model = null;
		
		try {
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resLoc);
			
			File tmpFile = new File(entityName + ".obm");
			InputStream is = res.getInputStream();
			OutputStream os = new FileOutputStream(tmpFile);
			IOUtils.copy(is, os);
			is.close();
			os.close();
			model = FileLoader.fromFile(tmpFile, clazz);
			tmpFile.delete();
		} catch (IOException e) {
			System.out.println("Could not load " + entityName + " model from resource");
			e.printStackTrace();
		}

		return model;
	}
	
	private static WavefrontObject readObj(String entityName, InputStream inputStream)
	{
		return new WavefrontObject(entityName, inputStream);
	}
	
	private static ResourceLocation readTexture(String entityName, InputStream inputStream)
	{
		FileHandler.copyFileToBin(entityName + ".png", inputStream);
		return FileHandler.generateTextureResourceLocation(entityName);
	}
	
}
