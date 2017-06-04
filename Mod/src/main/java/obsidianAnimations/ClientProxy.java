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
	
	private ResourceLocation obsidianPlayerModelRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.obm");
	private ResourceLocation obsidianPlayerTextureRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.png");
	
	public void registerRendering()
	{
		RenderDummyPlayer dummyPlayerRenderer = new RenderDummyPlayer(FileLoader.loadModelFromResources("DummyPlayer", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelDummyPlayer.class));
		RenderPlayerAnimated playerRenderer = new RenderPlayerAnimated(FileLoader.loadModelFromResources("Player", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelAnimatedPlayer.class));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, dummyPlayerRenderer);
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, playerRenderer);
	}
}

