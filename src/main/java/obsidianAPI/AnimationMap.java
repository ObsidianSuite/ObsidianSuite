package obsidianAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import obsidianAnimator.animation.AnimationSequence;

class AnimationMap 
{

	private Map<String, AnimationSequence> map;
	
	AnimationMap()
	{
		map = new HashMap<String, AnimationSequence>();
	}
	
	void registerAnimation(String binding, ResourceLocation resource)
	{
		try
		{
			AnimationSequence seq = loadAnimation(resource);
			map.put(binding, seq);
		}
		catch(IOException e)
		{
			System.out.println("Unable to load animation:");
			e.printStackTrace();
		}
	}
	
	private AnimationSequence loadAnimation(ResourceLocation resource) throws IOException
	{
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
		return new AnimationSequence(CompressedStreamTools.readCompressed(res.getInputStream()));
	}
	
}
