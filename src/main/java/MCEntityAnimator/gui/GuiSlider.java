package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton
{
	private static final ResourceLocation texture = new ResourceLocation("mod_mcea:gui/controls.png");

	private static final int w = 5;
	private static final int h = 7;
	
	public GuiSlider(int par1, int par2, int par3) 
	{		
		super(par1, par2, par3, w, h, "");
		this.width = w;
		this.height = h; 		
	}

	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);       
		par1Minecraft.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 25, 0, this.width, this.height); 
	}
}
