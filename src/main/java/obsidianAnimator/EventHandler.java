package obsidianAnimator;

import java.io.IOException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.WorldEvent;
import obsidianAnimator.data.Persistence;
import obsidianAnimator.gui.GuiAnimationMainMenu;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;
import obsidianAnimator.updater.ServerConfig;
import obsidianAnimator.updater.Updater;

public class EventHandler 
{

	boolean updateChecked = false;

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		try 
		{
			if(event.gui instanceof GuiMainMenu && !updateChecked)
			{
				ServerConfig.init();
				new Updater().checkForUpdate(ObsidianAnimator.version);
				updateChecked = true;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{    	    	
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && !(Minecraft.getMinecraft().currentScreen instanceof GuiAnimationMainMenu))
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationMainMenu());
		
		if(Minecraft.getMinecraft().inGameHasFocus && Minecraft.getMinecraft().currentScreen == null && MinecraftServer.getServer().getWorldName().equals("animation_world"))
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
			new HomeFrame().display();
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		Persistence.save();
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Load event)
	{
		Persistence.load();
	}
}