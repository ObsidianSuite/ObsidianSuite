package MCEntityAnimator.gui;

import java.util.List;

import MCEntityAnimator.DataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

class GuiEntityListBG extends GuiSlot
{
	protected int slotHeight;
	private static Minecraft mc = Minecraft.getMinecraft();

	protected List<String> entityNames;
	private GuiEntityList gui;

	public GuiEntityListBG(GuiEntityList par0Gui)
	{
		super(mc, par0Gui.width, par0Gui.height, 32, par0Gui.height, 24);
		entityNames = DataHandler.getEntities();
		this.slotHeight = 24;
		gui = par0Gui;
	}

	/**
	 * Gets the size of the current slot list.
	 */
	protected int getSize()
	{
		return this.entityNames.size();
	}

	/**
	 * the element in the slot that was clicked, boolean for whether it was double clicked or not
	 */
	@Override
	protected void elementClicked(int par0, boolean par1Bol, int par2, int par3)
	{
		mc.displayGuiScreen(new GuiAnimationHome(entityNames.get(par0)));
	}

	/**
	 * returns true if the element passed in is currently selected
	 */
	protected boolean isSelected(int par1)
	{
		return false;
	}
	/**
	 * return the height of the content being scrolled
	 */
	protected int getContentHeight()
	{
		return this.getSize() * 24;
	}

	@Override
	protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator, int par6, int par7)
	{
		gui.getFontRenderer().setBidiFlag(true);
		this.gui.drawCenteredString(gui.getFontRenderer(), entityNames.get(par1), this.gui.width / 2, par3 + 1, 16777215);
	}

	@Override
	protected void drawBackground()
	{
		this.gui.drawDefaultBackground();
	}

}