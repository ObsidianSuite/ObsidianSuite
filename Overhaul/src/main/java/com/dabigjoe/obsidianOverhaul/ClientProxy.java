package com.dabigjoe.obsidianOverhaul;

import com.dabigjoe.obsidianAPI.file.importer.FileLoader;
import com.dabigjoe.obsidianOverhaul.entity.saiga.EntitySaiga;
import com.dabigjoe.obsidianOverhaul.entity.saiga.ModelSaiga;
import com.dabigjoe.obsidianOverhaul.entity.saiga.RenderSaiga;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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

