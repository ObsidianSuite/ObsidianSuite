package MCEntityAnimator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(DataHandler.EXT_PROP_NAME) == null)
		{
			event.entity.registerExtendedProperties(DataHandler.EXT_PROP_NAME, new DataHandler((EntityPlayer) event.entity));
		}
	}

}