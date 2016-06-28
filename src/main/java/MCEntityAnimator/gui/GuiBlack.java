package MCEntityAnimator.gui;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiBlack extends GuiScreen
{

	private final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_parenting.png");

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);		
		Util.drawCustomGui(0, 0, width, height, 0);	
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		super.drawScreen(par1, par2, par3);
	}

}
