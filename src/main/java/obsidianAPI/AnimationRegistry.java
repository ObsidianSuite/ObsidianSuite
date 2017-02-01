package obsidianAPI;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class AnimationRegistry 
{
	
	//Map between entity type and the corresponding map of animations.
	private static Map<String, AnimationMap> entityMap = new HashMap<String, AnimationMap>();
	
	public static void registerEntity(String entityType)
	{
		entityMap.put(entityType, new AnimationMap());
	}
	
	public static void registerAnimation(String entityType)
	{
		entityMap.get(entityType).registerAnimation();
	}
	
}
