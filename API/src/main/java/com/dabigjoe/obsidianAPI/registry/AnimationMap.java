package com.dabigjoe.obsidianAPI.registry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.animation.wrapper.IAnimationWrapper;

/**
 * A class for storing animation sequences.
 * Animation sequences are stored in a map with a string key value.
 */
class AnimationMap 
{
	
	//Map of bindings to animation wrappers
	private Map<String, IAnimationWrapper> map;
	
	//Priority queue of animation wrappers
	private Queue<IAnimationWrapper> animationList = new PriorityQueue<IAnimationWrapper>(1, new AnimationWrapperComparator());
	
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
		animationList.add(wrapper);
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
	
    private class AnimationWrapperComparator implements Comparator<IAnimationWrapper>{

		@Override
		public int compare(IAnimationWrapper o1, IAnimationWrapper o2) {
			return o1.getPriority() - o2.getPriority();
		}
    	
    }

	public PriorityQueue<IAnimationWrapper> getAnimationListCopy() {
		return new PriorityQueue<IAnimationWrapper>(animationList);
	}
	
}
