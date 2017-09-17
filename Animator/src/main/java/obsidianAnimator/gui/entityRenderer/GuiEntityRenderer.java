package obsidianAnimator.gui.entityRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartEntityPos;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.ObsidianAnimator;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.render.MathHelper;
import obsidianAnimator.render.entity.EntityObj;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class GuiEntityRenderer extends GuiBlack
{

	public String entityName;
	public EntityLivingBase entityToRender;
	public ModelObj_Animator entityModel;
	public Part selectedPart;
	public Part hoveredPart;

	public boolean boolBase;
	public boolean boolGrid;

	private int prevMouseMoveX = 0, prevMouseMoveY = 0;

	private int posX, posY;
	protected float verticalRotation = 0, horizontalRotation = 0;

	protected int scaleModifier = 0, horizontalPan = 0,verticalPan = 0;

	private BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

	private final Map<Integer,View> views = new HashMap<Integer, View>();

	public int gridMinX = -1, gridMaxX = 1, gridMinZ = -1, gridMaxZ = 1;
	
	public GuiEntityRenderer(String entityName)
	{
		super();

		//Init variables.
		this.entityName = entityName;
		this.entityModel = ModelHandler.getModel(entityName);
		entityToRender = new EntityObj(Minecraft.getMinecraft().world, entityName);

		selectedPart = entityModel.parts.get(0);
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
			renderEntityIntoGui(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F, entityToRender);
			if(boolBase)
				renderBase(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F);
			if(boolGrid)
				renderGrid(posX + (width-posX-5)/2 + horizontalPan, posY + (height - 10)/2 + scaleModifier/2 + verticalPan, scale, 0.0F, 0.0F);
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
	protected void mouseClicked(int x, int y, int i) throws IOException 
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
		if(button == 1)
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
	public void mouseReleased(int x, int y, int which)
	{
		super.mouseReleased(x, y, which);
		if(which != -1)
		{
			prevMouseMoveX = 0;
			prevMouseMoveY = 0;
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
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
	public void handleMouseInput() throws IOException
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
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(par5EntityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
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

		GL11.glTranslatef(gridMinX + 0.5F, 0, gridMinZ + 0.5F);
		for(int z = gridMinZ; z < gridMaxZ; z++)
		{
			for(int x = gridMinX; x < gridMaxX; x++)
			{
				GL11.glPushMatrix();
				drawRect(new Vec3d(0.5, 0, -0.5), new Vec3d(-0.5, 0, -0.5), new Vec3d(0.5, 0, 0.5), new Vec3d(-0.5, 0, 0.5), 0xFF0000, 0.5f, 2.0f);
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

		GL11.glTranslatef(-0.5F, 0.5F, -0.5F);

		for(int z = 0; z < 2; z++)
		{
			for(int x = 0; x < 2; x++)
			{
				for(int y = 0; y < 2; y++)
				{
					GL11.glPushMatrix();
					drawCube(new Vec3d(0,0,0), 1f, 0x0000FF, 0.5f, 2.0f);
					GL11.glPopMatrix();
					GL11.glTranslatef(1.0F, 0.0F, 0.0F);
				}
				GL11.glTranslatef(-2.0F, 0.0F, 1.0F);
			}
			GL11.glTranslatef(0.0F, 1.0F, -2.0F);
		}
		GL11.glPopMatrix();
	}

	/**
	 * Draw an axis aligned cube
	 * @param centre - centre of cube
	 * @param width - width of side
	 */
	protected void drawCube(Vec3d centre, float width, int colour, float alpha, float lineWidth) {
		float f = width/2;
		double left = centre.x + f;
		double right = centre.x - f;
		double top = centre.y + f;
		double bottom = centre.y - f;
		double front = centre.z + f;
		double back = centre.z - f;
		Vec3d ltf = new Vec3d(left, top, front);
		Vec3d ltb = new Vec3d(left, top, back);
		Vec3d lbf = new Vec3d(left, bottom, front);
		Vec3d lbb = new Vec3d(left, bottom, back);
		Vec3d rtf = new Vec3d(right, top, front);
		Vec3d rtb = new Vec3d(right, top, back);
		Vec3d rbf = new Vec3d(right, bottom, front);
		Vec3d rbb = new Vec3d(right, bottom, back);
		drawRect(ltf, rtf, lbf, rbf, colour, alpha, lineWidth);
		drawRect(ltb, rtb, ltf, rtf, colour, alpha, lineWidth);
		drawRect(ltb, rtb, lbb, rbb, colour, alpha, lineWidth);
		drawRect(lbb, rbb, lbf, rbf, colour, alpha, lineWidth);
	}

	/**
	 * Draw a rectangle
	 * @param p1 - Top left
	 * @param p2 - Top right
	 * @param p3 - Bottom left
	 * @param p4 - Bottom right
	 */
	protected void drawRect(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4, int colour, float alpha, float lineWidth)
	{
		drawLine(p1, p2, colour, alpha, lineWidth);
		drawLine(p1, p3, colour, alpha, lineWidth);
		drawLine(p2, p4, colour, alpha, lineWidth);
		drawLine(p3, p4, colour, alpha, lineWidth);
	}

	protected void drawLine(Vec3d p1, Vec3d p2, int colour, float alpha, float width)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		float[] rgb = MathHelper.intToRGB(colour);
		GL11.glColor4f(rgb[0], rgb[1], rgb[2], alpha);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3d(p1.x,p1.y,p1.z);
		GL11.glVertex3d(p2.x,p2.y,p2.z);
		GL11.glEnd();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
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
