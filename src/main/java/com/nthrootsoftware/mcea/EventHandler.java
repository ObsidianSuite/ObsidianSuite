package com.nthrootsoftware.mcea;

import java.io.IOException;

import com.nthrootsoftware.mcea.gui.GuiAnimationMainMenu;
import com.nthrootsoftware.mcea.gui.GuiBlack;
import com.nthrootsoftware.mcea.gui.frames.HomeFrame;
import com.nthrootsoftware.mcea.updater.ServerConfig;
import com.nthrootsoftware.mcea.updater.Updater;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

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
				new Updater().checkForUpdate(MCEA_Main.version);
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
		
		if(Minecraft.getMinecraft().inGameHasFocus && Minecraft.getMinecraft().currentScreen == null)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
			new HomeFrame().display();
//			if(GuiHandler.loginGUI == null)
//				GuiHandler.loginGUI = new LoginGUI();
		}
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			event.entity.registerExtendedProperties("ExtendedPropertiesAnimation", new ExtendedPropertiesAnimation());
		}
	}
}