package obsidianAnimator.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAPI.render.part.prop.PartPropRotation;
import obsidianAPI.render.part.prop.PartPropTranslation;
import obsidianAnimator.render.entity.EntityObj;
import obsidianAnimator.render.entity.ModelObj_Animator;
import obsidianAnimator.render.entity.RenderObj_Animator;

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

//	        postRenderItem(itemstack1, modelObj.getPartObjFromName("armLwR"),
//	                         modelObj.getPartFromName("prop_trans"),
//	                         (PartRotation) modelObj.getPartFromName("prop_rot"),
//	                         modelObj.getPartFromName("prop_scale"));
			
			this.renderHeldItem(entityObj, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			this.renderHeldItem(entityObj, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GL11.glPopMatrix();
		}
	}


	private void renderHeldItem(EntityLivingBase p_188358_1_, ItemStack p_188358_2_, ItemCameraTransforms.TransformType p_188358_3_, EnumHandSide handSide)
	{
		if (!p_188358_2_.isEmpty())
		{
			GlStateManager.pushMatrix();

			if (p_188358_1_.isSneaking())
			{
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			this.transformToHand(handSide);
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = handSide == EnumHandSide.LEFT;
			GlStateManager.translate(0, 0.125F, -0.325F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(p_188358_1_, p_188358_2_, p_188358_3_, flag);
			GlStateManager.popMatrix();
		}
	}

    public void transformToHand(EnumHandSide handSide)
    {
		ModelObj_Animator modelObj = objRenderer.getModel();
    	boolean right = handSide.equals(EnumHandSide.RIGHT);
    	PartPropTranslation propTrans = right ? (PartPropTranslation) modelObj.getPartFromName("prop_trans") : (PartPropTranslation) modelObj.getPartFromName("prop_trans_l");
    	PartPropRotation propRot = right ? (PartPropRotation) modelObj.getPartFromName("prop_rot") : (PartPropRotation) modelObj.getPartFromName("prop_rot_l");
    	PartObj hand = right ? modelObj.getPartObjFromName("armLwR") : modelObj.getPartObjFromName("armLwL");
    	
        //Prop rotation. Need to swap signs so rotation is the correct way.
        propRot.setValue(-propRot.getValue(1), 1);
        propRot.setValue(-propRot.getValue(2), 2);

        hand.postRenderAll();
        GL11.glTranslatef(propTrans.getValue(0), propTrans.getValue(1), propTrans.getValue(2));
        propRot.rotate();

        //Need to swap back to original value.
        propRot.setValue(-propRot.getValue(1), 1);
        propRot.setValue(-propRot.getValue(2), 2);
    }

	public boolean shouldCombineTextures()
	{
		return false;
	}
}
