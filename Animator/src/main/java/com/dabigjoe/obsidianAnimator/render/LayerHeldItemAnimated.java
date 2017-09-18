package com.dabigjoe.obsidianAnimator.render;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.part.PartRotation;
import com.dabigjoe.obsidianAPI.render.part.prop.PartPropRotation;
import com.dabigjoe.obsidianAPI.render.part.prop.PartPropTranslation;
import com.dabigjoe.obsidianAnimator.render.entity.EntityObj;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;
import com.dabigjoe.obsidianAnimator.render.entity.RenderObj_Animator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHeldItemAnimated implements LayerRenderer<EntityObj> {
	
	private final RenderObj_Animator objRenderer;
	
	public LayerHeldItemAnimated(RenderObj_Animator objRenderer)
	{
		this.objRenderer = objRenderer;
	}

	public void doRenderLayer(EntityObj entityObj, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		boolean flag = entityObj.getPrimaryHand() == EnumHandSide.RIGHT;
		ItemStack itemstack = flag ? entityObj.getHeldItemOffhand() : entityObj.getHeldItemMainhand();
		ItemStack itemstack1 = flag ? entityObj.getHeldItemMainhand() : entityObj.getHeldItemOffhand();

		if (!itemstack.isEmpty() || !itemstack1.isEmpty())
		{
			GL11.glPushMatrix();
			
			//Basic model fixes
            GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);
			
			this.renderHeldItem(entityObj, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			this.renderHeldItem(entityObj, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GL11.glPopMatrix();
		}
	}

	private void renderHeldItem(EntityObj entityObj, ItemStack itemStack, ItemCameraTransforms.TransformType cameraTransforms, EnumHandSide handSide)
	{
		if (!itemStack.isEmpty())
		{
			GlStateManager.pushMatrix();

			if (entityObj.isSneaking())
			{
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			objRenderer.transformToHandAndRotateAndScale(handSide);
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = handSide == EnumHandSide.LEFT;
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityObj, itemStack, cameraTransforms, flag);
			GlStateManager.popMatrix();
		}
	}

	public boolean shouldCombineTextures()
	{
		return false;
	}
}
