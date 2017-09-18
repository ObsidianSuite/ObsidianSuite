package com.dabigjoe.obsidianAPI.render.player;

import com.dabigjoe.obsidianAPI.render.IRenderAnimated;
import com.dabigjoe.obsidianAPI.render.ModelAnimated;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerAnimated extends RenderPlayer implements IRenderAnimated
{
	
	private ModelAnimatedPlayer modelAnimatedPlayer;
	
	public RenderPlayerAnimated(ModelAnimatedPlayer modelAnimatedPlayer)
	{
		super(Minecraft.getMinecraft().getRenderManager());
		this.modelAnimatedPlayer = modelAnimatedPlayer;
		this.mainModel = modelAnimatedPlayer;
	}
	
	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer player) 
	{
		return modelAnimatedPlayer.getTexture(player);
	}

	@Override
	public ModelAnimated getModel() {
		return modelAnimatedPlayer;
	}

}
