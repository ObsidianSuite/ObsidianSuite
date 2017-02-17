package obsidianAPI.render.player;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerAnimated extends RenderPlayer
{
	
	private static final ResourceLocation texture = new ResourceLocation("mod_obsidian_animations:models/player.png");
	
	public RenderPlayerAnimated()
	{
		super();
		this.mainModel = new ModelAnimatedPlayer();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) 
	{
		return texture;
	}

}
