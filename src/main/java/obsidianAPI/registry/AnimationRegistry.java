package obsidianAPI.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import obsidianAPI.ObsidianEventHandler;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.exceptions.UnregisteredEntityException;

public class AnimationRegistry 
{
		
	//Map between entity type and the corresponding map of animations.
	private static Map<String, AnimationMap> entityMap = new HashMap<String, AnimationMap>();
	
	private static List<Class> registeredClasses = new ArrayList<Class>();
	
	public static void init()
	{
		ObsidianEventHandler eventHandler = new ObsidianEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			FMLCommonHandler.instance().bus().register(eventHandler);
	}
	
	public static void registerEntity(Class entityClass, String entityType)
	{
		entityMap.put(entityType, new AnimationMap());
		registeredClasses.add(entityClass);
	}
	
	public static boolean isRegisteredClass(Class entityClass)
	{
		return registeredClasses.contains(entityClass);
	}
	
	/**
	 * Add an animation for a given entity type.
	 * The binding parameter is the string used
	 *  to access the animation.
	 * Entity must be already registered.
	 */
	public static void registerAnimation(String entityType, String binding, ResourceLocation resource)
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		entityMap.get(entityType).registerAnimation(binding, resource);
	}
	
	public static AnimationSequence getAnimation(String entityType, String binding)
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		return entityMap.get(entityType).getAnimation(binding);
	}

}
