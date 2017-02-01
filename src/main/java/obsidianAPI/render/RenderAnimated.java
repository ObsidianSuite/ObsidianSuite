package obsidianAPI.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderAnimated extends RenderLiving
{

	private ResourceLocation texture;
	
	public RenderAnimated(ModelAnimated model, ResourceLocation texture)
	{
		//TODO shadow size?
		super(model, 1.0F);
		this.texture = texture;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) 
	{
		return texture;
	}

}
