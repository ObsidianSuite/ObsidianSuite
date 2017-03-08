package obsidianAnimator.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.PartEntityPos;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAnimator.render.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderObj_Animator extends RenderLiving
{
	private ModelObj modelObj;
			
	public RenderObj_Animator() 
	{
		super(null, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return modelObj.getTexture();
	}
	
	public void setModel(ModelObj model)
	{
		modelObj = model;
		mainModel = model;
	}

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		PartEntityPos pos = (PartEntityPos) modelObj.getPartFromName("entitypos");
		float[] values = pos.getValues();
        super.doRender(p_76986_1_, p_76986_2_ + values[0], p_76986_4_ + values[1], p_76986_6_ + values[2], p_76986_8_, p_76986_9_);
    }

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float f)
	{

//		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiAnimationTimeline 
//				&& ((GuiAnimationTimeline) Minecraft.getMinecraft().currentScreen).shouldRenderShield())
//		{
//			renderShield();
//		}
//
//		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiAnimationStanceCreator 
//				&& ((GuiAnimationStanceCreator) Minecraft.getMinecraft().currentScreen).shouldRenderShield())
//		{
//			renderShield();
//		}
		
		GL11.glColor3f(1.0F, 1.0F, 1.0F);       
		ItemStack itemstack1 = entity.getHeldItem();
		//itemstack1 = new ItemStack(Items.wooden_sword);

		float f2;
		float f4;

		if (itemstack1 != null)
		{
			GL11.glPushMatrix();

			GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);
			
			//Post render for lower right arm.
			PartObj armLwR = modelObj.getPartObjFromName("armLwR");
			armLwR.postRenderAll();
			
			//Prop rotation and translation
			PartRotation prop_rot = (PartRotation) modelObj.getPartFromName("prop_rot");

			float[] propTranslation = modelObj.getPartFromName("prop_trans").getValues();
			GL11.glTranslatef(propTranslation[0], propTranslation[1], propTranslation[2]);

			EnumAction enumaction = null;

			net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(itemstack1, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

			if (is3D || itemstack1.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack1.getItem()).getRenderType()))
			{
				GL11.glRotatef(180F, 1, 0, 0);
				GL11.glTranslatef(0, 0.30F, -0.3f);
				prop_rot.rotate();
				//drawRotationAxis();
				GL11.glTranslatef(-0, -0.30F, 0.3f);
				GL11.glRotatef(-180F, 1, 0, 0);

				f2 = 0.5F;
				f2 *= 0.75F;
				GL11.glTranslatef(0f, -0.3f, 0.3f);
				GL11.glRotatef(45, 1f, 0f, 0f);
				GL11.glRotatef(45, 0f, 1f, 0f);
				GL11.glScalef(-f2, -f2, f2);
			}
			else if (itemstack1.getItem() == Items.bow)
			{
				GL11.glRotatef(180F, 1, 0, 0);
				GL11.glTranslatef(0.125f, 0.17F, 0.0f);
				prop_rot.rotate();
				//drawRotationAxis();
				GL11.glTranslatef(-0.125f, -0.17F, 0.0f);
				GL11.glRotatef(-180F, 1, 0, 0);

				f2 = 0.625F;
				GL11.glTranslatef(0.125f, -0.4f, -0.25f);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(90f, 1f, 0f, 0f);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else if (itemstack1.getItem().isFull3D())
			{
				GL11.glRotatef(180F, 1, 0, 0);
				GL11.glTranslatef(-0.00f, 0.22F, -0.04f);
				prop_rot.rotate();
				//drawRotationAxis();
				GL11.glTranslatef(0.00f, -0.22F, 0.04f);
				GL11.glRotatef(-180F, 1, 0, 0);

				f2 = 0.625F;

				GL11.glTranslatef(0.03f, -0.15F, 0f);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				GL11.glRotatef(180F, 1, 0, 0);
				GL11.glTranslatef(0, 0.30F, -0.3f);
				prop_rot.rotate();
				//drawRotationAxis();
				GL11.glTranslatef(-0, -0.30F, 0.3f);
				GL11.glRotatef(-180F, 1, 0, 0);

				f2 = 0.375F;
				GL11.glTranslatef(0.2f, -0.3f, 0.07f);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(-90f, 0, 0f, 1f);
				GL11.glRotatef(110f, 1f, 0f, 0f);
				GL11.glRotatef(45f, 0f, 1f, 0f);
			}
			
			float f3;
			int k;
			float f12;

			if (itemstack1.getItem().requiresMultipleRenderPasses())
			{
				for (k = 0; k < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++k)
				{
					int i = itemstack1.getItem().getColorFromItemStack(itemstack1, k);
					f12 = (float)(i >> 16 & 255) / 255.0F;
					f3 = (float)(i >> 8 & 255) / 255.0F;
					f4 = (float)(i & 255) / 255.0F;
					GL11.glColor4f(f12, f3, f4, 1.0F);
					this.renderManager.itemRenderer.renderItem(entity, itemstack1, k);
				}
			}
			else
			{
				k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
				float f11 = (float)(k >> 16 & 255) / 255.0F;
				f12 = (float)(k >> 8 & 255) / 255.0F;
				f3 = (float)(k & 255) / 255.0F;
				GL11.glColor4f(f11, f12, f3, 1.0F);
				this.renderManager.itemRenderer.renderItem(entity, itemstack1, 0);
			}

			GL11.glPopMatrix();
		}
	}

	private void drawRotationAxis()
	{
		int colour = 0xFFFFFF;
		Vec3 u = null;
		Vec3 v = null;
		for(int i = 0; i < 3; i++)
		{
			switch(i)
			{
				case 0:
					colour = 0xFF0000;
					u = Vec3.createVectorHelper(-MathHelper.rotationWheelRadius, 0.0F, 0.0F);
					v = Vec3.createVectorHelper(MathHelper.rotationWheelRadius, 0.0F, 0.0F);
					break;
				case 1:
					colour = 0x00FF00;
					u = Vec3.createVectorHelper(0.0F, -MathHelper.rotationWheelRadius, 0.0F);
					v = Vec3.createVectorHelper(0.0F, MathHelper.rotationWheelRadius, 0.0F);
					break;
				case 2:
					colour = 0x0000FF;
					u = Vec3.createVectorHelper(0.0F, 0.0F, -MathHelper.rotationWheelRadius);
					v = Vec3.createVectorHelper(0.0F, 0.0F, MathHelper.rotationWheelRadius);
					break;
			}
				drawLine(u, v, colour, 4.0F, 1F);
		}
	}

	private void drawLine(Vec3 p1, Vec3 p2, int color, float width, float alpha)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(1);
		tessellator.setColorRGBA_I(color, (int) (alpha*255));
		tessellator.addVertex(p1.xCoord,p1.yCoord,p1.zCoord);
		tessellator.addVertex(p2.xCoord,p2.yCoord,p2.zCoord);
		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
