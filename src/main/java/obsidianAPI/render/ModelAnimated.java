package obsidianAPI.render;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelAnimated extends ModelObj
{

	public ModelAnimated(String entityName, ResourceLocation modelLocation, ResourceLocation textureLocation) throws IOException
	{			
		super(entityName, Minecraft.getMinecraft().getResourceManager().getResource(modelLocation).getInputStream(), textureLocation);
	}
	
	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) 
	{				
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
