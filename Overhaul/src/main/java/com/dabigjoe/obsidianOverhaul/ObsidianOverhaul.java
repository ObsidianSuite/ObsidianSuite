package com.dabigjoe.obsidianOverhaul;

import com.dabigjoe.obsidianAPI.ObsidianAPI;
import com.dabigjoe.obsidianAPI.ObsidianEventHandler;
import com.dabigjoe.obsidianAPI.debug.EventHandlerDebug;
import com.dabigjoe.obsidianAPI.network.AnimationNetworkHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ObsidianOverhaul.MODID, name = ObsidianOverhaul.MODNAME, version = ObsidianOverhaul.VERSION)
public class ObsidianOverhaul
{

	public static final String MODID = "obsidian_overhaul";
	public static final String MODNAME = "Obsidian Overhaul";
	public static final String VERSION = "0.0.1";

	@Mod.Instance
	public static ObsidianOverhaul instance;

	@SidedProxy(serverSide = "com.dabigjoe.obsidianOverhaul.CommonProxy", clientSide = "com.dabigjoe.obsidianOverhaul.ClientProxy")
	public static CommonProxy proxy;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{		
		instance = this;		
		proxy.init();
		proxy.registerAnimations();

		AnimationNetworkHandler.init();
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			EventHandlerDebug eventHandlerDebug = new EventHandlerDebug();
			MinecraftForge.EVENT_BUS.register(eventHandlerDebug);
			FMLCommonHandler.instance().bus().register(eventHandlerDebug);
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ObsidianEventHandler());
		ObsidianAPI.EVENT_BUS.register(new AnimationEventHandler());
	}

}
