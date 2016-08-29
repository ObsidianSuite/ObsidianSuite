package com.nthrootsoftware.mcea.gui.stance;

import java.awt.Color;
import java.io.File;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class GuiAnimationMainMenu extends GuiMainMenu
{
	
	private String mceaSplashText = "The official ProjectXY Animator!";
    private float mceaSplashHue = 0.0F;
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
        GL11.glTranslatef((float)(this.width / 2), 154.0F, 0.0F);
        //GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        f1 = f1 * 100.0F / (float)(this.fontRendererObj.getStringWidth(this.mceaSplashText) + 32);
        GL11.glScalef(f1, f1, f1);                
        
        mceaSplashHue += 0.015F;
        if(mceaSplashHue >= 1)
        	mceaSplashHue = 0F;
        
        this.drawCenteredString(this.fontRendererObj, this.mceaSplashText, 0, 0, Color.getHSBColor(mceaSplashHue, 1, 1).getRGB());
        GL11.glPopMatrix();
        
        
	}

}
