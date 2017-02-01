package obsidianAnimations;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.registry.AnimationRegistry;

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



