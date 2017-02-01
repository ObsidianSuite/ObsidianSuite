package obsidianAnimations;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.AnimationRegistry;
import obsidianAnimator.render.entity.EntityObj;

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
		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));
	}
}



