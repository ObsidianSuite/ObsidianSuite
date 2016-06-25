package MCEntityAnimator.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import MCEntityAnimator.Util;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class GuiEntityRenderer extends GuiScreen
{

	private final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_parenting.png");

	protected String entityName;
	public EntityLivingBase entityToRender;
	protected ModelObj entityModel;
	protected List<String> parts = new ArrayList<String>();
	protected String currentPartName, additionalHighlightPartName;

	protected boolean boolBase;
	
	private int prevMouseMoveX = 0, prevMouseMoveY = 0;

	private int posX, posY;
	protected float verticalRotation = 0, horizontalRotation = 0;

	protected int scaleModifier = 0, horizontalPan = 0,verticalPan = 0;
	
	private Block blockToRender = new BlockStone();
	private RenderBlocks renderBlocks = new RenderBlocks();
	private final ResourceLocation blockTexture = new ResourceLocation("mod_MCEA:gui/grass.png");

	public GuiEntityRenderer(String entityName)
	{
		super();
		
		//Init variables.
		this.entityName = entityName;
		entityToRender = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(entityName);

		//Setup parts list.
		for(Part part : entityModel.parts)
		{
			parts.add(part.getName());
			part.setToOriginalValues();
		}
		
		blockToRender.setBlockBounds(0.0F, 1.0F, 0.0F, 4.0F, 0.9F, 4.0F);

		currentPartName = parts.get(0);
	}

	@Override
	public void initGui()
	{
		posX = width/2;
		posY = 5;
	}

	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(texture);		

		Util.drawCustomGui(posX, posY, width - posX - 5, height - 10, 0);		

		if(entityToRender != null)
		{
			float scale = scaleModifier + 50;
			if(boolBase)
				renderBlockIntoGui(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F, blockToRender);
			renderEntityIntoGui(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F, entityToRender); 
		}
		
		entityModel.clearHighlights();
		
		if(currentPartName != null)
		{
			Part currentPart = Util.getPartFromName(currentPartName, entityModel.parts);
			if(currentPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) currentPart);
		}
		
		if(additionalHighlightPartName != null && !additionalHighlightPartName.equals(""))
		{
			Part additionalPart = Util.getPartFromName(additionalHighlightPartName, entityModel.parts);
			if(additionalPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) additionalPart);
		}
		

		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void mouseClickMove(int x, int y, int par2, long par3Long) 
	{
		super.mouseClickMove(x, y, par2, par3Long);
		if(prevMouseMoveX == 0)
		{
			prevMouseMoveX = x;
			prevMouseMoveY = y;
		}
		else
		{
			horizontalRotation -= (x - prevMouseMoveX);
			verticalRotation += (y - prevMouseMoveY);
			prevMouseMoveX = x;
			prevMouseMoveY = y;
		}
	}

	@Override
	public void mouseMovedOrUp(int x, int y, int which)
	{
		super.mouseMovedOrUp(x, y, which);
		if(which != -1)
		{
			prevMouseMoveX = 0;
			prevMouseMoveY = 0;
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
		GL11.glRotatef(-((float)Math.atan((double)(par4 / 40.0F))) * 20.0F + verticalRotation , 1.0F, 0.0F, 0.0F);
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
		this.mc.getTextureManager().bindTexture(blockTexture);
		GL11.glDepthMask(false);
		renderBlocks.renderBlockAsItem(par5Block, 0, 1.0F);
		GL11.glDepthMask(true);
		blockToRender.setBlockBounds(0.5F + ((par2 - 50)/par2), 1.0F, 0.5F + ((par2 - 50)/par2), 3.5F - ((par2 - 50)/par2), 0.9F, 3.5F - ((par2 - 50)/par2));
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
		//Revert texture to GUI texture
		this.mc.getTextureManager().bindTexture(texture);		
	}
}
