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
import obsidianAnimations.entity.saiga.EntitySaiga;
import obsidianAnimations.entity.saiga.ModelSaiga;
import obsidianAnimations.entity.saiga.RenderSaiga;
import obsidianAnimations.fnaf.EntityFreddy;
import obsidianAnimations.fnaf.ModelFreddyObsidian;
import obsidianAnimations.fnaf.RenderFreddyObsidian;

public class ClientProxy extends CommonProxy
{	
	
	private ResourceLocation obsidianPlayerModelRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.obm");
	private ResourceLocation obsidianPlayerTextureRL = new ResourceLocation("mod_obsidian_animations:models/ObsidianPlayer.png");
	private ResourceLocation saigaModelRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.obm");
	private ResourceLocation saigaTextureRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.png");
	
	private ResourceLocation freddyModel = new ResourceLocation("fnafmod:obsidian/models/Freddy.obm");
	private ResourceLocation freddyTexture = new ResourceLocation("fnafmod:obsidian/models/Freddy.png");
	
	public void registerRendering()
	{
		RenderDummyPlayer dummyPlayerRenderer = new RenderDummyPlayer(FileLoader.loadModelFromResources("DummyPlayer", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelDummyPlayer.class));
		RenderPlayerAnimated playerRenderer = new RenderPlayerAnimated(FileLoader.loadModelFromResources("player", obsidianPlayerModelRL, obsidianPlayerTextureRL, ModelAnimatedPlayer.class));
		RenderSaiga saigaRenderer = new RenderSaiga(FileLoader.loadModelFromResources("saiga", saigaModelRL, saigaTextureRL, ModelSaiga.class));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityDummyPlayer.class, dummyPlayerRenderer);
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, playerRenderer);
		RenderingRegistry.registerEntityRenderingHandler(EntitySaiga.class, saigaRenderer);
		
		ModelFreddyObsidian modelFreddy = FileLoader.loadModelFromResources("Freddy", freddyModel, freddyTexture, ModelFreddyObsidian.class);
		modelFreddy.setModelScale(0.1F);
		RenderFreddyObsidian renderFreddy = new RenderFreddyObsidian(modelFreddy);
		RenderingRegistry.registerEntityRenderingHandler(EntityFreddy.class,renderFreddy);
	}
}

