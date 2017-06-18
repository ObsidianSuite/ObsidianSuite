package obsidianAPI.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import obsidianAPI.ObsidianEventHandler;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import obsidianAPI.animation.wrapper.IAnimationWrapper;
import obsidianAPI.exceptions.UnregisteredEntityException;

public class AnimationRegistry 
{
		
	//Map between entity type and the corresponding map of animations.
	private static Map<String, AnimationMap> entityMap = new HashMap<String, AnimationMap>();
	
	private static Map<Class, String> registeredClasses = new HashMap<Class, String>();
	
	public static void init() {}
	
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
	public static void registerAnimation(String entityType, String binding, IAnimationWrapper wrapper)
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		entityMap.get(entityType).registerAnimation(binding, wrapper);
		wrapper.getAnimation().setName(binding);
	}
	
	public static void registerAnimation(String entityType, String binding, ResourceLocation resource, int priority, boolean loops, float transitionTime, IsActiveFunction isActiveFunction)
	{
		try {
			AnimationSequence seq = loadAnimation(resource);
			registerAnimation(entityType, binding, new FunctionAnimationWrapper(seq, priority, loops, transitionTime, isActiveFunction));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerAnimation(String entityType, String binding, ResourceLocation resource, int priority, boolean loops, IsActiveFunction isActiveFunction)
	{
		registerAnimation(entityType, binding, resource, priority, loops, 0.25F, isActiveFunction);
	}
	
	public static AnimationSequence getAnimation(String entityType, String binding)
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		return entityMap.get(entityType).getAnimation(binding);
	}
	
	public static IAnimationWrapper getAnimationWrapper(String entityType, String binding)
	{
		if(!entityMap.containsKey(entityType))
			throw new UnregisteredEntityException(entityType);
		return entityMap.get(entityType).getAnimationWrapper(binding);
	}
	
	public static PriorityQueue<IAnimationWrapper> getAnimationListCopy(String entityName) {
		if(!entityMap.containsKey(entityName))
			throw new UnregisteredEntityException(entityName);
		return entityMap.get(entityName).getAnimationListCopy();
	}
	
	public static AnimationSequence loadAnimation(ResourceLocation resource) throws IOException
	{
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
		return new AnimationSequence(CompressedStreamTools.readCompressed(res.getInputStream()));
	}

}
