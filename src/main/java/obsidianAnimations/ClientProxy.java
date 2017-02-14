package obsidianAnimations;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import obsidianAPI.render.player.RenderPlayerAnimated;
import obsidianAnimations.entity.EntityDummyPlayer;
import obsidianAnimations.entity.RenderDummyPlayer;

public class ClientProxy extends CommonProxy
{	
	
	public void registerRendering()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, new RenderDummyPlayer());
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerAnimated());
	}
}

