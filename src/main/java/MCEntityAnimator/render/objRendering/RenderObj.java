package MCEntityAnimator.render.objRendering;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import MCEntityAnimator.item.ModelLargeShield;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderObj extends RenderLiving
{
	static ModelObj modelObj;
	
	private HashMap<String, ModelObj> models;
	
	private static final ModelLargeShield shieldModel = new ModelLargeShield();
	private static final ResourceLocation shieldTexture = new ResourceLocation("mod_pxy:textures/models/L_shield.png");
	private static final ResourceLocation defaultTexture = new ResourceLocation("mod_mcea:defaultModelTextures/grey.png");
	private ResourceLocation properTexture;
	
	public RenderObj() 
	{
		super(null, 0.5F);
        models = new HashMap<String, ModelObj>();	
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return getModel(((EntityObj) entity).getType()).textureExists ? properTexture : defaultTexture;
	}

	public void updateModel(String entityType)
	{
		if(models.containsKey(entityType))
		{
			mainModel = models.get(entityType);
			modelObj = models.get(entityType);
		}
		else
		{
			ModelObj m = new ModelObj(entityType);
			models.put(entityType, m);
			modelObj = m;
			mainModel = m;
		}
        properTexture = modelObj.getTexture();
	}
	
	public ModelObj getModel(String entityType) 
	{
		this.updateModel(entityType);
		return modelObj;
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
			
			//Post render for lower right arm.
			PartObj armLwR = Util.getPartObjFromName("armLwR", modelObj.parts);
			armLwR.postRenderItem();
			
			//Prop rotation and translation
			float[] propRotation = Util.getPartFromName("prop_rot", modelObj.parts).getValues();
			float[] propTranslation = Util.getPartFromName("prop_trans", modelObj.parts).getValues();
			GL11.glTranslatef(propTranslation[0], propTranslation[1], propTranslation[2]);	
			
			GL11.glRotatef(180F, 1, 0, 0);
			Util.getPartFromName("prop_rot", modelObj.parts).rotate();
			GL11.glRotatef(-180F, 1, 0, 0);
			
			EnumAction enumaction = null;

			net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(itemstack1, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

			if (is3D || itemstack1.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack1.getItem()).getRenderType()))
			{
				f2 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f2 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-f2, -f2, f2);
			}
			else if (itemstack1.getItem() == Items.bow)
			{
				f2 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else if (itemstack1.getItem().isFull3D())
			{
				f2 = 0.625F;

				if (itemstack1.getItem().shouldRotateAroundWhenRendering())
				{
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				GL11.glTranslatef(0.07F, 0.15F, -0.14F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				f2 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
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

	public void renderShield()
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;

		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(shieldTexture);
//		Util.getPartObjFromName("chest", modelObj.parts).postRender(0.0625f);
//		Util.getPartObjFromName("armupl", modelObj.parts).postRender(0.0625f);
//		Util.getPartObjFromName("armlwl", modelObj.parts).postRender(0.0625f);
		GL11.glTranslatef(-0.375F, 0.0F, 0.0F); 
		shieldModel.render(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
	
	

}
