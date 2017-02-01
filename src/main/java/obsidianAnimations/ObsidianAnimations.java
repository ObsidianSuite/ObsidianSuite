package obsidianAnimations;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

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
	}
}
