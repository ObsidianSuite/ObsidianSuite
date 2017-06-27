package obsidianAnimations;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.ObsidianAPIUtil;
import obsidianAPI.animation.wrapper.AIAnimationWrapper;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAnimations.entity.ai.EntityAIEat;
import obsidianAnimations.entity.ai.EntityAIPanicAnimation;
import obsidianAnimations.entity.saiga.EntitySaiga;
import obsidianAnimations.fnaf.EntityFreddy;

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

		IsActiveFunction isWalking = (entity) -> { 
			return ObsidianAPIUtil.isEntityMoving(entity) && !entity.isSprinting() && !entity.isSneaking() && entity.onGround;
		};
		IsActiveFunction isSprinting = (entity) -> { 
			return ObsidianAPIUtil.isEntityMoving(entity) && entity.isSprinting() && entity.onGround;
		};
		IsActiveFunction isSneaking = (entity) -> { 
			return ObsidianAPIUtil.isEntityMoving(entity) && entity.isSneaking() && entity.onGround;
		};
		IsActiveFunction isJumping = (entity) -> { 
			return !ObsidianAPIUtil.isEntityMoving(entity) && !entity.onGround;
		};
		IsActiveFunction returnTrue = (entity) -> { 
			return true;
		};

		AnimationRegistry.registerEntity(EntityPlayer.class, "player");
		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"), 0, true, isWalking);
		AnimationRegistry.registerAnimation("player", "SprintF", new ResourceLocation("mod_obsidian_animations:animations/player/SprintF.oba"), 1, true, isSprinting);
		AnimationRegistry.registerAnimation("player", "CrouchF", new ResourceLocation("mod_obsidian_animations:animations/player/CrouchF.oba"), 2, true, isSneaking);	
		AnimationRegistry.registerAnimation("player", "Jump", new ResourceLocation("mod_obsidian_animations:animations/player/Jump.oba"), 3, false, 0.0F, isJumping);	
		//AnimationRegistry.registerAnimation("player", "MovementTest", new ResourceLocation("mod_obsidian_animations:animations/player/MovementTest.oba"), 4, true, returnTrue);	
		//AnimationRegistry.registerAnimation("player", "Idle", new ResourceLocation("mod_obsidian_animations:animations/player/Idle.oba"), 1, returnTrue);

		AnimationRegistry.registerEntity(EntitySaiga.class, "saiga");
		AnimationRegistry.registerAnimation("saiga", "Walk", new ResourceLocation("mod_obsidian_animations:animations/saiga/Walk.oba"), 10, true, isWalking);
		AnimationRegistry.registerAnimation("saiga", "Eat", new AIAnimationWrapper(EntityAIEat.name, new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaEat.oba"), 50, true, 0.5F));
		AnimationRegistry.registerAnimation("saiga", "Panic", new AIAnimationWrapper(EntityAIPanicAnimation.name, new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaRun.oba"), 0, true));
		AnimationRegistry.registerAnimation("saiga", "Idle", new ResourceLocation("mod_obsidian_animations:animations/saiga/SaigaIdle.oba"), 100, true, returnTrue);

		IsActiveFunction isIdleStage = (entity) -> {
			String activeAnimation = EntityAnimationProperties.get(entity).getActiveAnimation();
			return isDayTime(entity.worldObj.getWorldTime()) && activeAnimation != null && ((activeAnimation.equals("Idle") && entity.getRNG().nextFloat() > 0.99F) || activeAnimation.equals("Stage"));
		};

		IsActiveFunction isIdleShaking = (entity) -> {
			String activeAnimation = EntityAnimationProperties.get(entity).getActiveAnimation();
			return !isDayTime(entity.worldObj.getWorldTime()) && activeAnimation != null && ((activeAnimation.equals("Idle") && entity.getRNG().nextFloat() > 0.99F) || activeAnimation.equals("IdleShake"));
		};

		IsActiveFunction isIdleStare = (entity) -> {
			String activeAnimation = EntityAnimationProperties.get(entity).getActiveAnimation();
			return !isDayTime(entity.worldObj.getWorldTime()) && activeAnimation != null && ((activeAnimation.equals("Idle") && entity.getRNG().nextFloat() > 0.99F) || activeAnimation.equals("IdleStare"));
		};

		AnimationRegistry.registerEntity(EntityFreddy.class, "Freddy");
		AnimationRegistry.registerAnimation("Freddy", "Idle", new ResourceLocation("fnafmod:obsidian/animations/Idle.oba"), 20, true, returnTrue);
		AnimationRegistry.registerAnimation("Freddy", "Stage", new ResourceLocation("fnafmod:obsidian/animations/FreddyStageAnimation.oba"), 10, false, isIdleStage);
		AnimationRegistry.registerAnimation("Freddy", "IdleShake", new ResourceLocation("fnafmod:obsidian/animations/Shake.oba"), 11, false, isIdleShaking);
		AnimationRegistry.registerAnimation("Freddy", "IdleStare", new ResourceLocation("fnafmod:obsidian/animations/IdleStare.oba"), 12, false, isIdleStare);
		AnimationRegistry.registerAnimation("Freddy", "Walking", new ResourceLocation("fnafmod:obsidian/animations/Walk.oba"), 0, true, isWalking);


	}

	private boolean isDayTime(long time) {
		int hours = (int)time / 1000 + 6 > 24 ? (int)time / 1000 + 6 - 24 : (int)time / 1000 + 6;
		return hours >= 6 && hours < 19;
	}

}



