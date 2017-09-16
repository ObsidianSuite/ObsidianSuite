package obsidianAnimator.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiAnimationMainMenu extends GuiMainMenu
{
	
    
	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	super.initGui();
    	
        int startY = this.height / 4 + 50;
        int spaceY = 22;
        
        this.buttonList.clear();
        addSingleplayerMultiplayerButtons(startY, spaceY);
		this.buttonList.add(new GuiButton(10, this.width / 2 - 100, startY + spaceY * 2, "Animator"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, startY + spaceY * 4, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, startY + spaceY * 4, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, startY + spaceY * 4));
    }
    
    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int y, int spaceY)
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, y + spaceY * 1, I18n.format("menu.multiplayer", new Object[0])));
        GuiButton realmsButton = new GuiButton(14, this.width / 2 - 100, y + spaceY * 3, I18n.format("menu.online", new Object[0]));
        GuiButton fmlModButton = new GuiButton(6, this.width / 2 - 100, y + spaceY * 3, "Mods");
        fmlModButton.x = this.width / 2 + 2;
        realmsButton.width = 98;
        fmlModButton.width = 98;
        this.buttonList.add(realmsButton);
        this.buttonList.add(fmlModButton);
    }
	
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
    	super.actionPerformed(button);

		WorldSettings worldsettings = null;
        if(button.id == 10)
		{
			String s = "animation_world";
			File dir = new File(FMLClientHandler.instance().getSavesDir(), s);

			if(!dir.exists())
			{
				System.out.println("No animation world found, creating a new one.");
				
				WorldType.WORLD_TYPES[1].onGUICreateWorldPress();

				GameType gametype = GameType.getByName("creative");
				worldsettings = new WorldSettings(0, gametype, false, false, WorldType.WORLD_TYPES[1]);
				worldsettings.enableCommands();
			}
			this.mc.launchIntegratedServer(s, s, worldsettings);
		}
    }

}