package MCEntityAnimator;

import java.io.IOException;

import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiAnimationMainMenu;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
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
			if(event.gui instanceof GuiMainMenu && !updateChecked && ServerAccess.canConnect())
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{    	    	
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && !(Minecraft.getMinecraft().currentScreen instanceof GuiAnimationMainMenu))
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationMainMenu());
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