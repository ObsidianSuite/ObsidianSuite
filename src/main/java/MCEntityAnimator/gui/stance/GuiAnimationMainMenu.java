package MCEntityAnimator.gui.stance;

import java.io.File;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiModList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

public class GuiAnimationMainMenu extends GuiMainMenu
{
	
	private String mceaSplashText = "The official ProjectXY Animator!";
    private static final ResourceLocation mceaTitleTexture = new ResourceLocation("mod_MCEA:gui/mcea_title.png");

	
	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	super.initGui();
    	
        int i = this.height / 4 + 48;
		int j = 24;

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(10, this.width / 2 - 100, i + 60, "Load Animator"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 72 + 12, "Quit"));
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

	@Override
	public void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);
		
		this.mc.getTextureManager().bindTexture(mceaTitleTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        short short1 = 274;
        int k = this.width / 2 - short1 / 2;
        byte b0 = 30;
        
        this.drawTexturedModalRect(k + 50, b0 + 70, 0, 0, 170, 50);
        
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 155.0F, 0.0F);
        //GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        f1 = f1 * 100.0F / (float)(this.fontRendererObj.getStringWidth(this.mceaSplashText) + 32);
        GL11.glScalef(f1, f1, f1);
        this.drawCenteredString(this.fontRendererObj, this.mceaSplashText, 0, 0, 0xFF0000);
        GL11.glPopMatrix();
        
        
	}

}
