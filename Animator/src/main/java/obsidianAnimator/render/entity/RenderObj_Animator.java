package obsidianAnimator.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAPI.render.part.prop.PartPropScale;

@SideOnly(Side.CLIENT)
public class RenderObj_Animator extends RenderLiving
{
	private ModelObj_Animator modelObj;

	public RenderObj_Animator() 
	{
		super(null, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return modelObj.getTexture();
	}

	public void setModel(ModelObj_Animator model)
	{
		modelObj = model;
		mainModel = model;
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float f)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);       
		ItemStack itemstack1 = entity.getHeldItem();

		float f4;

		if (itemstack1 != null)
		{
			GL11.glPushMatrix();

			//Basic model fixes
			GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);

			postRenderItem(itemstack1);

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
	
	/**
	 * Transform an existing GL11 matrix to the held item location.
	 * Takes prop translation into account.
	 */
	public void transformToItemCentre(ItemStack itemstack)
	{				
		//Post render for lower right arm.
		PartObj armLwR = modelObj.getPartObjFromName("armLwR");
		armLwR.postRenderAll();

		//Prop translation
		float[] propTranslation = modelObj.getPartFromName("prop_trans").getValues();
		GL11.glTranslatef(propTranslation[0], propTranslation[1], propTranslation[2]);

		if (itemstack != null)
		{
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D));

			if (is3D || itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
				GL11.glTranslatef(0, -0.30F, 0.3f);
			else if (itemstack.getItem() == Items.bow)
				GL11.glTranslatef(-0.125f, -0.17F, 0.0f);
			else if (itemstack.getItem().isFull3D())
				GL11.glTranslatef(0.00f, -0.22F, 0.04f);
			else
				GL11.glTranslatef(0, -0.30F, 0.2f);
		}
	}

	/**
	 * Transform an existing GL11 matrix to the held item location.
	 * Takes prop rotation and translation into account.
	 */
	public void transformToItemCentreAndRotate(ItemStack itemstack)
	{				

		//Prop rotation. Need to swap signs so rotation is the correct way.
		PartRotation prop_rot = (PartRotation) modelObj.getPartFromName("prop_rot");
		//System.out.println(prop_rot.getValue(0));
		prop_rot.setValue(-prop_rot.getValue(1), 1);
		prop_rot.setValue(-prop_rot.getValue(2), 2);

		transformToItemCentre(itemstack);
		prop_rot.rotate();

		//Need to swap back to original value.
		prop_rot.setValue(-prop_rot.getValue(1), 1);
		prop_rot.setValue(-prop_rot.getValue(2), 2);
	}

	/**
	 * Transform an existing GL11 matrix for the player item.
	 * Transforms to item centre, then does further transforms based on item (default MC transforms)
	 */
	public void postRenderItem(ItemStack itemstack)
	{
		float f2;

		transformToItemCentreAndRotate(itemstack);

		PartPropScale prop_scale = (PartPropScale) modelObj.getPartFromName("prop_scale");
		float[] scaleVals = prop_scale.getValues();
		
		if (itemstack != null)
		{
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D));

			if (is3D || itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
			{
				f2 = 0.5F;
				f2 *= 0.75F;
				
				GL11.glTranslatef(0, 0.30F, -0.3f);
				GL11.glTranslatef(0f, -0.3f, 0.3f);
				
				//Prop scale
				GL11.glScalef(1.0f + scaleVals[0], 1.0f  + scaleVals[1], 1.0f + scaleVals[2]);
				
				GL11.glRotatef(45, 1, 0, 0);
				GL11.glRotatef(45, 0f, 1f, 0f);
				GL11.glRotatef(180, 1f, 0f, 0f);
				GL11.glRotatef(-90, 0f, 1f, 0f);
				GL11.glScalef(-f2, -f2, f2);

			}
			else if (itemstack.getItem() == Items.bow)
			{
				f2 = 0.625F;

				//TODO postRenderItem: bow - adjust all this
				GL11.glTranslatef(0.125f, 0.17F, 0f);
				GL11.glTranslatef(0.125f, -0.4f, -0.25f);
				GL11.glScalef(f2, -f2, f2);
				
				//Prop scale
				GL11.glScalef(1.0f + scaleVals[0], 1.0f  + scaleVals[1], 1.0f + scaleVals[2]);
				
				GL11.glRotatef(90f, 1f, 0f, 0f);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else if (itemstack.getItem().isFull3D())
			{
				f2 = 0.625F;

				//Prop scale
				GL11.glScalef(1.0f + scaleVals[0], 1.0f  + scaleVals[1], 1.0f + scaleVals[2]);
				
				GL11.glTranslatef(0, 0.22F, -0.04f);
				GL11.glTranslatef(0f, -0.15F, 0f);

				GL11.glTranslatef(0.03f, 0f, 0f);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				f2 = 0.375F;
				
				//Prop scale
				GL11.glScalef(1.0f + scaleVals[0], 1.0f  + scaleVals[1], 1.0f + scaleVals[2]);

				GL11.glTranslatef(-0.2f,0.02f,-0.1f);
				GL11.glScalef(f2, f2, f2);				
				GL11.glRotatef(88, 1, 0, 0);
				GL11.glRotatef(130, 0, 1, 0);
				GL11.glRotatef(26, 1, 0, 1);
			}
		}
				
		
	}

}
