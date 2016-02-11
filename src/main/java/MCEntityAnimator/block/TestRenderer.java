package MCEntityAnimator.block;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

public class TestRenderer extends TileEntitySpecialRenderer
{

	public WavefrontObject model;
	private final ResourceLocation modelRL;
	private static final ResourceLocation defaultTexture = new ResourceLocation("mod_mcea:objModels/default.png"); 

    
    public TestRenderer()
    {
    	modelRL = new ResourceLocation("mod_mcea:objModels/block.obj");
		model = (WavefrontObject) AdvancedModelLoader.loadModel(modelRL);
    }

    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
    {
        //OpenGL stuff
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        //Bind the texture and render the model
        bindTexture(defaultTexture);
        model.renderAll();

        //OpenGL stuff to put everything back
        GL11.glPopMatrix();
    }
}