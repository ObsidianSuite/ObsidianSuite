package MCEntityAnimator;

import java.io.IOException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class EventHandler 
{
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
	    if (event.entity instanceof EntityPlayer)
	    {
	         event.entity.registerExtendedProperties("ExtendedPropertiesHerdAnimal", new ExtendedPropertiesAnimation());
	    }
	}
	
	@SubscribeEvent
	
	public void onGuiOpen(GuiOpenEvent event)
	{
		try 
		{
			new Updater().checkForUpdate(MCEA_Main.version);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}