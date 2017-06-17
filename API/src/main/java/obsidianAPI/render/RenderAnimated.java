package obsidianAPI.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderAnimated extends RenderLiving
{

	private ModelObj modelObj;
	
	public RenderAnimated(ModelObj modelObj)
	{
		//TODO shadow size?
		super(modelObj, 1.0F);
		this.modelObj = modelObj;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) 
	{
		return modelObj.getTexture();
	}

}
