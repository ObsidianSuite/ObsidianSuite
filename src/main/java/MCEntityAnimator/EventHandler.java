package MCEntityAnimator;

import java.io.IOException;

import MCEntityAnimator.gui.GuiEntityList;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EventHandler 
{

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		try 
		{
			if(event.gui instanceof GuiMainMenu)
				new Updater().checkForUpdate(MCEA_Main.version);
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
			System.out.println("Constructed");
		}
	}
}