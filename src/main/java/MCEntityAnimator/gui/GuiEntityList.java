package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public class GuiEntityList extends GuiScreen 
{
	int posX = 85;
	int posY = 2;
	
	public GuiEntityList()
	{
		this.mc = Minecraft.getMinecraft();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		mc.displayGuiScreen(new GuiEntityListFG(this));
	}

	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(posX, posY, 0, 0, 256, 256);		

		super.drawScreen(par1, par2, par3);
	}
	
	public FontRenderer getFontRenderer()
	{
		return this.fontRendererObj;
	}

}


