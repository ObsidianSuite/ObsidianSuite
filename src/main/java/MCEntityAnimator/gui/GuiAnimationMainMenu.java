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
		//        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, i + j * 1, I18n.format("menu.multiplayer", new Object[0])));
		//        GuiButton realmsButton = new GuiButton(14, this.width / 2 - 100, i + j * 2, I18n.format("menu.online", new Object[0]));
		//        GuiButton fmlModButton = new GuiButton(6, this.width / 2 - 100, i + j * 2, "Mods");
		//        fmlModButton.xPosition = this.width / 2 + 2;
		//        realmsButton.width = 98;
		//        fmlModButton.width = 98;
		//        this.buttonList.add(realmsButton);
		//        this.buttonList.add(fmlModButton);

		//		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 72 + 12, I18n.format("menu.quit", new Object[0])));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 72 + 12));



		//        Object object = this.field_104025_t;
		//
		//        synchronized (this.field_104025_t)
		//        {
		//            this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
		//            this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
		//            int j = Math.max(this.field_92023_s, this.field_92024_r);
		//            this.field_92022_t = (this.width - j) / 2;
		//            this.field_92021_u = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
		//            this.field_92020_v = this.field_92022_t + j;
		//            this.field_92019_w = this.field_92021_u + 24;
		//        }
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
