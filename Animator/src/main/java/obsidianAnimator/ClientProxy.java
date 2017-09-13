package obsidianAnimator;

import java.util.List;

import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import obsidianAnimator.render.AnimationResourcePack;

public class ClientProxy extends CommonProxy
{	
	
	public void registerRendering()
	{
		//Entities
		ModEntities.initModels();
        
        //Register animator resource pack, where models and textures are stored, externally from the jar
        List<IResourcePack> resourcePackList = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
        resourcePackList.add(new AnimationResourcePack());
	}
}

