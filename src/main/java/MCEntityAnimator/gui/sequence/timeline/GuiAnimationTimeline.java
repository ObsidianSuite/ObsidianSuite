package MCEntityAnimator.gui.sequence.timeline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
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
import MCEntityAnimator.animation.AnimationMovement;
import MCEntityAnimator.animation.AnimationPart;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;
import MCEntityAnimator.gui.GuiSlider;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;

/**
 * A superclass for all the timeline guis.
 */
public abstract class GuiAnimationTimeline extends GuiScreen 
{
	int posX;
	int posY;

	String entityName;
	EntityLivingBase entityToRender;
	ModelObj entityModel;
	private final Block blockToRender = new BlockStone();

	private int scaleModifier = 0;
	protected int horizontalPan = 0;
	protected int verticalPan = 0;
	protected int horizontalRotation = 0;
	protected int verticalRotation = 0;

	private String popUpString = "";
	private int popUpTime = 0;
	private int popUpColour;

	protected AnimationSequence animation;

	protected boolean boolPlay;
	protected float time;

	protected boolean boolLoop = false;
	protected boolean boolGround = false;
	protected boolean boolShield = false;

	protected List<String> parts = new ArrayList<String>();
	protected List<Keyframe> keyframes = new ArrayList<Keyframe>();
	protected int listOffset = 0;
	private int listOffset2 = 0;
	protected int lastButton = 0;

	private boolean rotationLocked = false;
	private boolean rotationLockPressed = false;
	private float lockX;
	private float lockY;

	protected DecimalFormat df = new DecimalFormat("#.##");
	private RenderBlocks renderBlocks = new RenderBlocks();
	private ArrayList<Integer> lockedParts = new ArrayList<Integer>();
	private boolean filter = false;

	protected boolean boolDelete = false;

	GuiSlider animationSpeedSlider;
	GuiSlider timeSlider;

	private final ResourceLocation blockTexture = new ResourceLocation("mod_MCEA:gui/grass.png");
	private ResourceLocation texture;


	/**
	 * Load animation to be edited from the given keyframes. If keyframes is null, get keyframes from animation
	 * Starting stance should be null unless creating a new animation from a stance.
	 */
	public GuiAnimationTimeline(String entityName, AnimationSequence animation, AnimationStance startingStance, ResourceLocation texture, List<Keyframe> keyframes, float time)
	{
		//Init variables.
		this.mc = Minecraft.getMinecraft();
		this.entityName = entityName;
		this.texture = texture;
		this.animation = animation;
		this.time = time;
		entityToRender = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(entityName);
		blockToRender.setBlockBounds(0.0F, 1.0F, 0.0F, 4.0F, 0.9F, 4.0F);

		if(keyframes == null)
			this.keyframes = animation.getKeyframes(this, entityModel);
		else
			this.keyframes = keyframes;
		
		updateAnimation();

		//Setup parts list.
		parts.add("entitypos");
		for(PartObj obj : entityModel.parts)
		{
			parts.add(obj.getName());
			obj.setToOriginalRotation();
		}
		//Create keyframes for starting stance. 
		if(startingStance != null)
		{
			for(String partName : parts)
			{
				float[] rot = startingStance.getRotation(partName);
				Keyframe kf = new Keyframe(0, partName, startingStance.getRotation(partName)); 
				this.keyframes.add(kf);	
			}
		}
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
		//If screen is resized, adjusts the positions of the keyframes accordingly
		for(Keyframe kf : keyframes)
			kf.refreshBaseX(posX);
	}

	public void setup()
	{
		String setup = AnimationData.getAnimationSetup(entityName);
		if(setup != null)
		{
			String[] split = setup.split(",");
			horizontalPan = Integer.parseInt(split[0]);
			verticalPan = Integer.parseInt(split[1]);
			horizontalRotation = Integer.parseInt(split[2]);
			verticalRotation = Integer.parseInt(split[3]);
			scaleModifier = Integer.parseInt(split[4]);
			rotationLocked = Boolean.parseBoolean(split[5]);
			boolGround = Boolean.parseBoolean(split[6]);
		}
	}

	public abstract void updateButtons();

	public abstract void actionPerformed(GuiButton button);

	public void saveSetup()
	{
		String data = horizontalPan + "," + verticalPan + "," + horizontalRotation + "," 
				+ verticalRotation + "," + scaleModifier + "," + rotationLocked + "," + boolGround;
		AnimationData.setAnimationSetup(entityName, data);
	}

	@Override
	public void onGuiClosed()
	{
		saveSetup();
	}

	protected void updateAnimation()
	{
		animation.clearAnimations();
		for(Keyframe kf : keyframes)
		{   
			if(kf.frameTime != 0.0F)
			{
				if(!kf.partName.equals("entitypos"))
				{
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					Keyframe prevKf = kf.getPreviousKeyframe();
					animation.addAnimation(new AnimationPart(prevKf.frameTime, kf.frameTime, prevKf.values, kf.values, part));
				}
				else
				{
					Keyframe prevKf = kf.getPreviousKeyframe();
					animation.addMovement(new AnimationMovement(prevKf.frameTime, kf.frameTime, prevKf.values, kf.values));
				}
			}
			else
			{
				//Used for parts that only have one keyframe and where that keyframe is at the beginning 
				//The part will maintain that rotation throughout the whole animation.
				if(!kf.partName.equals("entitypos"))
				{
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					if(doesPartOnlyHaveOneKeyframe(part.getName()))
					{
						animation.addAnimation(new AnimationPart(0.0F, getLastKeyFrameTime(), kf.values, kf.values, part));
					}
				}
				else
				{
					if(doesPartOnlyHaveOneKeyframe("entitypos"))
					{
						animation.addMovement(new AnimationMovement(0, this.getLastKeyFrameTime(), kf.values, kf.values));
					}
				}
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
	protected void mouseClickMove(int x, int y, int par2, long par3long) 
	{
		//TODO is this causing the judder?
		time = (float) timeSlider.getValue();
	}

	@Override
	public void mouseMovedOrUp(int par0, int par1, int par2)
	{
		if(par2 != -1)
		{
			this.lastButton = 0;
		}
		super.mouseMovedOrUp(par0, par1, par2);
	}

	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int i)
	{
		super.mouseClicked(x, y, i);
		for(Keyframe keyframe : keyframes)
		{
			if(keyframe.withinBounds(x, y))
			{
				time = keyframe.frameTime;
				keyframe.isCurrent = true;
				if(this instanceof GuiAnimationTimelinePartSelected)
				{
					GuiAnimationTimelinePartSelected gui = (GuiAnimationTimelinePartSelected) this;
					gui.selectedKeyframe = keyframe;
				}
				else
				{
					mc.displayGuiScreen(new GuiAnimationTimelinePartSelected(entityName, animation, keyframe.partName, keyframes, time, keyframe));
				}
			}
		}
	}

	public void popUp(String string, int colour) 
	{
		this.popUpString = string;
		this.popUpColour = colour;
		this.popUpTime = 200;
	}

	public float getAnimationTime() 
	{
		if(time >= animation.getTotalTime())
		{
			if(boolLoop){time = 0.0F;}
			else{boolPlay = false;}
			this.updateButtons();
		}
		if(boolPlay)
		{
			if(animationSpeedSlider != null)
				time = (float) (time + (animationSpeedSlider.getValue() + 1.0F)); 
			else
				time = (float) (time + 1.0F); 
			time = Math.round(time * 100)/100.0F;
			timeSlider.updateXPos((int) (timeSlider.getInitialX() + time));
		}
		else
		{
			time = Math.round(time);
		}
		return time;
	}

	private boolean doesPartOnlyHaveOneKeyframe(String partName)
	{
		int count = 0;
		for(Keyframe kf : this.keyframes)
		{
			if(kf.partName.toLowerCase().equals(partName.toLowerCase()))
			{
				count++;
				if(count == 2)
				{
					return false;
				}
			}
		}
		return true;
	}

	private float getLastKeyFrameTime() 
	{
		float lastTime = 0.0F;
		for(Keyframe kf : keyframes)
		{
			if(kf.frameTime > lastTime)
			{
				lastTime = kf.frameTime;
			}
		}
		return lastTime;
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
					lockX = (float)(posX + 95) - par1;
					lockY = (float)(posY + 60) - par2;
					this.rotationLockPressed = false;
				}
				if(boolGround)
				{
					renderBlockIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, lockX, lockY, blockToRender); 
				}
				renderEntityIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, lockX, lockY, entityToRender); 
				//renderEntityFirstPersonIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, lockX, lockY, entityToRender); 
			}
			else
			{
				if(boolGround)
				{
					renderBlockIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, (float)(posX + 95) - par1, (float)(posY + 60) - par2, blockToRender);
				}
				renderEntityIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, (float)(posX + 95) - par1, (float)(posY + 60) - par2, entityToRender);
				//renderEntityFirstPersonIntoGui(posX + 100 + horizontalPan, posY + 115 + scaleModifier/2 + verticalPan, scale, (float)(posX + 95) - par1, (float)(posY + 60) - par2, entityToRender);
				//System.out.println(horizontalPan + " " + (scaleModifier/2 + verticalPan) + " " + scale + " " + par1 + " " + par2);
			}
		}

		if(this.popUpTime > 0)
		{
			this.drawCenteredString(this.fontRendererObj, popUpString, posX + 192, posY + 116, popUpColour);
			popUpTime -= 1;
		}

		//Set the parts to their positions at the current time, or if the current time is greater than the end
		//of the animation then set them to their positions at the end time.
		float t;
		if(time > animation.getTotalTime())
		{
			t = animation.getTotalTime();
		}
		else
		{
			t = getAnimationTime();
		}
		if(this instanceof GuiAnimationTimelinePartSelected && ((GuiAnimationTimelinePartSelected) this).selectedKeyframe != null)
		{
			this.animation.animateAll(t, 0.1F, false, entityToRender, ((GuiAnimationTimelinePartSelected) this).partName);
		}
		else
		{
			this.animation.animateAll(t, 0.1F, false, entityToRender, "");
		}
		
		
	
		if(animation.getActionPoint() != 0.0F)
		{
			drawVerticalLine((int) (posX + 79 + animation.getActionPoint()), posY + 128, posY + 134, 0xFFDAA520);
		}

		fontRendererObj.drawString("Loop", posX + 352, posY + 5, 0);
		fontRendererObj.drawString("Shield", posX + 352, posY + 15, 0);
		fontRendererObj.drawString("Time: " + time, posX + 306, posY + 50, 0);
		fontRendererObj.drawString("W/A/S/D-Rotate    Arrow Keys-Move", posX + 4, posY + 4, 0);
		fontRendererObj.drawString("Scroll-Zoom B-Base T-Txtr L-Lock", posX + 4, posY + 14, 0);

		if(parts.size() > 12)
		{
			fontRendererObj.drawString("Filter", posX - 30, posY + 158, 0xFFFFFFFF);
		}

		int max = parts.size() > 12 ? 12 : parts.size();
		ArrayList<String> visibleParts = new ArrayList<String>();
		for(int i = 0; i < max; i++)
		{
			String str = parts.get(i + listOffset2);
			if(filter)
			{
				if(lockedParts.contains(i + listOffset2))
				{
					visibleParts.add(str);
				}
			}
			else
			{
				visibleParts.add(str);
			}

		}

		
		//Draw keyframes and the part names next to the timeline section.
		String selectedPart = "";
		if(this instanceof GuiAnimationTimelinePartSelected)
		{
			GuiAnimationTimelinePartSelected gui = (GuiAnimationTimelinePartSelected) this;
			selectedPart = gui.partName;
		}
		for(int i = 0; i < visibleParts.size(); i++)
		{
			String str = visibleParts.get(i);			
			int color = 0;
			if(!selectedPart.equals("") && str.equals(selectedPart))
				color = 0xFFFF0000;
			
			this.fontRendererObj.drawString(str, posX + 41 - this.fontRendererObj.getStringWidth(str)/2, posY + 132 + i*10, color);
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(str))
				{
					kf.updatePosY(posY + 132 + 10*i);
					kf.draw();
				}
			}
		}

		highlightParts(par1, par2);
		super.drawScreen(par1, par2, par3);
	}

	protected void highlightParts(int x, int y)
	{
		//Highlight a part if its equivalent button or one of its keyframe is hovered over.
		entityModel.clearHighlights();	
		for(int k = 0; k < 10; k++)
		{
			GuiButton b = Util.getButtonFromID(k + 6, this.buttonList);
			if(b != null && (x > b.xPosition && x < b.xPosition + b.width && y > b.yPosition && y < b.yPosition + b.height))
			{
				entityModel.hightlightPart(Util.getPartFromName(b.displayString, entityModel.parts));		
				break;
			}
		}
		for(Keyframe kf : keyframes)
		{
			if(kf.withinBounds(x, y))
			{
				entityModel.hightlightPart(Util.getPartFromName(kf.partName, entityModel.parts));
				break;
			}
		}
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

	public boolean shouldRenderShield()
	{
		return this.boolShield;
	}

	public class Keyframe 
	{
		String partName;
		float frameTime;
		int yPos;
		//Rotation for parts and position for entityPosition
		float[] values;
		//Is current keyframe, or is a selected keyframe (multiple selected).
		boolean isCurrent;
		boolean isSelected;
		//The x coordinate of the keyframe when it is at time zero.
		int baseX;

		public Keyframe(float frameTime, String partName, float[] values)
		{
			this.frameTime = frameTime;		
			this.partName = partName;
			this.values = values;
			//Setup y position (ie which line it is on).
			for(int i = 0; i < parts.size(); i++)
			{
				if(parts.get(i).equals(partName))
				{
					yPos = posY + 132 + 10*i;
				}
			}
			baseX = posX + 79;
		}

		/**
		 * Return true if there is a keyframe for the same part that comes after this one.
		 */
		public boolean hasNextKeyframe() 
		{
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(partName) && kf.frameTime > frameTime)
				{
					return true;
				}
			}
			return false;
		}
		
		public void refreshBaseX(int x)
		{
			baseX = x + 79;
		}

		public void updatePosY(int yPos)
		{
			this.yPos = yPos;
		}

		/**
		 * Gets the keyframe that comes before this one, for the same part, or a default keyframe at time zero if none exists. 
		 */
		private Keyframe getPreviousKeyframe()
		{
			Keyframe previousKf = null;
			Float prevFt = null;
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(partName) && kf.frameTime < frameTime && (prevFt == null || kf.frameTime > prevFt))
				{
					previousKf = kf;
					prevFt = kf.frameTime;
				}
			}
			if(previousKf == null)
			{
				if(partName.equals("entitypos"))
				{
					previousKf = new Keyframe(0.0F, partName, new float[]{0.0F, 0.0F, 0.0F});
				}
				else
				{
					PartObj part = Util.getPartFromName(this.partName, entityModel.parts);
					float[] defaults = part.getOriginalRotation();
					previousKf = new Keyframe(0.0F, part.getName(), new float[]{0.0F, 0.0F, 0.0F});
				}
			}
			return previousKf;
		}

		/**
		 * Returns true if the (x,y) coordinate is within the area marked out by the keyframe.
		 */
		boolean withinBounds(int x, int y)
		{
			int frameX = (int) (baseX + frameTime);
			if(x == frameX && y >= yPos && y <= yPos + 8)
			{
				return true;
			}					
			return false;
		}

		/**
		 * Draws the keyframe. //TODO selected checks...
		 */
		private void draw()
		{
			int colour;
			if(isCurrent){colour = 0xff00ff00;}
			else if(isSelected){colour = 0xff0000ff;}
			else {colour = 0xffff0000;}				
			drawVerticalLine((int) (baseX + frameTime), yPos, yPos + 8, colour);
		}

	}

}


