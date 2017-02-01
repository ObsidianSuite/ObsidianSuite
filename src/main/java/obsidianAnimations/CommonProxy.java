package obsidianAnimations;

import java.util.Random;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAnimator.ObsidianAnimator;

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



