package obsidianAnimations;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import obsidianAPI.file.importer.FileLoader;
import obsidianAnimations.entity.saiga.EntitySaiga;
import obsidianAnimations.entity.saiga.ModelSaiga;
import obsidianAnimations.entity.saiga.RenderSaiga;

public class ClientProxy extends CommonProxy
{	
	private ResourceLocation saigaModelRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.obm");
	private ResourceLocation saigaTextureRL = new ResourceLocation("mod_obsidian_animations:models/Saiga.png");
	
	public void registerRendering()
	{
		RenderSaiga saigaRenderer = new RenderSaiga(FileLoader.loadModelFromResources("saiga", saigaModelRL, saigaTextureRL, ModelSaiga.class));
		RenderingRegistry.registerEntityRenderingHandler(EntitySaiga.class, saigaRenderer);
	}
}

