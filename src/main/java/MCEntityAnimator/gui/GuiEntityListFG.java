package MCEntityAnimator.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class GuiEntityListFG extends GuiScreen
{
	protected GuiEntityListBG list;
	
	public GuiEntityListFG(GuiEntityList par0Gui)
	{
		this.list = new GuiEntityListBG(par0Gui);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
	}
	
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		list.drawScreen(par1, par2, par3);		
		super.drawScreen(par1, par2, par3);
	}
}