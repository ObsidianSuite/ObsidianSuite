package obsidianAnimations;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import obsidianAPI.ObsidianEventHandler;
import obsidianAPI.debug.EventHandlerDebug;

@Mod(modid = "ObsidianAnimations")
public class ObsidianAnimations
{

	@Mod.Instance("ObsidianAnimations")
	public static ObsidianAnimations instance;

	@SidedProxy(serverSide = "obsidianAnimations.CommonProxy", clientSide = "obsidianAnimations.ClientProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{		
		instance = this;		
		proxy.init();
		proxy.registerAnimations();

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

		//		ObsidianEventHandler eventHandler = new ObsidianEventHandler();
		//		MinecraftForge.EVENT_BUS.register(eventHandler);
		//
		//		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		//			FMLCommonHandler.instance().bus().register(eventHandler);
	}

}
