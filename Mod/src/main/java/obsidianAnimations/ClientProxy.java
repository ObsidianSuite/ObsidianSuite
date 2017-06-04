package obsidianAnimations;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.file.importer.FileLoader;
import obsidianAPI.render.player.ModelAnimatedPlayer;
import obsidianAPI.render.player.RenderPlayerAnimated;
import obsidianAnimations.entity.EntityDummyPlayer;
import obsidianAnimations.entity.ModelDummyPlayer;
import obsidianAnimations.entity.RenderDummyPlayer;

public class ClientProxy extends CommonProxy
{	
	
	private ResourceLocation obsidianPlayerResLoc = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.obm");

	
	public void registerRendering()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, new RenderDummyPlayer(FileLoader.loadModelFromResource("DummyPlayer", obsidianPlayerResLoc, ModelDummyPlayer.class)));
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerAnimated(FileLoader.loadModelFromResource("Player", obsidianPlayerResLoc, ModelAnimatedPlayer.class)));
	}
}

