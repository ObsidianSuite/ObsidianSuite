package obsidianAPI;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import obsidianAPI.exceptions.UnregisteredEntityException;

public class AnimationRegistry 
{
	
	//Map between entity type and the corresponding map of animations.
	private static Map<String, AnimationMap> entityMap = new HashMap<String, AnimationMap>();
	
	/**
	 * Add an entity type to the AnimationRegistry. 
	 * This allows animations to be registered for this entity type.
	 */
	public static void registerEntity(String entityType)
	{
		entityMap.put(entityType, new AnimationMap());
	}
	
	/**
	 * Add an animation for a given entity type.
	 * The binding parameter is the string used
	 *  to access the animation.
	 * Entity must be already registered.
	 */
	public static void registerAnimation(String entityType, String binding, ResourceLocation resource) throws UnregisteredEntityException
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		entityMap.get(entityType).registerAnimation(binding, resource);
	}

}
