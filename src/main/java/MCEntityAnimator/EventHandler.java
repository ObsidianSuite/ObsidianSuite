package MCEntityAnimator;

import java.io.IOException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class EventHandler 
{

	boolean updateChecked = false;
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		try 
		{
			if(event.gui instanceof GuiMainMenu && !updateChecked)
			{
				new Updater().checkForUpdate(MCEA_Main.version);
				updateChecked = true;
			}
				
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			event.entity.registerExtendedProperties("ExtendedPropertiesAnimation", new ExtendedPropertiesAnimation());
		}
	}
}