package obsidianAnimations;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import obsidianAPI.registry.AnimationRegistry;

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
		
		
		IsActiveFunction returnTrue = (swingTime, swingMax, clock, lookX, lookY, f5, entity) -> { return true; };
		
		AnimationRegistry.registerEntity(EntityPlayer.class, "player");
		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"), 6, returnTrue);
		AnimationRegistry.registerAnimation("player", "SprintF", new ResourceLocation("mod_obsidian_animations:animations/player/SprintF.oba"), 5, returnTrue);
		AnimationRegistry.registerAnimation("player", "CrouchF", new ResourceLocation("mod_obsidian_animations:animations/player/CrouchF.oba"), 4, returnTrue);	
		AnimationRegistry.registerAnimation("player", "Jump", new ResourceLocation("mod_obsidian_animations:animations/player/Jump.oba"), 3, returnTrue);	
		AnimationRegistry.registerAnimation("player", "MovementTest", new ResourceLocation("mod_obsidian_animations:animations/player/MovementTest.oba"), 2, returnTrue);	
		AnimationRegistry.registerAnimation("player", "Idle", new ResourceLocation("mod_obsidian_animations:animations/player/Idle.oba"), 1, returnTrue);
	}


}



