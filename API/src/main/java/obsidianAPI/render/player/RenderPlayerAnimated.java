package obsidianAPI.render.player;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerAnimated extends RenderPlayer
{
	
	private ModelAnimatedPlayer modelAnimatedPlayer;
	
	public RenderPlayerAnimated(ModelAnimatedPlayer modelAnimatedPlayer)
	{
		super();
		this.modelAnimatedPlayer = modelAnimatedPlayer;
		this.mainModel = modelAnimatedPlayer;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) 
	{
		return modelAnimatedPlayer.getTexture();
	}

}
