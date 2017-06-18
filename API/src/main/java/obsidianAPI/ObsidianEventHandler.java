package obsidianAPI;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import obsidianAPI.registry.AnimationRegistry;

public class ObsidianEventHandler 
{

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		if(AnimationRegistry.isRegisteredClass(e.entity.getClass()))
			e.entity.registerExtendedProperties("Animation", new EntityAnimationProperties());
	}
	
	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void onEntityUpdate(LivingUpdateEvent e) {
		System.out.println("Running entity update event");
	}

}
