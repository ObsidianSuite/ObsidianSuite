package obsidianAPI.render.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.debug.GuiDebug;
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;

public class ModelAnimatedPlayer extends ModelAnimated
{

	private PartObj head, bodyUp, armUpL, armUpR, legUpL, legUpR;
	private float previousSwingTime = 0.0F;

	public ModelAnimatedPlayer(String entityName, WavefrontObject object, ResourceLocation texture)
	{
		super(entityName, object, texture);
		head = getPartObjFromName("head");
		bodyUp = getPartObjFromName("bodyUp");
		armUpL = getPartObjFromName("armUpL");
		armUpR = getPartObjFromName("armUpR");
		legUpL = getPartObjFromName("legUpL");
		legUpR = getPartObjFromName("legUpR");		
	}
	

	@Override
	protected void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps) {
		String state;
		
		if(swingTime - previousSwingTime > 0.05F) {
			if(entity.isSprinting()) {
				if(!entity.isCollidedVertically)
					state = "SprintJump";
				else
					state = "SprintF";
			}
			else if(entity.isSneaking())
				state = "Crouch";
			else {
				if(!entity.isCollidedVertically)
					state = "RunJump";
				else
					state = "WalkF";
			}
		}
		else if(this.onGround != 0F){
			state = "Swing";
		}
		else if(!entity.isCollidedVertically) {
			state = "Jump";
		}
		else {
			if(entity.isSneaking())
				state = "CrouchedIdle";
			else
				state = "Idle";
		}	
		
		GuiDebug.instance.stateText = state;
				
		if(state.equals("WalkF") && !isAnimationActive(animProps, "WalkF"))
		{
			animProps.setActiveAnimation(this, "WalkF", true);
		} 
		else if(state.equals("SprintF") && !isAnimationActive(animProps, "SprintF"))
		{
			animProps.setActiveAnimation(this, "SprintF", true);
		}
		else if(!isIdle(animProps) && state.equals("Idle"))
		{
			animProps.clearAnimation(this);
		}
		
		previousSwingTime = swingTime;
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);		

		//Look TODO sort head lookX
		head.setValue(lookY/(180F/(float)Math.PI), 0);

		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
			GuiDebug.instance.animationText = "null";
		else
		{
			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq == null)
				GuiDebug.instance.animationText = "null";
			else
				GuiDebug.instance.animationText = seq.getName();
		}		
	}
	
	@Override
	protected void doDefaultAnimations(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity) {

		//Walking - swing time changing.
		armUpR.setValue(MathHelper.cos(swingTime * 0.6662F + (float)Math.PI) * 2.0F * swingMax * 0.5F, 0);
		armUpL.setValue(MathHelper.cos(swingTime * 0.6662F) * 2.0F * swingMax * 0.5F, 0);
		armUpR.setValue(0.0F, 2);
		armUpL.setValue(0.0F, 2);
		legUpR.setValue(MathHelper.cos(swingTime * 0.6662F) * 1.4F * swingMax, 0);
		legUpL.setValue(MathHelper.cos(swingTime * 0.6662F + (float)Math.PI) * 1.4F * swingMax, 0);
		legUpR.setValue(0.0F, 2);
		legUpL.setValue(0.0F, 2);

		//Riding
		//        if (this.isRiding)
		//        {
		//            armUpR.rotateAngleX += -((float)Math.PI / 5F);
		//            armUpL.rotateAngleX += -((float)Math.PI / 5F);
		//            legUpR.rotateAngleX = -((float)Math.PI * 2F / 5F);
		//            legUpL.rotateAngleX = -((float)Math.PI * 2F / 5F);
		//            legUpR.rotateAngleY = ((float)Math.PI / 10F);
		//            legUpL.rotateAngleY = -((float)Math.PI / 10F);
		//        }

		//Item left
		//        if (this.heldItemLeft != 0)
		//        {
		//            armUpL.rotateAngleX = armUpL.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemLeft;
		//        }

		//Item right
		//        if (this.heldItemRight != 0)
		//        {
		//            armUpR.rotateAngleX = armUpR.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemRight;
		//        }

		//Not sure what this does
		//onGround seems to indicate swinging.
		armUpR.setValue(0.0F, 1);
		armUpL.setValue(0.0F, 1);
		float f6;
		float f7;

		if (this.onGround > -9990.0F)
		{
			f6 = this.onGround;
			bodyUp.setValue(MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F, 1);
			//armUpR.setValue(MathHelper.sin(bodyUp.getValue(1)) * 5.0F, 2);
			//armUpR.setValue(-MathHelper.cos(bodyUp.getValue(1)) * 5.0F, 0);
			//armUpL.setValue(-MathHelper.sin(bodyUp.getValue(1)) * 5.0F, 2);
			//armUpL.setValue(MathHelper.cos(bodyUp.getValue(1)) * 5.0F, 0);
			armUpR.setValue(armUpR.getValue(1) + bodyUp.getValue(1), 1);
			armUpL.setValue(armUpR.getValue(1) + bodyUp.getValue(1), 1);
			armUpL.setValue(armUpL.getValue(0) + bodyUp.getValue(1), 0);
			f6 = 1.0F - this.onGround;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			f7 = MathHelper.sin(f6 * (float)Math.PI);
			float f8 = MathHelper.sin(this.onGround * (float)Math.PI) * -(head.getValue(0) - 0.7F) * 0.75F;
			armUpR.setValue((float) (armUpR.getValue(0)  - ((double)f7 * 1.2D + (double)f8)), 0);            
			armUpR.setValue(armUpR.getValue(1) + bodyUp.getValue(1) * 2.0F, 1);
			armUpR.setValue(MathHelper.sin(this.onGround * (float)Math.PI) * -0.4F, 2);
		}

		//Sneaking, or not...
		//        if (this.isSneak)
		//        {
		//            this.bipedBody.rotateAngleX = 0.5F;
		//            armUpR.rotateAngleX += 0.4F;
		//            armUpL.rotateAngleX += 0.4F;
		//            legUpR.rotationPointZ = 4.0F;
		//            legUpL.rotationPointZ = 4.0F;
		//            legUpR.rotationPointY = 9.0F;
		//            legUpL.rotationPointY = 9.0F;
		//            head.rotationPointY = 1.0F;
		//            headwear.rotationPointY = 1.0F;
		//        }
		//        else
		//        {
		//            this.bipedBody.rotateAngleX = 0.0F;
		//            legUpR.rotationPointZ = 0.1F;
		//            legUpL.rotationPointZ = 0.1F;
		//            legUpR.rotationPointY = 12.0F;
		//            legUpL.rotationPointY = 12.0F;
		//            head.rotationPointY = 0.0F;
		//            headwear.rotationPointY = 0.0F;
		//        }
		//

		//Idle     
		armUpR.setValue(armUpR.getValue(2) + MathHelper.cos(clock * 0.09F) * 0.05F + 0.05F, 2);
		armUpL.setValue(armUpL.getValue(2) - MathHelper.cos(clock * 0.09F) * 0.05F + 0.05F, 2);
		armUpR.setValue(armUpR.getValue(0) + MathHelper.sin(clock * 0.067F) * 0.05F, 0);
		armUpL.setValue(armUpL.getValue(0) - MathHelper.sin(clock * 0.067F) * 0.05F, 0);

		//Bow Aiming
		//        if (this.aimedBow)
		//        {
		//            f6 = 0.0F;
		//            f7 = 0.0F;
		//            armUpR.rotateAngleZ = 0.0F;
		//            armUpL.rotateAngleZ = 0.0F;
		//            armUpR.rotateAngleY = -(0.1F - f6 * 0.6F) + head.rotateAngleY;
		//            armUpL.rotateAngleY = 0.1F - f6 * 0.6F + head.rotateAngleY + 0.4F;
		//            armUpR.rotateAngleX = -((float)Math.PI / 2F) + head.rotateAngleX;
		//            armUpL.rotateAngleX = -((float)Math.PI / 2F) + head.rotateAngleX;
		//            armUpR.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
		//            armUpL.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
		//            armUpR.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
		//            armUpL.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
		//            armUpR.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
		//            armUpL.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
		//        }
	}

}

