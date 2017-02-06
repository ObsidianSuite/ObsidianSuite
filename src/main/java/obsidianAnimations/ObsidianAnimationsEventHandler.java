package obsidianAnimations;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import obsidianAPI.EntityAnimationProperties;

public class ObsidianAnimationsEventHandler 
{
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		e.entity.registerExtendedProperties("Animation", new EntityAnimationProperties());
	}
	

}
