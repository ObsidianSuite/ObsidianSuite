package obsidianAPI.render;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ModelAnimated extends ModelObj
{

	public ModelAnimated(String entityName, ResourceLocation modelLocation, ResourceLocation textureLocation) throws IOException
	{			
		super(entityName, Minecraft.getMinecraft().getResourceManager().getResource(modelLocation).getInputStream(), textureLocation);
	}

}
