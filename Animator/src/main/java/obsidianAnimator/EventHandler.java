package obsidianAnimator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import obsidianAnimator.data.Persistence;
import obsidianAnimator.gui.GuiAnimationMainMenu;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;

public class EventHandler 
{

//	public static final int ANIMATOR_BUTTON_ID = ObsidianAnimator.MODNAME.hashCode();
//
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent(priority = EventPriority.LOW)
//	public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
//	{
//		if(event.gui instanceof GuiMainMenu)
//		{
//			int offsetX = 0;
//			int offsetY = 0; //24?
//					int btnX = event.gui.width / 2 - 124 + offsetX;
//					int btnY = event.gui.height / 4 + 48 + 24 * 2 + offsetY;
//					List buttonList = ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, event.gui, "buttonList");
//					while(true)
//					{
//						if(btnX < 0)
//						{
//							if(offsetY <= -48) //give up
//							{
//								btnX = 0;
//								btnY = 0;
//								break;
//							}
//							else
//							{
//								offsetX = 0;
//								offsetY -= 24;
//								btnX = event.gui.width / 2 - 124 + offsetX;
//								btnY = event.gui.height / 4 + 48 + 24 * 2 + offsetY;
//							}
//						}
//
//						Rectangle btn = new Rectangle(btnX, btnY, 20, 20);//Thanks to heldplayer for this.
//						boolean intersects = false;
//						for(int i = 0; i < buttonList.size(); i++)
//						{
//							GuiButton button = (GuiButton)buttonList.get(i);
//							if(!intersects)
//							{
//								intersects = btn.intersects(new Rectangle(button.xPosition, button.yPosition, button.width, button.height));
//							}
//						}
//
//						if(!intersects)
//						{
//							break;
//						}
//
//						btnX += 24; // move to the right to try and find a free space.
//					}
//
//					buttonList.add(new GuiButton(ANIMATOR_BUTTON_ID, btnX, btnY, 20, 20, "A"));
//		}
//	}
//
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent
//	public void onButtonPressPre(GuiScreenEvent.ActionPerformedEvent.Pre event)
//	{
//		if(event.gui instanceof GuiMainMenu && event.button.id == ANIMATOR_BUTTON_ID)
//		{
//			String s = "animation_world";
//			File dir = new File(FMLClientHandler.instance().getSavesDir(), s);
//
//			if(!dir.exists())
//			{
//				System.out.println("No animation world found, creating a new one.");
//
//				WorldType.worldTypes[1].onGUICreateWorldPress();
//
//				WorldSettings.GameType gametype = WorldSettings.GameType.getByName("creative");
//				WorldSettings worldsettings = new WorldSettings(0, gametype, false, false, WorldType.worldTypes[1]);
//				worldsettings.enableCommands();
//
//				Minecraft.getMinecraft().launchIntegratedServer(s, s, worldsettings);
//			}
//			else
//			{
//				System.out.println("Animation world found, loading.");
//				FMLClientHandler.instance().tryLoadExistingWorld(null, s, s);
//			}
//			event.setCanceled(true);
//		}
//	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{    	  		
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && !(Minecraft.getMinecraft().currentScreen instanceof GuiAnimationMainMenu))
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationMainMenu());

		if(Minecraft.getMinecraft().inGameHasFocus && Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().getIntegratedServer().worlds[0].getWorldInfo().getWorldName().equals("animation_world"))
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
			new HomeFrame().display();
		}
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		Persistence.save();
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Load event)
	{
		Persistence.load();
	}
}