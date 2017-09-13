package obsidianAPI.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderAnimated extends RenderLiving implements IRenderAnimated
{

	private ModelAnimated model;
	
	public RenderAnimated(ModelAnimated model)
	{
		this(model, 1.0F);
	}
	
	public RenderAnimated(ModelAnimated model, float shadowSize)
	{
		super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
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
