package obsidianAnimations;

import java.io.IOException;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{	
	
	public void registerRendering()
	{
        try 
        {
			RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, new RenderDummyPlayer());
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
}

