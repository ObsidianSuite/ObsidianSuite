package obsidianAnimator.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.prop.PartPropRotation;
import obsidianAPI.render.part.prop.PartPropScale;
import obsidianAPI.render.part.prop.PartPropTranslation;
import obsidianAnimator.render.LayerHeldItemAnimated;

@SideOnly(Side.CLIENT)
public class RenderObj_Animator extends RenderLiving<EntityObj>
{
    private ModelObj_Animator modelObj;

    public RenderObj_Animator()
    {
        super(Minecraft.getMinecraft().getRenderManager(), null, 0.5F);
        this.addLayer(new LayerHeldItemAnimated(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityObj entity)
    {
        return modelObj.getTexture(entity);
    }

    public void setModel(ModelObj_Animator model)
    {
        modelObj = model;
        mainModel = model;
    }
    
    public ModelObj_Animator getModel() {
    	return modelObj;
    }

    public void transformToHand(EnumHandSide handSide)
    {
    	boolean right = handSide.equals(EnumHandSide.RIGHT);
    	PartPropTranslation propTrans = right ? (PartPropTranslation) modelObj.getPartFromName("prop_trans") : (PartPropTranslation) modelObj.getPartFromName("prop_trans_l");
    	PartObj hand = right ? modelObj.getPartObjFromName("armLwR") : modelObj.getPartObjFromName("armLwL");

    	//Position fix
        GL11.glTranslatef(0F, -0.25F, 0.1F);
        
        hand.postRenderAll();
        GL11.glTranslatef(propTrans.getValue(0), propTrans.getValue(1), propTrans.getValue(2));
    }
    
    public void transformToHandAndRotate(EnumHandSide handSide)
    {
    	boolean right = handSide.equals(EnumHandSide.RIGHT);
    	PartPropRotation propRot = right ? (PartPropRotation) modelObj.getPartFromName("prop_rot") : (PartPropRotation) modelObj.getPartFromName("prop_rot_l");
    	
    	transformToHand(handSide);
        propRot.rotate();
    }

    public void transformToHandAndRotateAndScale(EnumHandSide handSide)
    {
    	boolean right = handSide.equals(EnumHandSide.RIGHT);
    	
    	PartPropScale propScale = right ? (PartPropScale) modelObj.getPartFromName("prop_scale") : (PartPropScale) modelObj.getPartFromName("prop_scale_l");
    	transformToHandAndRotate(handSide);
		GL11.glScalef(1F + propScale.getValue(0), 1F + propScale.getValue(1), 1F + propScale.getValue(2));
    }

}
