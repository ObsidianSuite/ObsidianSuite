package MCEntityAnimator.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
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
import MCEntityAnimator.render.objRendering.Bend;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;


public class GuiAnimationParenting extends GuiScreen 
{
	int posX;
	int posY;
	private boolean boolRemove = false;
	private boolean boolParent = false;

	String entityName;
	EntityLivingBase entityToRender;
	ModelObj entityModel;
	String currentPartName;
	String parentPartName;
	ArrayList<String> partNames = new ArrayList<String>();

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_parenting.png");

	private int listOffset1 = 0;
	private int listOffset2 = 0;

	private int scaleModifier = 0;
	private int horizontalPan = 0;
	private int verticalPan = 0;
	private int horizontalRotation = 0;
	private int verticalRotation = 0;
	private boolean rotationLocked = false;
	private boolean rotationLockPressed = false;
	private float lockX;
	private float lockY;

	private String popUpString = "";
	private int popUpTime = 0;
	private int popUpColour;
	
	public GuiAnimationParenting(String par0Str)
	{
		this.mc = Minecraft.getMinecraft();
		entityName = par0Str;
		entityToRender = new EntityObj(mc.theWorld, entityName);
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(entityName);
		for(PartObj obj : entityModel.parts)
		{
			partNames.add(obj.getName());
			obj.setToOriginalRotation();
		}
		currentPartName = partNames.get(0);
		entityModel.currentPart = Util.getPartFromName(this.currentPartName, entityModel.parts);
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
		String setup = AnimationData.getParentingSetup(entityName);
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
		AnimationData.setParentingSetup(entityName, horizontalPan + "," + verticalPan + "," + scaleModifier);
	}
	
	@Override
	public void onGuiClosed()
	{
		saveSetup();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, posX + 132, posY + 10, 50, 20, "Clear"));
		this.buttonList.add(new GuiButton(1, posX + 202, posY + 10, 50, 20, "Done"));
		if(partNames.size() > 10)
		{
			this.buttonList.add(new GuiButton(17, posX + 5, 2, 50, 20, "^"));
			this.buttonList.add(new GuiButton(28, posX + 5, 233, 50, 20, "V"));
		}
		int max = partNames.size() > 10 ? 10 : partNames.size();	
		for(int i = 0; i < max; i++)
		{
			if(currentPartName.equals(partNames.get(i + listOffset1)))
			{
				this.buttonList.add(new GuiButton(18+i, posX + 5, 23 + 21*i, 50, 20, partNames.get(i + listOffset1) + "*"));
			}
			else
			{
				this.buttonList.add(new GuiButton(18+i, posX + 5, 23 + 21*i, 50, 20, partNames.get(i + listOffset1)));
			}
		}
		if(partNames.size() > 10)
		{
			this.buttonList.add(new GuiButton(29, posX + 328, 2, 50, 20, "^"));
			this.buttonList.add(new GuiButton(40, posX + 328, 233, 50, 20, "V"));
		}
		for(int i = 0; i < max; i++)
		{
			this.buttonList.add(new GuiButton(30+i, posX + 328, 23 + 21*i, 50, 20, partNames.get(i + listOffset2)));
		}
		if(boolRemove)
		{
			this.buttonList.add(new GuiButton(41, posX + 132, 200, 50, 20, "Yes"));
			this.buttonList.add(new GuiButton(42, posX + 202, 200, 50, 20, "No"));
		}
		if(boolParent)
		{
			GuiButton yesButton = new GuiButton(43, posX + 97, 200, 50, 20, "Yes");
			this.buttonList.add(yesButton);
			this.buttonList.add(new GuiButton(44, posX + 167, 200, 50, 20, "No"));
			this.buttonList.add(new GuiButton(45, posX + 237, 200, 50, 20, "Cancel"));
			if(!Bend.canCreateBend(Util.getPartFromName(this.currentPartName, entityModel.parts), Util.getPartFromName(parentPartName, entityModel.parts)))
			{
				yesButton.enabled = false;
			}
			
		}
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

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	public void actionPerformed(GuiButton button)
	{	
		switch(button.id)
		{
		case 0: AnimationData.getAnipar(entityName).clear((EntityObj) entityToRender); break;
		case 1: mc.displayGuiScreen(new GuiAnimationHome(entityName)); break;
		case 17: 
			if(listOffset1 > 0)
			{
				listOffset1--;
			}
			this.updateButtons();
			break;
		case 28: 
			if(listOffset1 < partNames.size() - 10)
			{
				listOffset1++;
			}
			this.updateButtons();
			break;
		case 29: 
			if(listOffset2 > 0)
			{
				listOffset2--;
			}
			this.updateButtons();
			break;
		case 40: 
			if(listOffset2 < partNames.size() - 10)
			{
				listOffset2++;
			}
			this.updateButtons();
			break;
		case 41: AnimationData.getAnipar(entityName).unParent(Util.getPartFromName(this.currentPartName, this.entityModel.parts)); this.boolRemove = false; this.updateButtons(); break;
		case 42: this.boolRemove = false; this.updateButtons(); break;
		case 43: parent(true); break;
		case 44: parent(false); break;
		case 45:  boolParent = false; this.updateButtons(); break;
		}
		if(button.id > 17 && button.id < 28)
		{
			this.currentPartName = button.displayString;
			entityModel.currentPart = Util.getPartFromName(this.currentPartName, entityModel.parts);
			this.updateButtons();
		}
		if(button.id > 29 && button.id < 40)
		{
			prepParent(button.displayString);
		}
	}

	private void prepParent(String buttonName)
	{
		boolean flag = true;
		if(!this.currentPartName.equals(buttonName))
		{
			if(AnimationData.getAnipar(entityName).hasParent(Util.getPartFromName(this.currentPartName, entityModel.parts)))
			{
				this.popUp(this.currentPartName + " is already parented to " + AnimationData.getAnipar(entityName).getParent(Util.getPartFromName(this.currentPartName, entityModel.parts)).getName(), 0xFFFF0000);
				this.boolRemove = true;
				this.updateButtons();
				flag = false;
			}
		}
		else
		{
			this.popUp("Can't parent a part to itself", 0xFFFF0000);
			flag = false;
		}
		
		if(flag)
		{
			boolParent = true;
			parentPartName = buttonName;
			this.updateButtons();
		}
	}
	
	private void parent(boolean bend) 
	{
		entityModel.setParent(Util.getPartFromName(this.currentPartName, entityModel.parts), Util.getPartFromName(parentPartName, entityModel.parts), bend);
		this.popUp("Parented " + this.currentPartName + " to " + parentPartName, 0xFF00FF00);
		boolParent = false; 
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
		this.popUpTime = 100;
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

		if(entityToRender != null)
		{
			int scale = 50 + scaleModifier;

			if(rotationLocked)
			{
				if(this.rotationLockPressed)
				{
					lockX = (float)(posX + 190) - par1;
					lockY = (float)(posY + 25) - par2;
					this.rotationLockPressed = false;
				}
				renderEntityIntoGui(posX + 190 + horizontalPan, posY + 180 + scaleModifier/2 + verticalPan, scale, lockX, lockY, entityToRender); 
			}
			else
			{
				renderEntityIntoGui(posX + 190 + horizontalPan, posY + 180 + scaleModifier/2 + verticalPan, scale, (float)(posX + 190) - par1, (float)(posY + 25) - par2, entityToRender); 
			}
		}

		if(this.popUpTime > 0)
		{
			this.drawCenteredString(this.fontRendererObj, popUpString, posX + 190, posY + 40, popUpColour);
			popUpTime -= 1;
		}
		
		fontRendererObj.drawString("W/A/S/D-Rotate    Arrow Keys-Move", posX + 99, posY + 230, 0);
		fontRendererObj.drawString("Scroll-Zoom B-Base T-Txtr L-Lock", posX + 99, posY + 240, 0);
		
		fontRendererObj.drawString("Child", posX + 20, posY + 10, 0);
		fontRendererObj.drawString("Parent", posX + 340, posY + 10, 0);


		if(this.boolRemove)
		{
			this.drawCenteredString(this.fontRendererObj, "Unparent?", posX + 192, posY + 190, 0xFF555555);
		}
		
		if(this.boolParent)
		{
			this.drawCenteredString(this.fontRendererObj, "Parent with bend?", posX + 192, posY + 190, 0xFF555555);
		}

		entityModel.hightlightPart(Util.getPartFromName(this.currentPartName, entityModel.parts));		

		boolean flag = true;
		for(int j = 0; j < 10; j++)
		{
			GuiButton b = Util.getButtonFromID(j + 18, this.buttonList);
			if(b != null && (par1 > b.xPosition && par1 < b.xPosition + b.width && par2 > b.yPosition && par2 < b.yPosition + b.height && !this.currentPartName.equals(b.displayString.substring(0, b.displayString.length() - 1))))
			{
				entityModel.hightlightPart(Util.getPartFromName(b.displayString, entityModel.parts));		
				flag = false;
				break;
			}

			GuiButton b2 = Util.getButtonFromID(j + 30, this.buttonList);
			if(b2 != null && (par1 > b2.xPosition && par1 < b2.xPosition + b2.width && par2 > b2.yPosition && par2 < b2.yPosition + b2.height && !this.currentPartName.equals(b2.displayString.substring(0, b2.displayString.length() - 1))))
			{
				entityModel.hightlightPart(Util.getPartFromName(b2.displayString, entityModel.parts));		
				flag = false;
				break;
			}
		}
		if(flag)
		{
			entityModel.clearHighlights();
			entityModel.hightlightPart(Util.getPartFromName(this.currentPartName, entityModel.parts));		
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

}


