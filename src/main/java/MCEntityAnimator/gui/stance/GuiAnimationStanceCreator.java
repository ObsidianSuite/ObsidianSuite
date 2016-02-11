package MCEntityAnimator.gui.stance;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationStance;
import MCEntityAnimator.gui.GuiCheckBox;
import MCEntityAnimator.gui.GuiInventoryChooseItem;
import MCEntityAnimator.gui.GuiSlider;
import MCEntityAnimator.gui.sequence.GuiAnimationSequenceNew;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;


public class GuiAnimationStanceCreator extends GuiScreen 
{
	int posX;
	int posY;

	String entityName;
	EntityLivingBase entityToRender;
	ModelObj entityModel;
	Block blockToRender;

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/stance_creator.png");
	private static final ResourceLocation blockTexture = new ResourceLocation("mod_MCEA:gui/grass.png");

	private int scaleModifier = 0;
	private int horizontalPan = 0;
	private int verticalPan = 0;
	private int horizontalRotation = 0;
	private int verticalRotation = 0;

	private String popUpString = "";
	private int popUpTime = 0;
	private int popUpColour;

	private AnimationStance stance;

	private int xReference = 0;

	private boolean boolGround = false;
	private boolean boolShield = false;

	private boolean showPositionSliders = false;
	private boolean showParts = false;

	private ArrayList<String> parts = new ArrayList<String>();
	private String currentPartName = "";
	private int listOffset = 0;
	private int listOffset2 = 0;
	private int lastButton = 0;

	double xPosSlider = 0.0D;
	double yPosSlider = 0.0D;
	double zPosSlider = 0.0D;

	private boolean rotationLocked = false;
	private boolean rotationLockPressed = false;
	private float lockX;
	private float lockY;

	private DecimalFormat df = new DecimalFormat("#.##");
	private RenderBlocks renderBlocks = new RenderBlocks();

	private boolean boolDelete = false;

	public GuiAnimationStanceCreator(String par0Str, AnimationStance animationStance, boolean newStance) 
	{
		this(par0Str, animationStance, new EntityObj(Minecraft.getMinecraft().theWorld, par0Str), newStance);
	}

	public GuiAnimationStanceCreator(String par0Str, AnimationStance par1Stance, EntityLivingBase par2EntityLivingBase, boolean newStance)
	{
		this.mc = Minecraft.getMinecraft();
		entityName = par0Str;
		entityToRender = par2EntityLivingBase;
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(par0Str);
		blockToRender = new BlockStone();
		blockToRender.setBlockBounds(0.0F, 1.0F, 0.0F, 4.0F, 0.9F, 4.0F);
		stance = par1Stance;
		parts.add("entitypos");
		for(PartObj obj : entityModel.parts)
		{
			parts.add(obj.getName());
			obj.setToOriginalRotation();
			if(newStance)
			{
				stance.setRotation(obj.getName(), obj.getOriginalRotation());
			}
		}
		this.updateStance();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		this.posX = (this.width - 384)/2;
		this.posY = (this.height - 255)/2;
		setup();
		this.updateButtons();
	}
	
	public void setup()
	{
		String setup = AnimationData.getStanceSetup(entityName);
		if(setup != null)
		{
			String[] split = setup.split(",");
			horizontalPan = Integer.parseInt(split[0]);
			verticalPan = Integer.parseInt(split[1]);
			scaleModifier = Integer.parseInt(split[2]);
		}
	}
	
	public void saveSetup()
	{
		AnimationData.setStanceSetup(entityName, horizontalPan + "," + verticalPan + "," + scaleModifier);
	}
	
	@Override
	public void onGuiClosed()
	{
		saveSetup();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, posX + 195, posY + 4, 182, 20, "Choose Part"));
		this.buttonList.add(new GuiCheckBox(33, posX + 260, posY + 137, boolShield));
		this.buttonList.add(new GuiButton(21, posX + 195, posY + 26, 182, 20, "Reset Rotation"));
		this.buttonList.add(new GuiButton(22, posX + 195, posY + 48, 182, 20, "Choose Prop"));
		if(!this.entityName.equals("Human"))
		{
			Util.getButtonFromID(22, this.buttonList).enabled = false;
		}

		if(boolDelete)
		{
			this.buttonList.add(new GuiButton(48, posX + 195, posY + 70, 90, 20, "Yes"));
			this.buttonList.add(new GuiButton(50, posX + 287, posY + 70, 90, 20, "No"));
		}
		else
		{
			this.buttonList.add(new GuiButton(23, posX + 195, posY + 70, 182, 20, "Delete Stance"));
		}

		this.buttonList.add(new GuiButton(24, posX + 195, posY + 92, 182, 20, "Export stance to sequence"));
		this.buttonList.add(new GuiButton(25, posX + 195, posY + 114, 182, 20, "Back"));
		this.buttonList.add(new GuiSlider(17, (int) Math.round((posX + 286 + xPosSlider)), posY + 193));
		this.buttonList.add(new GuiSlider(18, (int) Math.round((posX + 286 + yPosSlider)), posY + 210));
		this.buttonList.add(new GuiSlider(19, (int) Math.round((posX + 286 + zPosSlider)), posY + 227));	

		if(showParts)
		{
			if(parts.size() > 10)
			{
				this.buttonList.add(new GuiButton(4, posX + 380, 2, 50, 20, "^"));
				this.buttonList.add(new GuiButton(5, posX + 380, 233, 50, 20, "V"));
			}
			int max = parts.size() > 10 ? 10 : parts.size();	
			for(int i = 0; i < max; i++)
			{
				this.buttonList.add(new GuiButton(6+i, posX + 380, 23 + 21*i, 50, 20, parts.get(i + listOffset)));
			}
			this.buttonList.add(new GuiButton(34, posX + 328, 117, 50, 20, "Close"));
		}
	}

	public void actionPerformed(GuiButton button)
	{	
		this.lastButton = button.id;
		switch(button.id)
		{
		case 0: showParts = true; this.updateButtons(); break;
		case 4:
			if(listOffset > 0)
			{
				listOffset--;
			}
			this.updateButtons();
			break;
		case 5:
			if(listOffset < parts.size() - 10)
			{
				listOffset++;
			}
			this.updateButtons();
			break;
		case 21: this.horizontalPan = 0; this.horizontalRotation = 0; this.verticalPan = 0; this.verticalRotation = 0; break;
		case 22: this.mc.displayGuiScreen(new GuiInventoryChooseItem(this, (EntityObj) this.entityToRender)); break;
		case 23: boolDelete = true; System.out.println("Delete pressed"); break;
		case 24: mc.displayGuiScreen(new GuiAnimationSequenceNew(entityName, this.stance)); break;
		case 25: mc.displayGuiScreen(new GuiAnimationStanceList(entityName, AnimationData.getStances(entityName))); break;
		case 30:
			if(listOffset2 < parts.size() - 12)
			{
				listOffset2++;
			}
			this.updateButtons();
			break;
		case 31:
			if(listOffset2 > 0)
			{
				listOffset2--;
			}
			this.updateButtons();
			break;
		case 33: GuiCheckBox checkBox2 = (GuiCheckBox) button; checkBox2.isChecked = !checkBox2.isChecked; boolShield = checkBox2.isChecked; break; 
		case 34: this.showParts = false; break;
		case 48: AnimationData.deleteStance(entityName, this.stance); mc.displayGuiScreen(new GuiAnimationStanceList(entityName, AnimationData.getStances(entityName))); System.out.println("Yes pressed"); break;
		case 50: boolDelete = false; System.out.println("No pressed"); break;
		}
		if(button.id > 5 && button.id < 17)
		{
			this.currentPartName = button.displayString;
			this.showParts = false;
			if(button.displayString.equals("entitypos"))
			{
				float[] position = this.stance.getRotation(button.displayString).clone();
				this.xPosSlider = position[0]*50.0F;
				this.yPosSlider = position[1]*50.0F;
				this.zPosSlider = position[2]*50.0F;
			}
			else
			{
				float[] rotation = this.stance.getRotation(button.displayString).clone();
				this.xPosSlider = rotation[0] * 58.0D / Math.PI;
				this.yPosSlider = rotation[1] * 58.0D / Math.PI;
				this.zPosSlider = rotation[2] * 58.0D / Math.PI;
			}
			showPositionSliders = true;
			this.showParts = false;
		}
		this.updateButtons();
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		switch(par2)
		{
		case Keyboard.KEY_LEFT:
			horizontalPan -= 3;
			break;
		case Keyboard.KEY_RIGHT:
			horizontalPan += 3;
			break;
		case Keyboard.KEY_UP:
			verticalPan -= 3;
			break;
		case Keyboard.KEY_DOWN:
			verticalPan += 3;
			break;	
		case Keyboard.KEY_A:
			horizontalRotation += 10;
			break;
		case Keyboard.KEY_D:
			horizontalRotation -= 10;
			break;
		case Keyboard.KEY_S:
			verticalRotation += 10;
			break;
		case Keyboard.KEY_W:
			verticalRotation -= 10;
			break;	
		case Keyboard.KEY_L:
			this.rotationLocked = !this.rotationLocked;
			this.rotationLockPressed = this.rotationLocked;
			if(rotationLocked){this.popUp("Locked rotation", 0xFF00FF00);} 
			else{this.popUp("Unlocked rotation", 0xFFFF0000);}
			break;	
		case Keyboard.KEY_B:
			this.boolGround = !this.boolGround;
			if(boolGround){this.popUp("Showing base", 0xFF00FF00);} 
			else{this.popUp("Hiding base", 0xFFFF0000);}
			break;	
		case Keyboard.KEY_T:
			this.entityModel.renderWithTexture = !this.entityModel.renderWithTexture;
			break;
		}
		super.keyTyped(par1, par2);
	}

	@Override
	public void handleMouseInput()
	{
		scaleModifier += Mouse.getEventDWheel()/40;
		super.handleMouseInput();
	}

	@Override
	protected void mouseClickMove(int par0, int par1, int par2, long par3long) 
	{
		if(currentPartName != "")
		{
			double d = 0.0D;
			if(par0 <= posX + 286 - 58.0D)
			{
				d = -58.0D;
			}
			else if(par0 >= posX + 286 + 58.0D)
			{
				d = 58.0D;
			}
			else
			{
				d = par0 - (posX + 286);
			}
			switch(lastButton)
			{
			case 17: xPosSlider = d; break; 
			case 18: yPosSlider = d; break; 
			case 19: zPosSlider = d; break; 
			}
			this.updateStance();
		}
		this.updateButtons();
	}


	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void popUp(String string, int colour) 
	{
		this.popUpString = string;
		this.popUpColour = colour;
		this.popUpTime = 200;
	}

	private void updateStance()
	{
		if(currentPartName != "")
		{
			if(currentPartName.equals("entitypos"))
			{
				stance.setRotation(currentPartName, new float[]{(float) (xPosSlider/50.0F), (float) (yPosSlider/50.0F), (float) (zPosSlider/50.0F)});
			}
			else
			{
				stance.setRotation(currentPartName, new float[]{(float) (xPosSlider/58.0F * Math.PI), (float) (yPosSlider/58.0F * Math.PI), (float) (zPosSlider/58.0F * Math.PI)});
			}
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(texture);		
		Util.drawCustomGui(posX, posY, 384, 255, 0);		

		for(PartObj obj : this.entityModel.parts)
		{
			obj.setRotation(stance.getRotation(obj.getName()));
		}

		float[] rot = stance.getRotation("entitypos");
		entityToRender.posX = rot[0];
		entityToRender.posY = rot[1];
		entityToRender.posZ = rot[2];

		if(entityToRender != null)
		{
			int scale = 50 + scaleModifier;

			if(rotationLocked)
			{
				if(this.rotationLockPressed)
				{
					lockX = (float)(posX + 95) - par1;
					lockY = (float)(posY + 60) - par2;
					this.rotationLockPressed = false;
				}
				if(boolGround)
				{
					renderBlockIntoGui(posX + 100 + horizontalPan, posY + 200 + scaleModifier/2 + verticalPan, scale, lockX, lockY, blockToRender); 
				}
				renderEntityIntoGui(posX + 100 + horizontalPan, posY + 200 + scaleModifier/2 + verticalPan, scale, lockX, lockY, entityToRender); 
			}
			else
			{
				if(boolGround)
				{
					renderBlockIntoGui(posX + 100 + horizontalPan, posY + 200 + scaleModifier/2 + verticalPan, scale, (float)(posX + 95) - par1, (float)(posY + 60) - par2, blockToRender);
				}
				renderEntityIntoGui(posX + 100 + horizontalPan, posY + 200 + scaleModifier/2 + verticalPan, scale, (float)(posX + 95) - par1, (float)(posY + 60) - par2, entityToRender);
			}
		}
		
		if(this.popUpTime > 0)
		{
			this.drawCenteredString(this.fontRendererObj, popUpString, posX + 287, posY + 152, popUpColour);
			popUpTime -= 1;
		}

		fontRendererObj.drawString("Shield", posX + 272, posY + 138, 0);
		fontRendererObj.drawString("W/A/S/D-Rotate    Arrow Keys-Move", posX + 4, posY + 4, 0);
		fontRendererObj.drawString("Scroll-Zoom B-Base T-Txtr L-Lock", posX + 4, posY + 14, 0);

		if(this.currentPartName != "")
		{
			this.fontRendererObj.drawString("Current Part: " + this.currentPartName, posX + 240, posY + 167, 0);
		}
		else
		{
			this.fontRendererObj.drawString("Current Part: - ", posX + 240, posY + 167, 0);
		}



		if(this.currentPartName.equals("entitypos"))
		{
			this.fontRendererObj.drawString("X: " + df.format(xPosSlider/50.0F), posX + 273, posY + 201, 0);
			this.fontRendererObj.drawString("Y: " + df.format(yPosSlider/50.0F), posX + 273, posY + 218, 0);
			this.fontRendererObj.drawString("Z: " + df.format(zPosSlider/50.0F), posX + 273, posY + 235, 0);
		}
		else
		{
			this.fontRendererObj.drawString("X: " + df.format(xPosSlider/58.0F * Math.PI), posX + 273, posY + 201, 0);
			this.fontRendererObj.drawString("Y: " + df.format(yPosSlider/58.0F * Math.PI), posX + 273, posY + 218, 0);
			this.fontRendererObj.drawString("Z: " + df.format(zPosSlider/58.0F * Math.PI), posX + 273, posY + 235, 0);
		}

		boolean flag1 = true;
		if(this.showParts)
		{
			for(int k = 0; k < 10; k++)
			{
				GuiButton b = Util.getButtonFromID(k + 6, this.buttonList);
				if(b != null && par1 > b.xPosition && par1 < b.xPosition + b.width && par2 > b.yPosition && par2 < b.yPosition + b.height)
				{
					entityModel.hightlightPart(Util.getPartFromName(b.displayString, entityModel.parts), true);		
					flag1 = false;
					break;
				}

			}
		}

		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Renders an entity into a gui. Parameters - xpos, ypos, scale, rotx, roty, entity.
	 */
	private void renderEntityIntoGui(int par0, int par1, float par2, float par3, float par4, EntityLivingBase par5EntityLivingBase)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par0, (float)par1, 50.0F);
		GL11.glScalef((float)(-par2), (float)par2, (float)par2);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(par4 / 40.0F))) * 20.0F + verticalRotation, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(par3 / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);
		GL11.glTranslated(par5EntityLivingBase.posX, par5EntityLivingBase.posY, par5EntityLivingBase.posZ);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(par5EntityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	/**
	 * Renders an entity into a gui. Parameters - xpos, ypos, scale, rotx, roty, entity.
	 */
	private void renderBlockIntoGui(int par0, int par1, float par2, float par3, float par4, Block par5Block)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par0, (float)par1, 50.0F);
		GL11.glScalef((float)(-par2), (float)par2, 0.01F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(par4 / 40.0F))) * 20.0F + verticalRotation, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(par3 / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);
		GL11.glTranslatef(-1.5F, -0.49F, 1.5F);
		System.out.println("X: " + par3);
		System.out.println("Y: " + par4);
		this.mc.getTextureManager().bindTexture(blockTexture);
		GL11.glDepthMask(false);
		blockToRender.setBlockBounds(0.5F + ((par2 - 50)/par2), 1.0F, 0.5F + ((par2 - 50)/par2), 3.5F - ((par2 - 50)/par2), 0.9F, 3.5F - ((par2 - 50)/par2)); 
		renderBlocks.renderBlockAsItem(par5Block, 0, 1.0F);
		GL11.glDepthMask(true);
		this.mc.getTextureManager().bindTexture(texture);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	public boolean shouldRenderShield()
	{
		return this.boolShield;
	}

}


