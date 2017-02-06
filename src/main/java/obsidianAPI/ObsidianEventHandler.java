package obsidianAPI;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import obsidianAPI.registry.AnimationRegistry;

public class ObsidianEventHandler 
{

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		if(AnimationRegistry.isRegisteredClass(e.entity.getClass()))
			e.entity.registerExtendedProperties("Animation", new EntityAnimationProperties());
	}
	
}
