package com.dabigjoe.obsidianAPI.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class GuiDebug extends Gui {

	public static GuiDebug instance;
	public String stateText = "";
	public String animationText = "";
	private Minecraft mc;
	
	public GuiDebug(Minecraft mc)
	{
		this.mc = mc;
		instance = this;
	}

	public void draw() {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		drawCenteredString(mc.fontRenderer, stateText, width - 40, 2, Integer.parseInt("FFFF00", 16));
		drawCenteredString(mc.fontRenderer, animationText, width - 40, 12, Integer.parseInt("FFFF00", 16));
	}
	
	
}
