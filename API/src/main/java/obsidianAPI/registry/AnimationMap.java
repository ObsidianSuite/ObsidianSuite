package obsidianAPI.registry;

import java.util.HashMap;
import java.util.Map;

import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.animation.wrapper.IAnimationWrapper;

/**
 * A class for storing animation sequences.
 * Animation sequences are stored in a map with a string key value.
 */
class AnimationMap 
{
	
	//Map to store animations
	private Map<String, IAnimationWrapper> map;
	
	AnimationMap()
	{
		map = new HashMap<String, IAnimationWrapper>();
	}
	
	/**
	 * Registers an animation by adding it to the map with a specific binding.
	 * Can fail is file doesn't exist at resource location.
	 */
	void registerAnimation(String binding, IAnimationWrapper wrapper)
	{
		map.put(binding, wrapper);
	}
	
	AnimationSequence getAnimation(String binding)
	{
		//TODO error handling here if wrapper is null. Should return null but maybe notify in debug?
		IAnimationWrapper wrapper = map.get(binding);
		return wrapper != null ? wrapper.getAnimation() : null;
	}
	
	IAnimationWrapper getAnimationWrapper(String binding)
	{
		//TODO error handling here if wrapper is null. Should return null but maybe notify in debug?
		return map.get(binding);
	}
	
}
