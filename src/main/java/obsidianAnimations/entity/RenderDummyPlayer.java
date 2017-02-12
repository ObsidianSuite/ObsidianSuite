package obsidianAnimations.entity;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.RenderObj;

public class RenderDummyPlayer extends RenderObj
{
	
	public RenderDummyPlayer()
	{
		super(new ModelDummyPlayer(), new ResourceLocation("mod_obsidian_animations:models/player/player.png"));
	}

}
