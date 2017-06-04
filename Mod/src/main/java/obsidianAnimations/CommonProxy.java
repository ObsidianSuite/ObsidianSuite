package obsidianAnimations;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAnimations.entity.EntityDummyPlayer;

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
//		AnimationRegistry.registerEntity(EntityDummyPlayer.class, "dummy");
//		AnimationRegistry.registerAnimation("dummy", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));
		
		AnimationRegistry.registerEntity(EntityPlayer.class, "player");
		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));
		AnimationRegistry.registerAnimation("player", "SprintF", new ResourceLocation("mod_obsidian_animations:animations/player/SprintF.oba"));	
//		AnimationRegistry.registerAnimation("player", "Idle", new ResourceLocation("mod_obsidian_animations:animations/player/Idle.oba"));
	}


}



