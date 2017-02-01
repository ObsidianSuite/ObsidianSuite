package obsidianAnimations;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import obsidianAPI.AnimationRegistry;
import obsidianAnimator.render.objRendering.EntityObj;

public class CommonProxy
{	
	public void init() 
	{	
		registerRendering();
	}

	public void registerRendering() {}
	
	public void registerAnimations()
	{
		AnimationRegistry.registerEntity("player");
	}
}



