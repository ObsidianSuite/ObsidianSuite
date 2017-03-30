package obsidianAnimator.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import obsidianAPI.render.part.PartEntityPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.ObsidianAnimator;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.render.entity.EntityObj;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class GuiEntityRenderer extends GuiBlack
{

	public String entityName;
	public EntityLivingBase entityToRender;
	public ModelObj_Animator entityModel;
	protected List<Part> parts = new ArrayList<Part>();
	public Part selectedPart;
	protected Part hoveredPart;

	public boolean boolBase;
	public boolean boolGrid;

	private int prevMouseMoveX = 0, prevMouseMoveY = 0;

	private int posX, posY;
	protected float verticalRotation = 0, horizontalRotation = 0;

	protected int scaleModifier = 0, horizontalPan = 0,verticalPan = 0;

	private RenderBlocks renderBlocks = new RenderBlocks();

	private final Map<Integer,View> views = new HashMap<Integer, View>();

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
			parts.add(part);
			part.setToOriginalValues();
		}

		selectedPart = parts.get(0);
		setupViews();
		
		ModelHandler.updateRenderer(entityName);
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
		addView("Default", -314, 26, 5);
		addView("Front", 0, 0, 7);
		addView("Left", 90, 0, 4);
		addView("Right", -90, 0, 6);
		addView("Back", 180, 0, 1);
		addView("Top", 180, 90, 8);
		addView("Bottom ", 0, -90, 2);
	}

	private void addView(String name, float horizontalRotation, float verticalRotation, int numpadKey)
	{
		views.put(numpadKey, new View(name, horizontalRotation, verticalRotation, numpadKey));
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

		if(selectedPart != null)
		{
			if(selectedPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) selectedPart, true);
		}

		if(hoveredPart != null)
		{
			if(hoveredPart instanceof PartObj)
				entityModel.hightlightPart((PartObj) hoveredPart, false);
		}
	}

	/* ---------------------------------------------------- *
	 * 						Input							*
	 * ---------------------------------------------------- */

	@Override
	protected void mouseClicked(int x, int y, int i) 
	{
		super.mouseClicked(x, y, i);
		if(i == 0 && hoveredPart != null)
			updatePart(hoveredPart);
		else if(i == 0)
			updatePart(null);
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

	protected void updatePart(Part newPartName)
	{
		selectedPart = newPartName;
	}


	/* ---------------------------------------------------- *
	 * 					  Ray Trace							*
	 * ---------------------------------------------------- */

	public void processRay()
	{
		hoveredPart = entityModel.testRay();
	}

	/* ---------------------------------------------------- *
	 * 						Render							*
	 * ---------------------------------------------------- */

	/**
	 * Renders an entity into a gui. Parameters - xpos, ypos, scale, rotx, roty, entity.
	 */
	private void renderEntityIntoGui(int xPos, int yPos, float scale, float rotX, float rotY, EntityLivingBase par5EntityLivingBase)
	{
		PartEntityPos pos = (PartEntityPos) entityModel.getPartFromName("entitypos");
		float[] values = pos.getValues();
		par5EntityLivingBase.posX += values[0];
		par5EntityLivingBase.posY += values[1];
		par5EntityLivingBase.posZ += values[2];

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

		par5EntityLivingBase.posX -= values[0];
		par5EntityLivingBase.posY -= values[1];
		par5EntityLivingBase.posZ -= values[2];
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
				Block block = ObsidianAnimator.Base;
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
					renderBlocks.renderBlockAsItem(ObsidianAnimator.Grid, 0, 1.0F);
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
		if (views.containsKey(numpadKey))
		{
			View v = views.get(numpadKey);
			horizontalRotation = v.horizontalRotation;
			verticalRotation = v.verticalRotation;
		}
		else
		{
			System.err.println("Could not change to view, numpadkey: " + numpadKey);
		}
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
