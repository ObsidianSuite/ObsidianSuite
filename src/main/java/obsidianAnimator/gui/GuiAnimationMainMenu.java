package obsidianAnimator.gui;

import java.io.File;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class GuiAnimationMainMenu extends GuiMainMenu
{
	
	private static final ResourceLocation titleRL = new ResourceLocation("mod_obsidian_animator:gui/obsidian_animator_title.png");
    
	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	super.initGui();
    	
        int i = this.height / 4 + 24;
		int j = 24;

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(10, this.width / 2 - 100, i + 60, "Load Animator"));
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i + 60 + 24, "Options"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 60 + 24*2, "Quit"));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 60 + 24*2));
    }
	
    @Override
    protected void actionPerformed(GuiButton button)
    {
    	super.actionPerformed(button);

        if(button.id == 10)
		{
			String s = "animation_world";
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

	@Override
	public void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);
		
		this.mc.getTextureManager().bindTexture(titleRL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        int texWidth = 256;
        int texHeight = 130;
        int xPos = this.width / 2 - texWidth / 2;
        int yPos = 110;
        
        this.drawTexturedModalRect(xPos, yPos, 0, 0, texWidth, texHeight);    
	}

}
