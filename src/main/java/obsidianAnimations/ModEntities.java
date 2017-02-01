package obsidianAnimations;

import java.util.Random;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EntityList;
import obsidianAnimator.render.entity.EntityObj;

public class ModEntities 
{

	public static void registerEntities()
	{
		registerEntity(EntityDummyPlayer.class, "DummyPlayer");
		//EntityRegistry.registerGlobalEntityID(EntityDummyPlayer.class, "DummyPlayer", EntityRegistry.findGlobalUniqueEntityId(), 0, 0);
	}

	public static void registerEntity(Class entityClass, String name)
	{
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		long seed = name.hashCode();
		Random rand = new Random(seed);
		int primaryColor = rand.nextInt() * 16777215;
		int secondaryColor = rand.nextInt() * 16777215;

		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		EntityRegistry.registerModEntity(entityClass, name, entityID, ObsidianAnimations.instance, 64, 1, true);
		EntityList.entityEggs.put(Integer.valueOf(entityID), new EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
	}

}
