package obsidianAPI.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class GuiDebug extends Gui {

	public static GuiDebug instance;
	public String text = "";
	private Minecraft mc;
	
	public GuiDebug(Minecraft mc)
	{
		this.mc = mc;
		instance = this;
	}

	public void draw() {
		ScaledResolution scaled = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int width = scaled.getScaledWidth();
		drawCenteredString(mc.fontRenderer, text, width - 40, 2, Integer.parseInt("FFFF00", 16));
	}
	
	
}
