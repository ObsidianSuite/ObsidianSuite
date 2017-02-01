package obsidianAPI;

import java.util.HashMap;
import java.util.Map;

public class AnimationRegistry 
{
	
	//Map between entity type and the corresponding map of animations.
	private Map<String, AnimationMap> entityMap;
	
	public AnimationRegistry()
	{
		entityMap = new HashMap<String, AnimationMap>();
	}
	
	public void registerEntity(String entityType)
	{
		entityMap.put(entityType, new AnimationMap());
	}
}
