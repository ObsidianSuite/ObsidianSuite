package com.dabigjoe.obsidianAPI.debug;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventHandlerDebug {

	private GuiDebug guiDebug = new GuiDebug(Minecraft.getMinecraft());
	
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() != ElementType.EXPERIENCE) return;
		guiDebug.draw();
	}

}
