package obsidianAnimator;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import obsidianAnimator.render.objRendering.EntityObj;

public class CommonProxy
{	
	public void init() 
	{	
		EntityRegistry.registerGlobalEntityID(EntityObj.class, "Obj", EntityRegistry.findGlobalUniqueEntityId(), 0, 0);
		registerRendering();
	}

	public void registerRendering() {}


	public void registerBlocks()
	{
		GameRegistry.registerBlock(ObsidianAnimator.Base, "Base");
		GameRegistry.registerBlock(ObsidianAnimator.Grid, "Grid");
	}
	
	public void registerItems(){}
}



