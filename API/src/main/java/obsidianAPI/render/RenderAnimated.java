package obsidianAPI.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderAnimated extends RenderLiving implements IRenderAnimated
{

	private ModelAnimated model;
	
	public RenderAnimated(ModelAnimated model)
	{
		super(model, 1.0F);
		this.model = model;
	}
	
	@Override
	public ModelAnimated getModel() {
		return model;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return model.getTexture(entity);
	}

}
