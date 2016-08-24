package MCEntityAnimator.gui;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class GuiAnimationMainMenu extends GuiMainMenu
{

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		super.initGui();

		int i = this.height / 4 + 48;
		int j = 24;

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(10, this.width / 2 - 100, i, "Load Animator"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 72 + 12, I18n.format("menu.quit", new Object[0])));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 72 + 12));
	}

	@Override
	protected void actionPerformed(GuiButton button) 
	{
		super.actionPerformed(button);

		if(button.id == 10)
		{
			String s = "mcea_animation_world";
			File dir = new File(FMLClientHandler.instance().getSavesDir(), s);

			if(!dir.exists())
			{
				System.out.println("No animation world found, creating a new one.");
				
				WorldType.worldTypes[1].onGUICreateWorldPress();

				WorldSettings.GameType gametype = WorldSettings.GameType.getByName("creative");
				WorldSettings worldsettings = new WorldSettings(0, gametype, false, false, WorldType.worldTypes[1]);
				worldsettings.enableCommands();

				this.mc.launchIntegratedServer(s, s, worldsettings);
			}
			else
			{
				System.out.println("Animation world found, loading.");
				FMLClientHandler.instance().tryLoadExistingWorld(null, s, s);
			}
		}

	}


}
