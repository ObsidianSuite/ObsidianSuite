package com.nthrootsoftware.mcea.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.nthrootsoftware.mcea.MCEA_Main;
import com.nthrootsoftware.mcea.Util;
import com.nthrootsoftware.mcea.data.ModelHandler;
import com.nthrootsoftware.mcea.render.objRendering.EntityObj;
import com.nthrootsoftware.mcea.render.objRendering.ModelObj;
import com.nthrootsoftware.mcea.render.objRendering.parts.Part;
import com.nthrootsoftware.mcea.render.objRendering.parts.PartObj;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;

public class GuiEntityRenderer extends GuiBlack
{

	public String entityName;
	public EntityLivingBase entityToRender;
	public ModelObj entityModel;
	protected List<String> parts = new ArrayList<String>();
	public String currentPartName;
	protected String additionalHighlightPartName;

	public boolean boolBase;
	public boolean boolGrid;

	private int prevMouseMoveX = 0, prevMouseMoveY = 0;

	private int posX, posY;
	protected float verticalRotation = 0, horizontalRotation = 0;

	protected int scaleModifier = 0, horizontalPan = 0,verticalPan = 0;

	private RenderBlocks renderBlocks = new RenderBlocks();

	private List<View> views;

	public int gridMinX = -1, gridMaxX = 1, gridMinZ = -1, gridMaxZ = 1;
	
	public GuiEntityRenderer(String entityName)
	{
		super();

		//Init variables.
		this.entityName = entityName;
		this.entityModel = ModelHandler.getModel(entityName);
		entityToRender = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);

		//Setup parts list.
		for(Part part : entityModel.parts)
		{
			parts.add(part.getName());
			part.setToOriginalValues();
		}

		currentPartName = parts.get(0);
		setupViews();
	}

	/* ---------------------------------------------------- *
	 * 						General							*
	 * ---------------------------------------------------- */

	@Override
	public void initGui()
	{
		posX = width/2;
		posY = 5;
		loadSetup();
	}

	public void loadSetup()
	{
		//TODO GUI setup
//		String setup = AnimationData.getGUISetup(entityName);
//		if(setup != null)
//		{
//			String[] split = setup.split(",");
//			horizontalPan = Integer.parseInt(split[0]);
//			verticalPan = Integer.parseInt(split[1]);
//			horizontalRotation = Float.parseFloat(split[2]);
//			verticalRotation = Float.parseFloat(split[3]);
//			scaleModifier = Integer.parseInt(split[4]);
//		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		saveSetup();
	}

	public void saveSetup()
	{
		//TODO GUI setup
//		String data = horizontalPan + "," + verticalPan + "," + horizontalRotation + "," 
//				+ verticalRotation + "," + scaleModifier;
//		AnimationData.setGUISetup(entityName, data);
	}

	/**
	 * Create various default views. 
	 */
	private void setupViews()
	{
		views = new ArrayList<View>();
		views.add(new View("Default", -314, 26, 5));
		views.add(new View("Front", 0, 0, 7));
		views.add(new View("Left", 90, 0, 4));
		views.add(new View("Right", -90, 0, 6));
		views.add(new View("Back", 180, 0, 1));
		views.add(new View("Top", 180, 90, 8));
		views.add(new View("Bottom ", 0, -90, 2));
	}

	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1, par2, par3);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);		
		if(entityToRender != null)
		{
			float scale = scaleModifier + 50;


			if(boolBase)
				renderBase(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F);
			if(boolGrid)
				renderGrid(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F);
			renderEntityIntoGui(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F, entityToRender); 
		}

		entityModel.clearHighlights();

		if(currentPartName != null && !currentPartName.equals(""))
		{
			Part currentPart = Util.getPartFromName(currentPartName, entityModel.parts);
			if(currentPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) currentPart, true);
		}

		if(additionalHighlightPartName != null && !additionalHighlightPartName.equals(""))
		{
			Part additionalPart = Util.getPartFromName(additionalHighlightPartName, entityModel.parts);
			if(additionalPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) additionalPart, false);
		}
	}

	/* ---------------------------------------------------- *
	 * 						Input							*
	 * ---------------------------------------------------- */

	@Override
	protected void mouseClicked(int x, int y, int i) 
	{
		super.mouseClicked(x, y, i);
		if(i == 0 && additionalHighlightPartName != null && !additionalHighlightPartName.equals(""))
			updatePart(additionalHighlightPartName);
		else if(i == 0)
			updatePart("");
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long par3Long) 
	{
		super.mouseClickMove(x, y, button, par3Long);
		if(button == 2)
		{
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

	/* ---------------------------------------------------- *
	 * 		     	   Part Manipulation					*
	 * ---------------------------------------------------- */

	protected void updatePart(String newPartName)
	{
		currentPartName = newPartName;
	}


	/* ---------------------------------------------------- *
	 * 					  Ray Trace							*
	 * ---------------------------------------------------- */

	public void processRay()
	{
		PartObj raySelection = entityModel.testRay();
		if(raySelection != null)
			additionalHighlightPartName = raySelection.getName();
		else
			additionalHighlightPartName = "";
	}

	/* ---------------------------------------------------- *
	 * 						Render							*
	 * ---------------------------------------------------- */

	/**
	 * Renders an entity into a gui. Parameters - xpos, ypos, scale, rotx, roty, entity.
	 */
	private void renderEntityIntoGui(int xPos, int yPos, float scale, float rotX, float rotY, EntityLivingBase par5EntityLivingBase)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(xPos, yPos, 200.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(rotY / 40.0F))) * 20.0F + verticalRotation , 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(rotX / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);
		GL11.glTranslated(par5EntityLivingBase.posX, par5EntityLivingBase.posY, par5EntityLivingBase.posZ);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(par5EntityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(xPos, yPos, 200.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(rotY / 40.0F))) * 20.0F + verticalRotation , 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(rotX / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);
		GL11.glTranslated(par5EntityLivingBase.posX, par5EntityLivingBase.posY, par5EntityLivingBase.posZ);
		processRay();
		GL11.glPopMatrix();
	}

	private void renderBase(int xPos, int yPos, float scale, float rotX, float rotY)
	{
		GL11.glPushMatrix();

		GL11.glTranslatef(xPos, yPos, 200.0F);
		GL11.glScalef(-scale, scale, scale);

		GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);

		GL11.glRotatef(-((float)Math.atan((double)(rotY / 40.0F))) * 20.0F + verticalRotation , 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(rotX / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);


		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		GL11.glTranslatef(gridMinX + 0.5F, -0.5F, gridMinZ + 0.5F);
		for(int z = gridMinZ; z < gridMaxZ; z++)
		{
			for(int x = gridMinX; x < gridMaxX; x++)
			{
				GL11.glPushMatrix();
				Block block = MCEA_Main.Base;
				block.setBlockBounds(0.0F, 0.97F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderBlocks.renderBlockAsItem(block, 0, 1.0F);
				GL11.glPopMatrix();
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);
			}
			GL11.glTranslatef(gridMinX - gridMaxX, 0.0F, 1.0F);
		}

		GL11.glPopMatrix();
	}

	private void renderGrid(int xPos, int yPos, float scale, float rotX, float rotY)
	{
		GL11.glPushMatrix();

		GL11.glTranslatef(xPos, yPos, 200.0F);
		GL11.glScalef(-scale, scale, scale);

		GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);

		GL11.glRotatef(-((float)Math.atan((double)(rotY / 40.0F))) * 20.0F + verticalRotation , 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(((float)Math.atan((double)(rotX / 40.0F))) * 20.0F + horizontalRotation, 0.0F, -1.0F, 0.0F);


		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

		GL11.glTranslatef(-1.0F, 0.5F, -1.0F);

		for(int z = 0; z < 3; z++)
		{
			for(int x = 0; x < 3; x++)
			{
				for(int y = 0; y < 3; y++)
				{
					GL11.glPushMatrix();
					renderBlocks.renderBlockAsItem(MCEA_Main.Grid, 0, 1.0F);
					GL11.glPopMatrix();
					GL11.glTranslatef(1.0F, 0.0F, 0.0F);
				}
				GL11.glTranslatef(-3.0F, 0.0F, 1.0F);
			}
			GL11.glTranslatef(0.0F, 1.0F, -3.0F);
		}
		GL11.glPopMatrix();
	}

	/* ---------------------------------------------------- *
	 * 						Views							*
	 * ---------------------------------------------------- */

	public void changeView(int numpadKey)
	{
		boolean viewFound = false;
		for(View v : views)
		{
			if(v.numpadKey == numpadKey)
			{
				horizontalRotation = v.horizontalRotation;
				verticalRotation = v.verticalRotation;
				viewFound = true;
				break;
			}
		}
		if(!viewFound)
			System.err.println("Could not change to view, numpadkey: " + numpadKey);
	}

	private class View
	{

		private String name;
		private float horizontalRotation, verticalRotation;
		private int numpadKey;

		private View(String name, float horizontalRotation, float verticalRotation, int numpadKey)
		{
			this.name = name;
			this.horizontalRotation = horizontalRotation;
			this.verticalRotation = verticalRotation;
			this.numpadKey = numpadKey;
		}

		public int getKey()
		{
			return numpadKey;
		}

	}
}
