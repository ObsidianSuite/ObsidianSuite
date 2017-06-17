package obsidianAnimations;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import obsidianAPI.file.importer.FileLoader;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.player.ModelAnimatedPlayer;
import obsidianAPI.render.player.RenderPlayerAnimated;
import obsidianAnimations.entity.EntityDummyPlayer;
import obsidianAnimations.entity.ModelDummyPlayer;
import obsidianAnimations.entity.RenderDummyPlayer;
import obsidianAnimations.entity.saiga.EntitySaiga;
import obsidianAnimations.entity.saiga.ModelSaiga;
import obsidianAnimations.entity.saiga.RenderSaiga;

public class ClientProxy extends CommonProxy
{	
	
	private ResourceLocation obsidianPlayerModelRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.obm");
	private ResourceLocation obsidianPlayerTextureRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.png");
	private ResourceLocation saigaModelRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.obm");
	private ResourceLocation saigaTextureRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.png");
	
	public void registerRendering()
	{
		RenderDummyPlayer dummyPlayerRenderer = new RenderDummyPlayer(FileLoader.loadModelFromResources("DummyPlayer", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelDummyPlayer.class));
		RenderPlayerAnimated playerRenderer = new RenderPlayerAnimated(FileLoader.loadModelFromResources("player", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelAnimatedPlayer.class));
		RenderSaiga saigaRenderer = new RenderSaiga(FileLoader.loadModelFromResources("saiga", saigaModelRL, saigaTextureRL, ModelSaiga.class));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, dummyPlayerRenderer);
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, playerRenderer);
		RenderingRegistry.registerEntityRenderingHandler(EntitySaiga.class, saigaRenderer);
	}
	
	public void registerAnimations()
	{
		AnimationRegistry.init();
		//		AnimationRegistry.registerEntity(EntityDummyPlayer.class, "dummy");
		//		AnimationRegistry.registerAnimation("dummy", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));


		//		if(swingTime - previousSwingTime > 0.05F) {
		//			if(entity.isSprinting()) {
		//				if(!entity.isCollidedVertically)
		//					state = "SprintJump";
		//				else
		//					state = "SprintF";
		//			}
		//			else if(entity.isSneaking())
		//				state = "CrouchF";
		//			else {
		//				if(!entity.isCollidedVertically)
		//					state = "RunJump";
		//				else
		//					state = "WalkF";
		//			}
		//		}
		//		else if(this.onGround != 0F){
		//			state = "Swing";
		//		}
		//		else if(!entity.isCollidedVertically) {
		//			state = "Jump";
		//		}
		//		else {
		//			if(entity.isSneaking())
		//				state = "CrouchedIdle";
		//			else
		//				state = "Idle";
		//		}	
		
		IsActiveFunction isWalking = (swingTime, swingMax, clock, lookX, lookY, f5, model, entity) -> { 
			return model.isMoving(entity, swingTime) && !entity.isSprinting() && !entity.isSneaking() && entity.isCollidedVertically;
		};
		IsActiveFunction isSprinting = (swingTime, swingMax, clock, lookX, lookY, f5, model, entity) -> { 
			return model.isMoving(entity, swingTime) && entity.isSprinting() && entity.isCollidedVertically;
		};
		IsActiveFunction isSneaking = (swingTime, swingMax, clock, lookX, lookY, f5, model, entity) -> { 
			return model.isMoving(entity, swingTime) && entity.isSneaking() && entity.isCollidedVertically;
		};
		IsActiveFunction isJumping = (swingTime, swingMax, clock, lookX, lookY, f5, model, entity) -> { 
			return !model.isMoving(entity, swingTime) && !entity.isCollidedVertically;
		};
		IsActiveFunction returnTrue = (swingTime, swingMax, clock, lookX, lookY, f5, model, entity) -> { 
			return true;
		};

		AnimationRegistry.registerEntity(EntityPlayer.class, "player");
		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"), 0, true, isWalking);
		AnimationRegistry.registerAnimation("player", "SprintF", new ResourceLocation("mod_obsidian_animations:animations/player/SprintF.oba"), 1, true, isSprinting);
		AnimationRegistry.registerAnimation("player", "CrouchF", new ResourceLocation("mod_obsidian_animations:animations/player/CrouchF.oba"), 2, true, isSneaking);	
		AnimationRegistry.registerAnimation("player", "Jump", new ResourceLocation("mod_obsidian_animations:animations/player/Jump.oba"), 3, false, 0.0F, isJumping);	
		//AnimationRegistry.registerAnimation("player", "MovementTest", new ResourceLocation("mod_obsidian_animations:animations/player/MovementTest.oba"), 4, returnTrue);	
		//AnimationRegistry.registerAnimation("player", "Idle", new ResourceLocation("mod_obsidian_animations:animations/player/Idle.oba"), 1, returnTrue);
		
		AnimationRegistry.registerEntity(EntitySaiga.class, "saiga");
		AnimationRegistry.registerAnimation("saiga", "Walk", new ResourceLocation("mod_obsidian_animations:animations/saiga/Walk.oba"), 0, true, isWalking);
		AnimationRegistry.registerAnimation("saiga", "Idle", new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaIdle.oba"), 100, true, returnTrue);

	}
}

