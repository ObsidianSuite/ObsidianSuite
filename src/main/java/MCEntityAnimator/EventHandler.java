package MCEntityAnimator;

import java.io.IOException;

import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiAnimationMainMenu;
import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiHandler;
import MCEntityAnimator.gui.animation.LoginGUI;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
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
		
		if(Minecraft.getMinecraft().inGameHasFocus)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
			if(GuiHandler.loginGUI == null)
				GuiHandler.loginGUI = new LoginGUI();
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