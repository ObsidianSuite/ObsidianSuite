package obsidianAnimations;

import java.util.Random;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAnimations.entity.EntityDummyPlayer;
import obsidianAnimator.ObsidianAnimator;

public class CommonProxy
{	
	public void init() 
	{	
		ModEntities.registerEntities();
		registerRendering();
	}

	public void registerRendering() {}

	public void registerAnimations()
	{
		AnimationRegistry.init();
		AnimationRegistry.registerEntity(EntityDummyPlayer.class, "dummy");
		AnimationRegistry.registerAnimation("dummy", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/dummy/WalkF.oba"));
	}


}



