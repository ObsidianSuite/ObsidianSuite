package obsidianAPI.render.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.part.PartObj;

public class ModelAnimatedPlayer extends ModelAnimated
{

	private PartObj head, chestLw;
	private static final ResourceLocation modelRL = new ResourceLocation("mod_obsidian_animations:models/biped.obm");
	private static final ResourceLocation textureRL = new ResourceLocation("mod_obsidian_animations:models/player.png");
	
	public ModelAnimatedPlayer()
	{
		super("player", modelRL, textureRL);
		head = getPartObjFromName("head");
		chestLw = getPartObjFromName("chestLw");
	}
	
	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity) 
	{				
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);
		head.setValue((float) (lookY/180F*Math.PI), 0);
		chestLw.setValue((float) (-lookX/180F*Math.PI), 1);
	}

}

