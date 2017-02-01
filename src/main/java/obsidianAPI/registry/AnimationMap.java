package obsidianAPI.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import obsidianAnimator.animation.AnimationSequence;

/**
 * A class for storing animation sequences.
 * Animation sequences are stored in a map with a string key value.
 */
class AnimationMap 
{
	
	//Map to store animations
	private Map<String, AnimationSequence> map;
	
	AnimationMap()
	{
		map = new HashMap<String, AnimationSequence>();
	}
	
	/**
	 * Registers an animation by adding it to the map with a specific binding.
	 * Can fail is file doesn't exist at resource location.
	 */
	void registerAnimation(String binding, ResourceLocation resource)
	{
		//Attempt to load sequence from resource location.
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
