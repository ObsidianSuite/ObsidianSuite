package obsidianAnimations;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{	
	
	public void registerRendering()
	{
        RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, new RenderDummyPlayer());
	}
}

