package MCEntityAnimator.gui.sequence;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
import MCEntityAnimator.gui.GuiCheckBox;
import MCEntityAnimator.gui.GuiInventoryChooseItem;
import MCEntityAnimator.gui.GuiSlider;
import MCEntityAnimator.gui.stance.GuiAnimationStanceNew;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;


public class GuiAnimationTimeline extends GuiScreen 
{
	int posX;
	int posY;

	String entityName;
	EntityLivingBase entityToRender;
	ModelObj entityModel;
	Block blockToRender;

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_timeline.png");
	private static final ResourceLocation texture2 = new ResourceLocation("mod_MCEA:gui/animation_timeline_sliders.png");
	private static final ResourceLocation blockTexture = new ResourceLocation("mod_MCEA:gui/grass.png");

	private int scaleModifier = 0;
	private int horizontalPan = 0;
	private int verticalPan = 0;
	private int horizontalRotation = 0;
	private int verticalRotation = 0;

	private String popUpString = "";
	private int popUpTime = 0;
	private int popUpColour;

	private AnimationSequence animation;

	private int xReference = 0;

	private boolean boolPlay;
	private float time;

	private Slider slider;
	private boolean sliderSelected = false;
	private boolean boolLoop = false;
	private boolean boolGround = false;
	private boolean boolShield = false;

	private boolean showPositionSliders = false;

	private ArrayList<String> parts = new ArrayList<String>();
	private ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();
	private Keyframe currentKeyframe;
	private boolean keyframeSelected = false;
	private PartObj currentPart;
	private int listOffset = 0;
	private int listOffset2 = 0;
	private int lastButton = 0;

	double xPosSlider = 0.0D;
	double yPosSlider = 0.0D;
	double zPosSlider = 0.0D;
	double animationSpeedSlider = 0.0D;

	private boolean copyingFrame = false;
	private boolean copyingFrames = false;
	private boolean multiSelected = false;
	private ArrayList<Keyframe> selectedFrames = new ArrayList<Keyframe>();
	private boolean rotationLocked = false;
	private boolean rotationLockPressed = false;
	private float lockX;
	private float lockY;

	private DecimalFormat df = new DecimalFormat("#.##");
	private RenderBlocks renderBlocks = new RenderBlocks();
	private ArrayList<Integer> lockedParts = new ArrayList<Integer>();
	private boolean filter = false;

	private boolean boolDelete = false;

	private float sliderLength = 29.0F;

	public GuiAnimationTimeline(String par0Str, AnimationSequence par1Sequence) 
	{
		this(par0Str, par1Sequence, new EntityObj(Minecraft.getMinecraft().theWorld, par0Str), null);
	}

	public GuiAnimationTimeline(String par0Str, AnimationSequence par1Sequence, AnimationStance par3Stance) 
	{
		this(par0Str, par1Sequence, new EntityObj(Minecraft.getMinecraft().theWorld, par0Str), par3Stance);
	}

	public GuiAnimationTimeline(String par0Str, AnimationSequence par1Sequence, EntityLivingBase par2EntityLivingBase, AnimationStance par3Stance)
	{
		this.mc = Minecraft.getMinecraft();
		entityName = par0Str;
		entityToRender = par2EntityLivingBase;
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(par0Str);
		blockToRender = new BlockStone();
		blockToRender.setBlockBounds(0.0F, 1.0F, 0.0F, 4.0F, 0.9F, 4.0F);
		animation = par1Sequence;
		parts.add("entitypos");
		for(PartObj obj : entityModel.parts)
		{
			parts.add(obj.getName());
			obj.setToOriginalRotation();
		}
		if(par3Stance != null)
		{
			for(String partName : parts)
			{
				float[] rot = par3Stance.getRotation(partName);
				if(!(rot[0] == 0 && rot[1] == 0 && rot[2] == 0))
				{
					Keyframe kf = new Keyframe(0, partName); 
					if(partName.equals("entitypos"))
					{
						kf.position = par3Stance.getRotation(partName);
					}
					else
					{
						kf.rotation = par3Stance.getRotation(partName);
					}
					this.keyframes.add(kf);	
				}
			}
		}
		this.loadAnimations();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		this.posX = (this.width - 384)/2;
		this.posY = (this.height - 255)/2;
		slider = new Slider(79, 130);
		setup();
		this.updateButtons();
	}

	public void setup()
	{
		String setup = AnimationData.getAnimationSetup(entityName);
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
		AnimationData.setAnimationSetup(entityName, horizontalPan + "," + verticalPan + "," + scaleModifier);
	}

	@Override
	public void onGuiClosed()
	{
		saveSetup();
	}

	public void loadAnimations()
	{
		for(AnimationPart animpart : animation.getAnimations())
		{
			PartObj mr = Util.getPartFromName(animpart.getPartName(), entityModel.parts);	
			float[] defaults = animpart.getPart().getOriginalRotation();
			if(animpart.getStartTime() == 0.0F && (animpart.xBase != defaults[0] || animpart.yBase != defaults[1] || animpart.zBase != defaults[2]))
			{
				Keyframe kf = new Keyframe(0.0F, animpart.getPartName());
				kf.setPartRotation(animpart.xBase, animpart.yBase, animpart.zBase);
				keyframes.add(kf);
			}
			Keyframe kf = new Keyframe(animpart.getFinishTime(), animpart.getPartName());
			kf.setPartRotation(animpart.getFinalRotation()[0], animpart.getFinalRotation()[1], animpart.getFinalRotation()[2]);
			keyframes.add(kf);
		}
		for(AnimationMovement animmove : animation.getMovements())
		{
			if(animmove.getStartTime() == 0.0F && (animmove.getStartPosition()[0] != 0.0F || animmove.getStartPosition()[1] != 0.0F || animmove.getStartPosition()[2] != 0.0F))
			{
				Keyframe kf = new Keyframe(0.0F, "entitypos");
				kf.setEntityPosition(animmove.getStartPosition()[0], animmove.getStartPosition()[1], animmove.getStartPosition()[2]);
				keyframes.add(kf);
			}
			Keyframe kf = new Keyframe(animmove.getFinishTime(), "entitypos");
			kf.setEntityPosition(animmove.getEndPosition()[0], animmove.getEndPosition()[1], animmove.getEndPosition()[2]);
			keyframes.add(kf);
		}
		this.updateAnimations();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiCheckBox(1, posX + 342, posY + 4, boolLoop));
		this.buttonList.add(new GuiButton(0, posX + 290, posY + 4, 50, 20, boolPlay ? "Pause" : "Play"));
		this.buttonList.add(new GuiCheckBox(33, posX + 342, posY + 14, boolShield));
		if(!this.entityName.equals("Human"))
		{
			Util.getButtonFromID(33, this.buttonList).enabled = false;
		}
		if(!this.showPositionSliders)
		{
			//TODO change what button 2 does.
			this.buttonList.add(new GuiButton(2, posX + 195, posY + 4, 90, 20, "New Keyframe"));
			this.buttonList.add(new GuiButton(21, posX + 195, posY + 26, 90, 20, "Reset Rotation"));
			this.buttonList.add(new GuiButton(22, posX + 195, posY + 48, 90, 20, "Choose Prop"));
			if(!this.entityName.equals("Human"))
			{
				Util.getButtonFromID(22, this.buttonList).enabled = false;
			}

			if(boolDelete)
			{
				this.buttonList.add(new GuiButton(48, posX + 195, posY + 70, 44, 20, "Yes"));
				this.buttonList.add(new GuiButton(49, posX + 241, posY + 70, 44, 20, "No"));
			}
			else
			{
				this.buttonList.add(new GuiButton(23, posX + 195, posY + 70, 90, 20, "Delete Sequence"));
			}

			this.buttonList.add(new GuiButton(24, posX + 195, posY + 92, 90, 20, "Back"));
			this.buttonList.add(new GuiButton(32, posX + 290, posY + 26, 90, 20, "Set Action Point"));
			this.buttonList.add(new GuiButton(50, posX + 290, posY + 70, 90, 20, "Export as Stance"));

			this.buttonList.add(new GuiSlider(51, (int) Math.round((posX + 239 + animationSpeedSlider)), posY + 116));
		}
		else
		{
			if(!multiSelected)
			{
				this.buttonList.add(new GuiButton(25, posX + 195, posY + 4, 90, 20, "Copy Keyframe"));
				this.buttonList.add(new GuiButton(26, posX + 195, posY + 26, 90, 20, "Delete Keyframe"));
				this.buttonList.add(new GuiButton(20, posX + 195, posY + 48, 90, 20, "Done"));
				this.buttonList.add(new GuiSlider(17, (int) Math.round((posX + 255 + xPosSlider)), posY + 73));
				this.buttonList.add(new GuiSlider(18, (int) Math.round((posX + 255 + yPosSlider)), posY + 90));
				this.buttonList.add(new GuiSlider(19, (int) Math.round((posX + 255 + zPosSlider)), posY + 107));	
			}
			else
			{
				this.buttonList.add(new GuiButton(27, posX + 195, posY + 4, 90, 20, "Copy Keyframes"));
				this.buttonList.add(new GuiButton(28, posX + 195, posY + 26, 90, 20, "Delete Keyframes"));
				this.buttonList.add(new GuiButton(29, posX + 195, posY + 48, 90, 20, "Done"));			
			}
		}

		if(parts.size() > 10)
		{
			this.buttonList.add(new GuiButton(4, posX + 380, 2, 50, 20, "^"));
			this.buttonList.add(new GuiButton(5, posX + 380, 233, 50, 20, "V"));
		}
		int max = parts.size() > 10 ? 10 : parts.size();	
		for(int i = 0; i < max; i++)
		{
			this.buttonList.add(new GuiButton(6+i, posX + 382, 23 + 21*i, 50, 20, parts.get(i + listOffset)));
		}

		if(parts.size() > 12)
		{
			this.buttonList.add(new GuiButton(30, posX - 30, posY + 192, 20, 20, "V"));
			this.buttonList.add(new GuiButton(31, posX - 30, posY + 170, 20, 20, "^"));
			this.buttonList.add(new GuiCheckBox(47, posX - 40,  posY + 157, filter));
			if(!filter)
			{
				for(int i = 0; i < 12; i++)
				{
					this.buttonList.add(new GuiCheckBox(35 + i, posX + 3,  posY + 132 + i*10, lockedParts.contains(Integer.valueOf(i + listOffset2))));
				}
			}
		}
	}

	public void actionPerformed(GuiButton button)
	{	
		this.lastButton = button.id;
		switch(button.id)
		{
		case 0: 
			this.boolPlay = !this.boolPlay; 
			if(time >= animation.getTotalTime())
			{
				time = 0.0F;
			}			
			break;
		case 1: GuiCheckBox checkBox = (GuiCheckBox) button; checkBox.isChecked = !checkBox.isChecked; boolLoop = checkBox.isChecked; break; 
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
		case 20: this.currentKeyframe = null; this.currentPart = null; this.keyframeSelected = false; this.showPositionSliders = false; lastButton = 0; entityModel.clearHighlights(); break;
		case 21: this.horizontalPan = 0; this.horizontalRotation = 0; this.verticalPan = 0; this.verticalRotation = 0; break;
		case 22: this.mc.displayGuiScreen(new GuiInventoryChooseItem(this, (EntityObj) this.entityToRender)); break;
		case 23: boolDelete = true; break;
		case 24: mc.displayGuiScreen(new GuiAnimationSequenceList(entityName, AnimationData.getSequences(entityName))); break;
		case 25: this.copyingFrame = true; this.popUp("Copied key frame", 0xff00ff00); break;
		case 26: keyframes.remove(this.currentKeyframe); this.updateAnimations(); this.currentKeyframe = null; this.currentPart = null; this.keyframeSelected = false; this.showPositionSliders = false; lastButton = 0; entityModel.clearHighlights(); break;
		case 27: this.copyingFrames = true; this.popUp("Copied key frames", 0xff00ff00); break;
		case 28: for(Keyframe kf : this.selectedFrames){keyframes.remove(kf);} this.multiSelected = false; this.updateAnimations(); this.currentKeyframe = null; 
		this.currentPart = null; this.keyframeSelected = false; this.showPositionSliders = false; lastButton = 0; entityModel.clearHighlights(); break;
		case 29: this.selectedFrames.clear(); multiSelected = false; entityModel.clearHighlights(); break;
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
		case 32: animation.setActionPoint(time); break;
		case 33: GuiCheckBox checkBox2 = (GuiCheckBox) button; checkBox2.isChecked = !checkBox2.isChecked; boolShield = checkBox2.isChecked; break; 
		case 47: GuiCheckBox checkBox4 = (GuiCheckBox) button; checkBox4.isChecked = !checkBox4.isChecked; filter = checkBox4.isChecked; listOffset2 = 0; break; 
		case 48: AnimationData.deleteSequence(entityName, this.animation); mc.displayGuiScreen(new GuiAnimationSequenceList(entityName, AnimationData.getSequences(entityName))); break;
		case 49: boolDelete = false; break;
		case 50: 
			AnimationStance stance = new AnimationStance();
			for(String partName : this.parts)
			{
				if(partName.equals("entitypos"))
				{
					stance.setRotation(partName, new float[]{0, (float) entityToRender.posY, 0});
				}
				else
				{
					PartObj part = Util.getPartFromName(partName, entityModel.parts);
					stance.setRotation(partName, new float[]{part.getRotation(0), part.getRotation(1), part.getRotation(2)});
				}
			}
			mc.displayGuiScreen(new GuiAnimationStanceNew(entityName, stance)); 
			break;
		}
		if(button.id > 5 && button.id < 17)
		{
			//Check if an existing key frame exists, and if it does select it. 
			boolean flag = false;
			for(Keyframe k : keyframes)
			{
				if(k.frameTime == time && k.partName.equals(button.displayString))
				{
					flag = true; 
					this.currentPart = Util.getPartFromName(button.displayString, entityModel.parts);
					this.currentKeyframe = k; 
					this.updateButtons();
					if(button.displayString.equals("entitypos"))
					{
						float[] position = currentKeyframe.position;
						this.xPosSlider = position[0]*50.0F;
						this.yPosSlider = position[1]*50.0F;
						this.zPosSlider = position[2]*50.0F;
					}
					else
					{
						float[] rotation = currentKeyframe.rotation;
						this.xPosSlider = rotation[0] * sliderLength / Math.PI;
						this.yPosSlider = rotation[1] * sliderLength / Math.PI;
						this.zPosSlider = rotation[2] * sliderLength / Math.PI;
					}
					this.updateAnimations();
					showPositionSliders = true;
					break;
				}
			}
			//If no existing keyframe exists, create a new one. 
			if(!flag)
			{
				this.currentPart = Util.getPartFromName(button.displayString, entityModel.parts);
				Keyframe kf = new Keyframe(time, button.displayString); this.keyframes.add(kf); this.currentKeyframe = kf; this.updateButtons();
				if(button.displayString.equals("entitypos"))
				{
					float[] position;

					if(this.currentKeyframe.hasNextKeyframe())
					{
						position = new float[]{(float) entityToRender.posX, (float) entityToRender.posY, (float) entityToRender.posZ};
					}
					else
					{
						position = this.currentKeyframe.getPreviousKeyframe().position.clone();
					}

					this.xPosSlider = position[0]*50.0F;
					this.yPosSlider = position[1]*50.0F;
					this.zPosSlider = position[2]*50.0F;
					currentKeyframe.setEntityPosition(position[0], position[1], position[2]);
				}
				else
				{
					float[] rotation;

					if(this.currentKeyframe.hasNextKeyframe())
					{
						rotation = new float[]{currentPart.getRotation(0), currentPart.getRotation(1), currentPart.getRotation(2)};
					}
					else
					{
						rotation = this.currentKeyframe.getPreviousKeyframe().rotation.clone();
					}

					this.xPosSlider = rotation[0] * sliderLength / Math.PI;
					this.yPosSlider = rotation[1] * sliderLength / Math.PI;
					this.zPosSlider = rotation[2] * sliderLength / Math.PI;
					currentKeyframe.setPartRotation(rotation[0], rotation[1], rotation[2]);
				}
				this.updateAnimations();
				showPositionSliders = true;
			}
			this.updateButtons();
		}
		if(button.id > 34 && button.id < 47)
		{
			GuiCheckBox checkBox3 = (GuiCheckBox) button; checkBox3.isChecked = !checkBox3.isChecked;
			if(checkBox3.isChecked)
			{
				if(lockedParts.size() < 12)
				{
					lockedParts.add(Integer.valueOf(button.id - 35 + listOffset2));
				}
				else
				{
					checkBox3.isChecked = false;
				}
			}
			else
			{
				lockedParts.remove(Integer.valueOf(button.id - 35 + listOffset2));
			}
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
	protected void mouseClicked(int par0, int par1, int par2)
	{
		if(slider.isClicked(par0, par1))
		{
			sliderSelected = true;
			this.xReference = par0;
		}
		else
		{
			sliderSelected = false;
		}

		for(Keyframe keyframe : keyframes)
		{
			if(keyframe.hoveredOver(par0, par1))
			{
				if(this.currentKeyframe != keyframe)
				{
					if(this.isCtrlKeyDown())
					{
						this.multiSelected = true;
						if(this.selectedFrames.contains(keyframe))
						{
							this.selectedFrames.remove(keyframe);
							entityModel.removeHighlight(Util.getPartFromName(keyframe.partName, entityModel.parts));
						}
						else
						{
							this.selectedFrames.add(keyframe);
							entityModel.hightlightPart(Util.getPartFromName(keyframe.partName, entityModel.parts), false);		
						}
						if(!this.selectedFrames.contains(this.currentKeyframe)){this.selectedFrames.add(this.currentKeyframe);}
						if(this.selectedFrames.size() == 1){this.multiSelected = false;}
						this.updateButtons();
					}
					else
					{
						this.currentKeyframe = keyframe; 
						this.currentPart = Util.getPartFromName(keyframe.partName, entityModel.parts);
						this.time = keyframe.frameTime;

						if(currentKeyframe.partName.equals("entitypos"))
						{
							float[] position = this.currentKeyframe.position.clone();
							this.xPosSlider = position[0]*50.0F;
							this.yPosSlider = position[1]*50.0F;
							this.zPosSlider = position[2]*50.0F;
						}
						else
						{
							float[] rotation = this.currentKeyframe.rotation.clone();
							this.xPosSlider = rotation[0] * sliderLength / Math.PI;
							this.yPosSlider = rotation[1] * sliderLength / Math.PI;
							this.zPosSlider = rotation[2] * sliderLength / Math.PI;
						}		

						showPositionSliders = true;
						this.updateButtons();
						entityModel.hightlightPart(Util.getPartFromName(keyframe.partName, entityModel.parts), true);		
					}
					break;
				}				
			}
		}

		if(!multiSelected)
		{
			if(currentKeyframe != null && currentKeyframe.hoveredOver(par0, par1))
			{
				keyframeSelected = true;
				this.xReference = par0;
			}
			else
			{
				keyframeSelected = false;
			}
		}
		else
		{
			boolean flag = true;
			for(Keyframe kf : this.selectedFrames)
			{
				if(kf != null && kf.hoveredOver(par0, par1))
				{
					keyframeSelected = true;
					this.xReference = par0;
					flag = false;
				}
			}
			if(flag)
			{
				keyframeSelected = false;
			}
		}

		if(this.copyingFrame)
		{
			Keyframe kf = new Keyframe(par0 - (posX + 79), this.currentKeyframe.partName);
			if(currentKeyframe.partName.equals("entitypos"))
			{
				kf.position = this.currentKeyframe.position.clone();
			}
			else
			{
				kf.rotation = this.currentKeyframe.rotation.clone();
			}
			keyframes.add(kf);
			this.copyingFrame = false;
			this.updateAnimations();
			this.updateButtons();
		}

		if(this.copyingFrames)
		{
			for(Keyframe skf : this.selectedFrames)
			{		
				Keyframe kf = new Keyframe(par0 - (posX + 79) + (skf.frameTime - currentKeyframe.frameTime), skf.partName);
				if(skf.partName.equals("entitypos"))
				{
					kf.position = skf.position.clone();
				}
				else
				{
					kf.rotation = skf.rotation.clone();
				}
				keyframes.add(kf);
				this.copyingFrames = false;
			}
			this.updateAnimations();
			this.updateButtons();
		}

		super.mouseClicked(par0, par1, par2);
	}

	@Override
	protected void mouseClickMove(int par0, int par1, int par2, long par3long) 
	{
		if(sliderSelected)
		{
			if(this.time + par0 - xReference < 0.0F)
			{
				this.time = 0.0F;
			}
			else
			{
				this.time = time + par0 - xReference;
			}
			this.xReference = par0;
		}

		if(keyframeSelected)
		{
			if(multiSelected)
			{
				ArrayList<Float> differences = new ArrayList<Float>();
				for(Keyframe kf : this.selectedFrames)
				{
					if(kf != currentKeyframe)
					{
						differences.add(kf.frameTime - currentKeyframe.frameTime);
					}
				}
				if(currentKeyframe.frameTime + par0 - xReference < 0.0F)
				{
					currentKeyframe.frameTime = 0.0F;
				}
				else
				{
					currentKeyframe.frameTime = currentKeyframe.frameTime + par0 - xReference;
				}
				int i = 0;
				for(Keyframe kf : this.selectedFrames)
				{
					if(kf != currentKeyframe)
					{
						if(currentKeyframe.frameTime + differences.get(i) < 0.0F)
						{
							kf.frameTime = 0.0F;
						}
						else
						{
							kf.frameTime = currentKeyframe.frameTime + differences.get(i);
						}
						i++;
					}
				}
			}
			else
			{
				if(currentKeyframe.frameTime + par0 - xReference < 0.0F)
				{
					currentKeyframe.frameTime = 0.0F;
				}
				else
				{
					currentKeyframe.frameTime = currentKeyframe.frameTime + par0 - xReference;
				}
			}
			this.xReference = par0;
			//this.updateAnimations();
		}

		if(currentPart != null || (currentKeyframe != null && currentKeyframe.partName.equals("entitypos")))
		{
			double d = 0.0D;
			if(par0 <= posX + 255 - 58)
			{
				d = -58;
			}
			else if(par0 >= posX + 255 + 58)
			{
				d = 58;
			}
			else
			{
				d = par0 - (posX + 255);
			}
			switch(lastButton)
			{
			case 17: xPosSlider = d; break; 
			case 18: yPosSlider = d; break; 
			case 19: zPosSlider = d; break; 
			}
			if(currentPart != null)
			{
				this.currentKeyframe.setPartRotation((float) (xPosSlider/sliderLength * Math.PI), (float) (yPosSlider/sliderLength * Math.PI), (float) (zPosSlider/sliderLength * Math.PI));
				currentPart.setRotation(currentKeyframe.rotation);
			}
			else 
			{
				this.currentKeyframe.setEntityPosition((float) (xPosSlider/50.0F), (float) (yPosSlider/50.0F), (float) (zPosSlider/50.0F));
				entityToRender.posX = currentKeyframe.position[0];
				entityToRender.posY = currentKeyframe.position[1];
				entityToRender.posZ = currentKeyframe.position[2];
			}
			this.updateAnimations();
		}

		if(lastButton == 51)
		{
			double d = 0.0D;
			if(par0 <= posX + 240 - 42)
			{
				d = -42;
			}
			else if(par0 >= posX + 240 + 42)
			{
				d = 42;
			}
			else
			{
				d = par0 - (posX + 240);
			}
			animationSpeedSlider = d;
		}



		this.updateButtons();

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
		if(boolPlay){time = (float) (time + (animationSpeedSlider + 42.0F)/84.0F); time = Math.round(time * 100)/100.0F;}
		else
		{
			time = Math.round(time);
		}
		return time;
	}

	private void updateAnimations()
	{
		animation.clearAnimations();
		for(Keyframe kf : keyframes)
		{   
			if(kf.frameTime != 0.0F)
			{
				if(!kf.partName.equals("entitypos"))
				{
					System.out.println("Creating animation");
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					Keyframe prevKf = kf.getPreviousKeyframe();
					float[] baseRotations = prevKf.rotation;
					AnimationPart partAnimation = new AnimationPart(part);
					partAnimation.update(baseRotations[0], Util.calculateSwing(kf.rotation[0] - baseRotations[0], kf.frameTime - prevKf.frameTime), 
							baseRotations[1], Util.calculateSwing(kf.rotation[1] - baseRotations[1], kf.frameTime - prevKf.frameTime), 
							baseRotations[2], Util.calculateSwing(kf.rotation[2] - baseRotations[2], kf.frameTime - prevKf.frameTime));
					partAnimation.setStartTime(prevKf.frameTime);
					partAnimation.setFinishTime(kf.frameTime);
					animation.addAnimation(partAnimation);
				}
				else
				{
					Keyframe prevKf = kf.getPreviousKeyframe();
					animation.addMovement(new AnimationMovement(prevKf.frameTime, kf.frameTime, prevKf.position, kf.position));
				}
			}
			else
			{
				if(!kf.partName.equals("entitypos"))
				{
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					if(doesPartOnlyHaveOneKeyframe(part.getName()))
					{
						AnimationPart partAnimation = new AnimationPart(part);
						partAnimation.update(kf.rotation[0], 0, kf.rotation[1], 0, kf.rotation[2], 0);
						partAnimation.setStartTime(0);
						partAnimation.setFinishTime(this.getLastKeyFrameTime());
						animation.addAnimation(partAnimation);
					}
				}
				else
				{
					if(doesPartOnlyHaveOneKeyframe("entitypos"))
					{
						animation.addMovement(new AnimationMovement(0, this.getLastKeyFrameTime(), kf.position, kf.position));
					}
				}
			}
		}
	}

	private boolean doesPartOnlyHaveOneKeyframe(String partName)
	{
		int count = 0;
		for(Keyframe kf : this.keyframes)
		{
			if(kf.partName.toLowerCase().equals(partName.toLowerCase()))
			{
				count++;
				if(count >= 2)
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

		this.mc.getTextureManager().bindTexture(showPositionSliders && (currentPart != null || currentKeyframe.partName.equals("entitypos"))  ? texture2 : texture);		
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

		this.animation.animateAll(this.getAnimationTime(), 0.1F, false, entityToRender);

		this.slider.draw();

		if(animation.getActionPoint() != 0.0F)
		{
			drawVerticalLine((int) (posX + 79 + animation.getActionPoint()), posY + 128, posY + 134, 0xFFDAA520);
		}

		//TODO remove?
		//		if(currentPart != null)
		//		{
		//			float x = currentPart.offsetX;
		//			float y = currentPart.offsetY;
		//			float z = currentPart.offsetZ;
		//			System.out.println(x/z);
		//			drawVerticalLine((int) (posX + 30 + x/z), (int) (posY + 30 + y/z), (int) (posY + 40 + y/z), 0xFFDAA520);
		//		}

		fontRendererObj.drawString("Loop", posX + 352, posY + 5, 0);
		fontRendererObj.drawString("Shield", posX + 352, posY + 15, 0);
		fontRendererObj.drawString("Time: " + time, posX + 306, posY + 50, 0);
		fontRendererObj.drawString("W/A/S/D-Rotate    Arrow Keys-Move", posX + 4, posY + 4, 0);
		fontRendererObj.drawString("Scroll-Zoom B-Base T-Txtr L-Lock", posX + 4, posY + 14, 0);

		if(!showPositionSliders)
		{
			fontRendererObj.drawString("Speed: " + Math.round((animationSpeedSlider + 42.0F)/84.0F * 100)/50.0F, posX + 290, posY + 114, 0);
		}

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
		int i = 0;
		for(String str : visibleParts)
		{
			this.fontRendererObj.drawString(str, posX + 41 - this.fontRendererObj.getStringWidth(str)/2, posY + 132 + i*10, 0);
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(str))
				{
					kf.updatePosY(posY + 132 + 10*i);
					kf.draw();
				}
			}
			i++;
		}

		if(this.showPositionSliders && currentKeyframe != null)
		{
			this.fontRendererObj.drawString("Frame Time: " + this.currentKeyframe.frameTime, posX + 293, posY + 60, 0);
		}


		if(this.showPositionSliders && !this.multiSelected)
		{
			if(this.currentKeyframe.partName.equals("entitypos"))
			{
				this.fontRendererObj.drawString("X: " + df.format(xPosSlider/50.0F), posX + 242, posY + 81, 0);
				this.fontRendererObj.drawString("Y: " + df.format(yPosSlider/50.0F), posX + 242, posY + 98, 0);
				this.fontRendererObj.drawString("Z: " + df.format(zPosSlider/50.0F), posX + 242, posY + 115, 0);
			}
			else
			{
				this.fontRendererObj.drawString("X: " + df.format(xPosSlider/sliderLength * Math.PI), posX + 242, posY + 81, 0);
				this.fontRendererObj.drawString("Y: " + df.format(yPosSlider/sliderLength * Math.PI), posX + 242, posY + 98, 0);
				this.fontRendererObj.drawString("Z: " + df.format(zPosSlider/sliderLength * Math.PI), posX + 242, posY + 115, 0);
			}
		}

		boolean flag1 = true;

		for(int k = 0; k < 10; k++)
		{
			GuiButton b = Util.getButtonFromID(k + 6, this.buttonList);
			if(b != null && (par1 > b.xPosition && par1 < b.xPosition + b.width && par2 > b.yPosition && par2 < b.yPosition + b.height))
			{
				entityModel.hightlightPart(Util.getPartFromName(b.displayString, entityModel.parts), true);		
				flag1 = false;
				break;
			}

		}

		if((copyingFrame || copyingFrames) && par1 > 126 && par2 > 125 && par2 < 250)
		{
			this.fontRendererObj.drawString(Integer.toString(par1 - 127), par1 + 5, par2 + 5, 0xFFFFFFFF);
		}

		boolean flag = true;
		for(Keyframe kf : keyframes)
		{
			if(kf.hoveredOver(par1, par2))
			{
				entityModel.hightlightPart(Util.getPartFromName(kf.partName, entityModel.parts), true);		
				flag = false;
			}
		}
		if(flag && flag1)
		{
			entityModel.clearHighlights();
			entityModel.hightlightPart(currentPart, true);
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
		this.mc.getTextureManager().bindTexture(blockTexture);
		GL11.glDepthMask(false);
		renderBlocks.renderBlockAsItem(par5Block, 0, 1.0F);
		GL11.glDepthMask(true);
		blockToRender.setBlockBounds(0.5F + ((par2 - 50)/par2), 1.0F, 0.5F + ((par2 - 50)/par2), 3.5F - ((par2 - 50)/par2), 0.9F, 3.5F - ((par2 - 50)/par2));
		this.mc.getTextureManager().bindTexture(showPositionSliders && (currentPart != null || currentKeyframe.partName.equals("entitypos"))  ? texture2 : texture);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	class Slider
	{
		int x;
		int y;

		private Slider(int par1, int par2)
		{
			this.x = posX + par1;
			this.y = posY + par2;
		}

		private boolean isClicked(int par0, int par1)
		{
			return par0 > x - 4 + time && par0 < x + 4 + time && par1 > y - 4 && par1 < y + 2;
		}

		private void draw()
		{
			drawHorizontalLine((int) (x - 2 + time), (int) (x + 2 + time), y - 1, 0xff000000);
			drawHorizontalLine((int) (x - 1 + time), (int) (x + 1 + time), y, 0xff000000);
			drawHorizontalLine((int) (x + time), (int) (x + time), y + 1, 0xff000000);
			drawVerticalLine((int) (x + time), y, y + 121, 0x77000000);
		}
	}

	class Keyframe 
	{
		String partName;
		float frameTime;
		int yPos;
		float[] rotation = new float[3];
		float[] position = new float[3];

		private Keyframe(float par0, String par1Str)
		{
			this.frameTime = par0;		
			this.partName = par1Str;
			for(int i = 0; i < parts.size(); i++)
			{
				if(parts.get(i).equals(par1Str))
				{
					yPos = posY + 132 + 10*i;
				}
			}
		}

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

		private void updatePosY(int y)
		{
			this.yPos = y;
		}

		private void setPartRotation(float x, float y, float z)
		{
			rotation[0] = x;
			rotation[1] = y;
			rotation[2] = z;
		}

		private void setEntityPosition(float x, float y, float z) 
		{
			position[0] = x;
			position[1] = y;
			position[2] = z;
		}

		private Keyframe getPreviousKeyframe()
		{
			Keyframe previousKf = null;
			float prevFt = 0.0F;
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(partName) && kf.frameTime < frameTime && (kf.frameTime > prevFt || (prevFt == 0.0F && kf.frameTime == 0.0F)))
				{
					previousKf = kf;
					prevFt = kf.frameTime;
				}
			}
			if(previousKf == null)
			{
				if(partName.equals("entitypos"))
				{
					previousKf = new Keyframe(0.0F, partName);
					previousKf.setEntityPosition(0.0F, 0.0F, 0.0F);
				}
				else
				{
					PartObj part = Util.getPartFromName(this.partName, entityModel.parts);
					float[] defaults = part.getOriginalRotation();
					previousKf = new Keyframe(0.0F, part.getName());
					previousKf.setPartRotation(defaults[0], defaults[1], defaults[2]);
				}
			}
			return previousKf;
		}

		private boolean hoveredOver(int par0, int par1)
		{
			int x = (int) (posX + 79 + frameTime);
			if(par0 == x && par1 >= yPos && par1 <= yPos + 8)
			{
				return true;
			}					
			return false;
		}

		private void draw()
		{
			int colour;
			if(currentKeyframe == this){colour = 0xff00ff00;}
			else if(selectedFrames.contains(this)){colour = 0xff0000ff;}
			else {colour = 0xffff0000;}				
			drawVerticalLine((int) (posX + 79 + frameTime), yPos, yPos + 8, colour);
		}

	}

	public boolean shouldRenderShield()
	{
		return this.boolShield;
	}

}


