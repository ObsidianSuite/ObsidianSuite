package MCEntityAnimator.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;
import MCEntityAnimator.gui.sequence.GuiAnimationSequenceMain;
import MCEntityAnimator.gui.stance.GuiAnimationStanceMain;
import MCEntityAnimator.render.objRendering.PartObj;


public class GuiAnimationHome extends GuiScreen 
{
	int posX;
	int posY;
	private String entityName;
	private int init = 0;

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_home.png");

	public GuiAnimationHome(String par0String)
	{
		this.mc = Minecraft.getMinecraft();
		this.entityName = par0String;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		this.posX = (this.width - 80)/2;
		this.posY = (this.height - 110)/2;
		this.updateButtons();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, posX + 5, posY + 19, 70, 20, "Parenting"));
		this.buttonList.add(new GuiButton(1, posX + 5, posY + 41, 70, 20, "Sequences"));
		this.buttonList.add(new GuiButton(2, posX + 5, posY + 63, 70, 20, "Stances"));
		this.buttonList.add(new GuiButton(3, posX + 5, posY + 85, 70, 20, "Distribution"));
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	public void actionPerformed(GuiButton button)
	{	
		if(init > 4)
		{
			switch(button.id)
			{
			case 0: mc.displayGuiScreen(new GuiAnimationParenting(entityName)); break;
			case 1: mc.displayGuiScreen(new GuiAnimationSequenceMain(entityName)); break;
			case 2: mc.displayGuiScreen(new GuiAnimationStanceMain(entityName)); break;
			case 3: mc.displayGuiScreen(new GuiImportExport(entityName)); break;
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(texture);		
		Util.drawCustomGui(posX, posY, 80, 110, 0);

		this.drawCenteredString(this.fontRendererObj, entityName, posX + 40, posY + 6, 0xffff0000);

		super.drawScreen(par1, par2, par3);

		if(init < 5)
		{
			init += 1;
		}
	}
}



