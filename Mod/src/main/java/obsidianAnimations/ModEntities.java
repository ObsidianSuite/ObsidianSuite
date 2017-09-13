package obsidianAnimations;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import obsidianAnimations.entity.saiga.EntitySaiga;

public class ModEntities 
{
	
	private static int id = 1;

	public static void registerEntities()
	{
		registerEntity(EntitySaiga.class, "Saiga");
	}

	public static void registerEntity(Class entityClass, String name)
	{
		long seed = name.hashCode();
		Random rand = new Random(seed);
		int primaryColor = rand.nextInt() * 16777215;
		int secondaryColor = rand.nextInt() * 16777215;
        EntityRegistry.registerModEntity(new ResourceLocation(ObsidianAnimations.MODID, name), entityClass, name, id++, ObsidianAnimations.instance, 64, 3, true, primaryColor, secondaryColor);
	}

}
