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
	
	private static Map<Class, String> registeredClasses = new HashMap<Class, String>();
	
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
		registeredClasses.put(entityClass, entityType);
	}
	
	public static boolean isRegisteredClass(Class entityClass)
	{
		return getRegisteredSuperClass(entityClass) != null;
	}
	
	private static Class getRegisteredSuperClass(Class entityClass)
	{
		boolean registered = false;
		while(!registered && entityClass != null)
		{
			registered = registeredClasses.containsKey(entityClass);
			if(!registered)
				entityClass = entityClass.getSuperclass();
		}
		return entityClass;
	}
		
	public static String getEntityName(Class entityClass)
	{
		Class regClass = getRegisteredSuperClass(entityClass);
		if(regClass == null)
			throw new UnregisteredEntityException(entityClass.getName());
		return AnimationRegistry.registeredClasses.get(regClass);
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
	
	public static boolean hasIdleAnimation(String entityName) {
		if(!entityMap.containsKey(entityName))
			throw new UnregisteredEntityException(entityName);
		return entityMap.get(entityName).getAnimation("Idle") != null;
	}

}
