package obsidianAnimator.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import obsidianAnimator.Util;

public class GuiBlack extends GuiScreen
{

	private final ResourceLocation texture = new ResourceLocation("mod_obsidian_animator:gui/gui_black.png");
	private boolean closeToMenu = false;
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		if(closeToMenu)
			returnToMainMenu();
		
		
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);		
		drawCustomGui(0, 0, width, height, 0);	
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		super.drawScreen(par1, par2, par3);
	}
	
	public void initateClose()
	{
		closeToMenu = true;
	}
	
	private void returnToMainMenu()
	{
		mc.theWorld.sendQuittingDisconnectingPacket();
        mc.loadWorld((WorldClient)null);
		mc.displayGuiScreen(new GuiAnimationMainMenu());
	}
	
	private void drawCustomGui(double x, double y, double width, double height, double zLevel)
	{
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
	}

}
