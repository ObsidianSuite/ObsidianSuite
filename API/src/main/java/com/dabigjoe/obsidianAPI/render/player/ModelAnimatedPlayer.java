package com.dabigjoe.obsidianAPI.render.player;

import com.dabigjoe.obsidianAPI.debug.GuiDebug;
import com.dabigjoe.obsidianAPI.render.ModelAnimated;
import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.WavefrontObject;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelAnimatedPlayer extends ModelAnimated
{

	private PartObj head, bodyUp, armUpL, armUpR, legUpL, legUpR;
	
	//Debugging
	private String previousState = "";
	private long startTime = 0L;
	private long endTime = 0l;
	private float prevHeight = 0.0F; 
	private float totalTime = 0.0F;
	private int readings = 0;

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
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);		

		//Look TODO sort head lookX
		head.setValue(lookY/(180F/(float)Math.PI), 0);

		if(entity.isSneaking())
			head.setValue(head.getValue(0) - 0.7F, 0);
		
//		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
//		if (animProps == null)
//			GuiDebug.instance.animationText = "null";
//		else
//		{
//			AnimationSequence seq = animProps.getActiveAnimation();
//			if (seq == null)
//				GuiDebug.instance.animationText = "null";
//			else
//				GuiDebug.instance.animationText = seq.getName();
//		}		
		
		String state = "";	
		
		GuiDebug.instance.stateText = state;
		
		//Jump height debugging
//		if(state.equals("Jump") || previousState.equals("Jump")) {
//			float currentTime = (System.nanoTime()-startTime)/1000000000F;
//			float height = (float) (entity.posY-5.62F);
//			if(height != prevHeight) {
//				//Calculations
//				float outputTime = totalTime/(float)readings;
//				outputTime = Math.round(outputTime*1000F)/1000F;
//				float outputHeight = Math.round(prevHeight*1000F)/1000F;
//				//Output
//				System.out.println(outputTime + " " + outputHeight);
//				//Reset
//				totalTime = currentTime;
//				readings = 1;
//			}			
//			else {
//				totalTime += currentTime;
//				readings++;
//			}
//			prevHeight = height;
//		}
//		else {
//			prevHeight = 0.0F;
//			totalTime = 0;
//			readings = 0;
//		}
		
		//Timing debug
//		if(!state.equals(previousState)) {
//			endTime = System.nanoTime();
//			float duration = (endTime - startTime)/1000000000F;
//			if(previousState.equals("Jump"))
//				System.out.println(previousState + " lasted for " + duration + " seconds");
//			startTime = endTime;
//		}
		
		previousState = state;
	}
	
	@Override
	protected void doDefaultAnimations(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity) {
		super.doDefaultAnimations(swingTime, swingMax, clock, lookX, lookY, f5, entity);
		
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

		if (this.swingProgress > -9990.0F)
		{
			f6 = this.swingProgress;
			bodyUp.setValue(MathHelper.sin(MathHelper.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F, 1);
			//armUpR.setValue(MathHelper.sin(bodyUp.getValue(1)) * 5.0F, 2);
			//armUpR.setValue(-MathHelper.cos(bodyUp.getValue(1)) * 5.0F, 0);
			//armUpL.setValue(-MathHelper.sin(bodyUp.getValue(1)) * 5.0F, 2);
			//armUpL.setValue(MathHelper.cos(bodyUp.getValue(1)) * 5.0F, 0);
			armUpR.setValue(armUpR.getValue(1) + bodyUp.getValue(1), 1);
			armUpL.setValue(armUpR.getValue(1) + bodyUp.getValue(1), 1);
			armUpL.setValue(armUpL.getValue(0) + bodyUp.getValue(1), 0);
			f6 = 1.0F - this.swingProgress;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			f7 = MathHelper.sin(f6 * (float)Math.PI);
			float f8 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(head.getValue(0) - 0.7F) * 0.75F;
			armUpR.setValue((float) (armUpR.getValue(0)  - ((double)f7 * 1.2D + (double)f8)), 0);            
			armUpR.setValue(armUpR.getValue(1) + bodyUp.getValue(1) * 2.0F, 1);
			armUpR.setValue(MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F, 2);
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

