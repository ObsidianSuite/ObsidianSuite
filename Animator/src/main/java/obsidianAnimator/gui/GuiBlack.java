package obsidianAnimator.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

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
		mc.world.sendQuittingDisconnectingPacket();
        mc.loadWorld((WorldClient)null);
        mc.displayGuiScreen(new GuiAnimationMainMenu());
    }
	
	private void drawCustomGui(double x, double y, double width, double height, double zLevel)
	{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + 0, y + height, zLevel).endVertex();
        bufferbuilder.pos(x + width, y + height, zLevel).endVertex();
        bufferbuilder.pos(x + width, y + 0, zLevel).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).endVertex();
        tessellator.draw();
	}

}
