package com.nthrootsoftware.mcea.gui.stance;
//package MCEntityAnimator.gui.stance;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.util.ResourceLocation;
//
//import org.lwjgl.opengl.GL11;
//
//import MCEntityAnimator.Util;
//import MCEntityAnimator.animation.AnimationData;
//import MCEntityAnimator.animation.AnimationStance;
//import MCEntityAnimator.gui.GuiAnimationHome;
//
//
//public class GuiAnimationStanceMain extends GuiScreen 
//{
//	int posX;
//	int posY;
//	private String entityName;
//
//	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_home.png");
//
//	public GuiAnimationStanceMain(String par0Str)
//	{
//		this.mc = Minecraft.getMinecraft();
//		this.entityName = par0Str;
//	}
//
//	/**
//	 * Adds the buttons (and other controls) to the screen in question.
//	 */
//	public void initGui()
//	{
//		super.initGui();
//		this.posX = (this.width - 80)/2;
//		this.posY = (this.height - 110)/2;
//		this.updateButtons();
//	}
//
//	public void updateButtons()
//	{
//		this.buttonList.clear();
//		this.buttonList.add(new GuiButton(0, posX + 5, posY + 19, 70, 20, "New"));
//		this.buttonList.add(new GuiButton(1, posX + 5, posY + 41, 70, 20, "Load"));
//		this.buttonList.add(new GuiButton(2, posX + 5, posY + 63, 70, 20, ""));
//		this.buttonList.add(new GuiButton(3, posX + 5, posY + 85, 70, 20, "Back"));
//
//	}
//
//	@Override
//	protected void keyTyped(char par1, int par2)
//	{
//		super.keyTyped(par1, par2);
//	}
//
//	@Override
//	public void handleMouseInput()
//	{
//		super.handleMouseInput();
//	}
//
//	/**
//	 *	Moving mouse with button pressed. mouseX, mouseY, last button clicked, time
//	 */
//	@Override
//	public void mouseClickMove(int par1, int par2, int par3, long par4)
//	{		
//		super.mouseClickMove(par1, par2, par3, par4);
//	}
//
//
//	/**
//	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
//	 */
//	public void actionPerformed(GuiButton button)
//	{	
//		switch(button.id)
//		{
//		case 0: mc.displayGuiScreen(new GuiAnimationStanceNew(entityName, new AnimationStance())); break;
//		case 1: mc.displayGuiScreen(new GuiAnimationStanceList(entityName, AnimationData.getStances(entityName))); break;
//		case 2: break;
//		case 3: mc.displayGuiScreen(new GuiAnimationHome(entityName)); break;
//		}
//	}
//
//	@Override
//	protected void mouseClicked(int mouseX, int mouseY, int button)
//	{
//		super.mouseClicked(mouseX, mouseY, button);
//	}
//
//	public boolean doesGuiPauseGame()
//	{
//		return false;
//	}
//
//	/**
//	 * Draws the screen and all the components in it.
//	 */
//	public void drawScreen(int par1, int par2, float par3)
//	{
//		this.drawDefaultBackground();
//		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//		this.mc.getTextureManager().bindTexture(texture);		
//		Util.drawCustomGui(posX, posY, 80, 110, 0);
//
//		this.drawCenteredString(this.fontRendererObj, "Stances", posX + 40, posY + 6, 0xffff0000);
//
//		super.drawScreen(par1, par2, par3);
//	}
//
//
//
//}
//
//
