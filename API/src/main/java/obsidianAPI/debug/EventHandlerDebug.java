package obsidianAPI.debug;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class EventHandlerDebug {

	private GuiDebug guiDebug = new GuiDebug(Minecraft.getMinecraft());
	
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event)
	{
		if (event.type != ElementType.EXPERIENCE) return;
		guiDebug.draw();
	}

}
