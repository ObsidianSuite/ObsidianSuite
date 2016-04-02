//package MCEntityAnimator.gui.sequence.timeline;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.util.ResourceLocation;
//import MCEntityAnimator.Util;
//import MCEntityAnimator.animation.AnimationSequence;
//import MCEntityAnimator.gui.GuiCheckBox;
//import MCEntityAnimator.gui.GuiSlider;
//import MCEntityAnimator.gui.GuiSliderRotation;
//import MCEntityAnimator.gui.GuiSliderTime;
//import MCEntityAnimator.gui.sequence.timeline.GuiAnimationTimeline.Keyframe;
//import MCEntityAnimator.render.objRendering.parts.PartObj;
//
//public class GuiAnimationTimelinePartSelected extends GuiAnimationTimeline
//{
//
//	//A list of all the sliders in the gui.
//	private List<GuiSlider> sliders;
//	//X,Y and Z sliders.
//	private GuiSlider xSlider, ySlider, zSlider;
//	protected String partName;
//	protected Keyframe selectedKeyframe;
//	//Used for moving keyframes
//	private boolean draggingKeyframe;
//
//	public GuiAnimationTimelinePartSelected(String entityName, AnimationSequence animation, String partName, List<Keyframe> keyframes, float time) 
//	{
//		this(entityName, animation, partName, keyframes, time, null);
//	}
//
//	public GuiAnimationTimelinePartSelected(String entityName, AnimationSequence animation, String partName, List<Keyframe> keyframes, float time, Keyframe selectedKeyframe) 
//	{
//		super(entityName, animation, null, new ResourceLocation("mod_MCEA:gui/animation_timeline_sliders.png"), keyframes, time);
//		this.partName = partName;
//		this.selectedKeyframe = selectedKeyframe;
//		this.draggingKeyframe = false;
//	}
//
//	@Override
//	public void initGui()
//	{
//		super.initGui();
//
//		timeSlider = new GuiSliderTime(24, posX + 77, posY + 130, 300);
//		timeSlider.updateXPos((int) (timeSlider.getInitialX() + time));
//		xSlider = new GuiSliderRotation(25, posX + 255, posY + 73, 58);
//		ySlider = new GuiSliderRotation(26, posX + 255, posY + 90, 58);
//		zSlider = new GuiSliderRotation(27, posX + 255, posY + 107, 58);
//
//		sliders = new ArrayList<GuiSlider>();
//		sliders.add(timeSlider);
//		sliders.add(xSlider);
//		sliders.add(ySlider);
//		sliders.add(zSlider);
//
//		if(selectedKeyframe != null)
//		{
//			float t;
//			if(time > animation.getTotalTime())
//			{
//				t = animation.getTotalTime();
//			}
//			else
//			{
//				t = getAnimationTime();
//			}
//			this.animation.animateAll(t, 0.1F, false, entityToRender, "");
//			setSliderValuesToPartValues();
//		}
//
//		this.updateButtons();
//	}
//
//	@Override
//	public void updateButtons() 
//	{		
//		this.buttonList.clear();
//		//Play pause button.
//		this.buttonList.add(new GuiButton(0, posX + 290, posY + 4, 50, 20, boolPlay ? "Pause" : "Play"));
//
//		//Loop animation
//		this.buttonList.add(new GuiCheckBox(1, posX + 342, posY + 4, boolLoop));
//		//Render with shield - only available for certain mobs TODO...
//		this.buttonList.add(new GuiCheckBox(2, posX + 342, posY + 14, boolShield));
//		if(!this.entityName.equals("Human"))
//			Util.getButtonFromID(2, this.buttonList).enabled = false;
//
//		//Part selection buttons, with up and down arrows if there is an excess. 
//		if(parts.size() > 10)
//		{
//			this.buttonList.add(new GuiButton(3, posX + 382, posY + 2, 50, 20, "^"));
//			this.buttonList.add(new GuiButton(4, posX + 382, posY + 233, 50, 20, "V"));
//		}
//		int max = parts.size() > 10 ? 10 : parts.size();	
//		for(int i = 0; i < max; i++)
//		{
//			this.buttonList.add(new GuiButton(5+i, posX + 382, posY + 23 + 21*i, 50, 20, parts.get(i + listOffset)));
//		}
//
//		//Return to previous screen.
//		this.buttonList.add(new GuiButton(15, posX + 195, posY + 48, 90, 20, "Done"));
//		//Reset the orientation of the model.
//		this.buttonList.add(new GuiButton(16, posX + 195, posY + 26, 90, 20, "Reset Orientation"));
//
//		//Keyframe buttons
//		if(selectedKeyframe != null)
//		{
//			//Delete keyframe
//			this.buttonList.add(new GuiButton(17, posX + 195, posY + 4, 90, 20, "Delete keyframe"));
//		}
//
//		for(GuiSlider slider : sliders)
//		{
//			this.buttonList.add(slider);	
//		}
//	}
//
//	@Override
//	protected void mouseClicked(int x, int y, int i)
//	{
//		super.mouseClicked(x, y, i);
//		for(Keyframe keyframe : keyframes)
//		{
//			if(keyframe.withinBounds(x, y))
//			{
//				if(keyframe.partName.equals(partName))
//				{
//					time = keyframe.frameTime;
//					timeSlider.updateXPos((int) (timeSlider.getInitialX() + time));
//					keyframe.isCurrent = true;
//					selectedKeyframe = keyframe;
//					draggingKeyframe = true;
//					this.animation.animateAll(this.getAnimationTime(), 0.1F, false, entityToRender, "");
//					setSliderValuesToPartValues();
//					updateButtons();
//				}
//				else
//				{
//					mc.displayGuiScreen(new GuiAnimationTimelinePartSelected(entityName, animation, keyframe.partName, keyframes, time, keyframe));
//				}
//			}
//		}
//	}
//
//	@Override
//	protected void mouseClickMove(int x, int y, int par2, long par3long) 
//	{
//		super.mouseClickMove(x, y, par2, par3long);
//
//		for(Keyframe kf : keyframes)
//		{
//			if(draggingKeyframe && kf.equals(selectedKeyframe))
//			{
//				int dx = x - posX - 79;
//				if(dx < 0)
//					dx = 0;
//				if(dx > 300)
//					dx = 300;
//				kf.frameTime = dx;
//				time = kf.frameTime;
//				timeSlider.updateXPos((int) (timeSlider.getInitialX() + time));
//			}
//		}
//
//		//Check sliders
//		for(GuiSlider slider : sliders)
//		{
//			if(slider.id == lastButton)
//			{
//				slider.updateXPos(x);
//				if(slider.equals(timeSlider))
//				{
//					selectedKeyframe = null;
//				}
//				else
//				{
//					addOrSelectKeyframe();
//					setPartValuesToSliderValues();
//				}
//			}
//		}		
//		if(selectedKeyframe != null)
//		{
//			updateSelectedKeyframe();
//		}
//		updateButtons();
//	}
//
//	@Override
//	public void mouseMovedOrUp(int x, int y, int i)
//	{
//		if(i != -1)
//		{
//			draggingKeyframe = false;	
//		}
//	}
//
//	@Override
//	public void actionPerformed(GuiButton button) 
//	{
//		//Used for sliders
//		lastButton = button.id;
//		switch(button.id)
//		{
//		//Play, pause button.
//		case 0: 
//			this.boolPlay = !this.boolPlay; 
//			if(time >= animation.getTotalTime())
//			{
//				time = 0.0F;
//			}			
//			if(boolPlay)
//				selectedKeyframe = null;
//			updateButtons(); 
//			break;
//			//Loop animation check box.
//		case 1: GuiCheckBox checkBox = (GuiCheckBox) button; checkBox.isChecked = !checkBox.isChecked; boolLoop = checkBox.isChecked; break; 
//		//Shield check box.
//		case 2: GuiCheckBox checkBox2 = (GuiCheckBox) button; checkBox2.isChecked = !checkBox2.isChecked; boolShield = checkBox2.isChecked; break;  
//
//		//Part selection list up.
//		case 3:
//			if(listOffset > 0)
//				listOffset--;
//			updateButtons();
//			break;
//			//Part selection list down.
//		case 4:
//			if(listOffset < parts.size() - 10)
//				listOffset++;
//			updateButtons();
//			break;
//			//Done button
//		case 15:
//			updateAnimation();
//			mc.displayGuiScreen(new GuiAnimationTimelineMain(entityName, animation, null, keyframes, time));
//			break;	
//			//Reset orientation of model.
//		case 16: horizontalPan = 0; horizontalRotation = 0; verticalPan = 0; verticalRotation = 0; break;
//		//Delete keyframe
//		case 17: keyframes.remove(selectedKeyframe); selectedKeyframe = null; updateButtons(); updateAnimation(); break;
//		}
//
//		//Part button pressed
//		if(button.id >= 5 && button.id <= 14)
//		{
//			updateAnimation();
//			mc.displayGuiScreen(new GuiAnimationTimelinePartSelected(entityName, animation, button.displayString, keyframes, time));
//		}
//	}
//
//	@Override
//	protected void highlightParts(int x, int y)
//	{
//		super.highlightParts(x, y);
//		entityModel.hightlightPart(Util.getPartFromName(partName, entityModel.parts));
//	}
//
//	private void addOrSelectKeyframe()
//	{
//		//Check if keyframe exists, if it does select it, else add a new one with current rotation.
//		Keyframe kf = getKeyframeAtTime(getAnimationTime());
//		if(kf == null)
//		{	
//			if(!partName.equals("entitypos"))
//			{
//				PartObj part = Util.getPartFromName(partName, entityModel.parts);
//				kf = new Keyframe(time, partName, part.getRotation());
//			}
//			else
//				kf = new Keyframe(time, partName, Util.getEntityPosition(entityToRender));
//			keyframes.add(kf);
//			updateAnimation();
//		}
//		selectedKeyframe = kf;
//		updateButtons();
//	}
//
//	/**
//	 * Return a keyframe if one exists for the part at this time, null if not.
//	 */
//	private Keyframe getKeyframeAtTime(float time)
//	{
//		for(Keyframe kf : keyframes)
//		{
//			if(kf.partName.equals(partName) && kf.frameTime == time)
//			{
//				return kf;
//			}
//		}
//		return null;
//	}
//
//	private void setPartValuesToSliderValues()
//	{
//		if(partName.equals("entitypos"))
//		{
//			entityToRender.posX = xSlider.getValue();
//			entityToRender.posY = ySlider.getValue();
//			entityToRender.posZ = zSlider.getValue();
//		}
//		else
//		{
//			PartObj part = Util.getPartFromName(partName, entityModel.parts);
//			float[] rotation = new float[3];
//			rotation[0] = (float) (Math.PI*xSlider.getValue());
//			rotation[1] = (float) (Math.PI*ySlider.getValue());
//			rotation[2] = (float) (Math.PI*zSlider.getValue());
//			part.setRotation(rotation);
//		}
//	}
//
//	private void setSliderValuesToPartValues()
//	{		
//		if(partName.equals("entitypos"))
//		{
//			xSlider.updateXPosFromAngle((float) entityToRender.posX, true);
//			ySlider.updateXPosFromAngle((float) entityToRender.posY, true);
//			zSlider.updateXPosFromAngle((float) entityToRender.posZ, true);
//		}
//		else
//		{
//			PartObj part = Util.getPartFromName(partName, entityModel.parts);
//			xSlider.updateXPosFromAngle(part.getRotation(0), false);
//			ySlider.updateXPosFromAngle(part.getRotation(1), false);
//			zSlider.updateXPosFromAngle(part.getRotation(2), false);
//		}
//	}
//
//	private void updateSelectedKeyframe() 
//	{
//		if(partName.equals("entitypos"))
//		{
//			float[] position = new float[3];
//			position[0] = (float) entityToRender.posX;
//			position[1] = (float) entityToRender.posY;
//			position[2] = (float) entityToRender.posZ;
//			selectedKeyframe.values = position;
//		}
//		else
//		{
//			PartObj part = Util.getPartFromName(partName, entityModel.parts);
//			selectedKeyframe.values = part.getRotation();
//		}
//		selectedKeyframe.isCurrent = true;
//		updateAnimation();
//	}
//
//	public void drawScreen(int x, int y, float f)
//	{
//		super.drawScreen(x, y, f);
//
//		//Sliders will move as the part moves.
//		if(selectedKeyframe == null)
//			setSliderValuesToPartValues();
//
//		//make sure only one keyframe is current.
//		for(Keyframe kf : keyframes)
//		{
//			if(!kf.equals(selectedKeyframe))
//			{
//				kf.isCurrent = false;
//			}
//		}
//
//		//sliders
//		if(partName.equals("entitypos"))
//		{
//			this.fontRendererObj.drawString("X: " + df.format(entityToRender.posX), posX + 242, posY + 81, 0);
//			this.fontRendererObj.drawString("Y: " + df.format(entityToRender.posY), posX + 242, posY + 98, 0);
//			this.fontRendererObj.drawString("Z: " + df.format(entityToRender.posZ), posX + 242, posY + 115, 0);
//		}
//		else
//		{
//			PartObj part = Util.getPartFromName(partName, entityModel.parts);
//			this.fontRendererObj.drawString("X: " + df.format(part.getRotation(0)), posX + 242, posY + 81, 0);
//			this.fontRendererObj.drawString("Y: " + df.format(part.getRotation(1)), posX + 242, posY + 98, 0);
//			this.fontRendererObj.drawString("Z: " + df.format(part.getRotation(2)), posX + 242, posY + 115, 0);
//		}
//	}
//
//}
