package obsidianAnimations;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import obsidianAPI.ObsidianAPI;
import obsidianAPI.ObsidianEventHandler;
import obsidianAPI.debug.EventHandlerDebug;
import obsidianAPI.network.AnimationNetworkHandler;

@Mod(modid = ObsidianAnimations.MODID, name = ObsidianAnimations.MODNAME, version = ObsidianAnimations.VERSION)
public class ObsidianAnimations
{

	public static final String MODID = "ObsidianAnimations";
	public static final String MODNAME = "Obsidian Animations";
	public static final String VERSION = "0.1.0";

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
