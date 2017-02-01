package obsidianAnimations;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.RenderObj;

public class RenderDummyPlayer extends RenderObj
{

	private static final ResourceLocation modelRL = new ResourceLocation("mod_obsidian_animations:models/player/player.obm");
	private static final ResourceLocation textureRL = new ResourceLocation("mod_obsidian_animations:models/player/player.png");
	
	public RenderDummyPlayer() throws IOException 
	{
		super(new ModelAnimated("dummy", modelRL, textureRL), textureRL);
	}

}
