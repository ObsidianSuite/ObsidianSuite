package obsidianAnimator;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.ResourceLocation;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.render.entity.EntityObj;

public class CommonProxy
{	
	public void init() 
	{	
		EntityRegistry.registerGlobalEntityID(EntityObj.class, "Obj", EntityRegistry.findGlobalUniqueEntityId());
		registerRendering();
		registerModels();
	}

	public void registerModels() 
	{
		ModelHandler.loadModelFromResource("player");
	}

	public void registerRendering() {}


	public void registerBlocks()
	{
		GameRegistry.registerBlock(ObsidianAnimator.Base, "Base");
		GameRegistry.registerBlock(ObsidianAnimator.Grid, "Grid");
	}
	
	public void registerItems(){}
}



