package MCEntityAnimator.gui.sequence.timeline;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;
import MCEntityAnimator.gui.GuiCheckBox;
import MCEntityAnimator.gui.GuiInventoryChooseItem;
import MCEntityAnimator.gui.GuiSlider;
import MCEntityAnimator.gui.GuiSliderRotation;
import MCEntityAnimator.gui.GuiSliderTime;
import MCEntityAnimator.gui.sequence.GuiAnimationSequenceList;
import MCEntityAnimator.gui.stance.GuiAnimationStanceNew;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.PartObj;

public class GuiAnimationTimelineMain extends GuiAnimationTimeline
{
	
	private List<GuiSlider> sliders;

	public GuiAnimationTimelineMain(String entityName, AnimationSequence par1Sequence, AnimationStance startingStance, List<Keyframe> keyframes, float time) 
	{
		super(entityName, par1Sequence, startingStance, new ResourceLocation("mod_MCEA:gui/animation_timeline.png"), keyframes, time);
		new JFrame("Test").setVisible(true);
	}

	@Override
	public void initGui()
	{
		super.initGui();
		animationSpeedSlider = new GuiSliderRotation(23, posX + 239, posY + 116, 42);
		timeSlider = new GuiSliderTime(24, posX + 77, posY + 130, 300);
		timeSlider.updateXPos((int) (timeSlider.getInitialX() + time));
		sliders = new ArrayList<GuiSlider>();
		sliders.add(animationSpeedSlider);
		sliders.add(timeSlider);
		
		this.updateButtons();
	}

	@Override
	public void updateButtons() 
	{
		this.buttonList.clear();
		//Play pause button.
		this.buttonList.add(new GuiButton(0, posX + 290, posY + 4, 50, 20, boolPlay ? "Pause" : "Play"));

		//Loop animation
		this.buttonList.add(new GuiCheckBox(1, posX + 342, posY + 4, boolLoop));
		//Render with shield - only available for certain mobs TODO...
		this.buttonList.add(new GuiCheckBox(2, posX + 342, posY + 14, boolShield));
		if(!this.entityName.equals("Human"))
			Util.getButtonFromID(2, this.buttonList).enabled = false;

		//Part selection buttons, with up and down arrows if there is an excess. 
		if(parts.size() > 10)
		{
			this.buttonList.add(new GuiButton(3, posX + 382, posY + 2, 50, 20, "^"));
			this.buttonList.add(new GuiButton(4, posX + 382, posY + 233, 50, 20, "V"));
		}
		int max = parts.size() > 10 ? 10 : parts.size();	
		for(int i = 0; i < max; i++)
		{
			this.buttonList.add(new GuiButton(5+i, posX + 382, posY + 23 + 21*i, 50, 20, parts.get(i + listOffset)));
		}

		//Return to previous screen.
		this.buttonList.add(new GuiButton(15, posX + 195, posY + 92, 90, 20, "Back"));
		//Define the action point for the animation.
		this.buttonList.add(new GuiButton(16, posX + 290, posY + 26, 90, 20, "Set Action Point"));
		//Export current position as a stance. 
		this.buttonList.add(new GuiButton(17, posX + 290, posY + 70, 90, 20, "Export as Stance"));
		//Reset the orientation of the model.
		this.buttonList.add(new GuiButton(18, posX + 195, posY + 26, 90, 20, "Reset Orientation"));
		//Choose the prop the entity is holding - only available for certain mobs TODO...
		this.buttonList.add(new GuiButton(19, posX + 195, posY + 48, 90, 20, "Choose Prop"));
		if(!this.entityName.equals("Human"))
			Util.getButtonFromID(19, this.buttonList).enabled = false;

		//If in delete option stage, show yes/no option, else show delete sequence button.
		if(!boolDelete)
			this.buttonList.add(new GuiButton(20, posX + 195, posY + 70, 90, 20, "Delete Sequence"));
		else
		{	
			this.buttonList.add(new GuiButton(21, posX + 195, posY + 70, 44, 20, "Yes"));
			this.buttonList.add(new GuiButton(22, posX + 241, posY + 70, 44, 20, "No"));
		}

		for(GuiSlider slider : sliders)
		{
			this.buttonList.add(slider);	
		}
	}

	@Override
	public void actionPerformed(GuiButton button) 
	{
		//TODO renumber all to make sense.
		//Used for sliders
		lastButton = button.id;
		switch(button.id)
		{
		//Play, pause button.
		case 0: 
			this.boolPlay = !this.boolPlay; 
			if(time >= animation.getTotalTime())
			{
				time = 0.0F;
			}			
			break;
			//Loop animation check box.
		case 1: GuiCheckBox checkBox = (GuiCheckBox) button; checkBox.isChecked = !checkBox.isChecked; boolLoop = checkBox.isChecked; break; 
		//Shield check box.
		case 2: GuiCheckBox checkBox2 = (GuiCheckBox) button; checkBox2.isChecked = !checkBox2.isChecked; boolShield = checkBox2.isChecked; break;  

		//Part selection list up.
		case 3:
			if(listOffset > 0)
				listOffset--;
			updateButtons();
			break;
			//Part selection list down.
		case 4:
			if(listOffset < parts.size() - 10)
				listOffset++;
			updateButtons();
			break;
			//Back button.
		case 15: mc.displayGuiScreen(new GuiAnimationSequenceList(entityName, AnimationData.getSequences(entityName))); break;	
		//Set action point.
		case 16: animation.setActionPoint(time); break;
		//Export as stance.
		case 17: createStanceFromCurrentPosition(); break;
		//Reset orientation of model.
		case 18: horizontalPan = 0; horizontalRotation = 0; verticalPan = 0; verticalRotation = 0; break;
		//Choose prop.
		case 19: mc.displayGuiScreen(new GuiInventoryChooseItem(this, (EntityObj) this.entityToRender)); break;
		//Delete mode selected.
		case 20: boolDelete = true; updateButtons(); break;
		//Delete sequence.
		case 21: AnimationData.deleteSequence(entityName, this.animation); mc.displayGuiScreen(new GuiAnimationSequenceList(entityName, AnimationData.getSequences(entityName))); break;
		//Exit delete mode.
		case 22: boolDelete = false; break;
		}
		//Part button pressed
		if(button.id >= 5 && button.id <= 14)
		{
			updateAnimation();
			mc.displayGuiScreen(new GuiAnimationTimelinePartSelected(entityName, animation, button.displayString, keyframes, time));
		}
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
				mc.displayGuiScreen(new GuiAnimationTimelinePartSelected(entityName, animation, keyframe.partName, keyframes, time, keyframe));
			}
		}
	}
	
	@Override
	protected void mouseClickMove(int x, int y, int par2, long par3long) 
	{
		super.mouseClickMove(x, y, par2, par3long);
		for(GuiSlider slider : sliders)
		{
			if(slider.id == lastButton)
			{
				slider.updateXPos(x);
			}
		}
	}

	/**
	 * Create a stance from the current position, and then open the stance gui.
	 */
	private void createStanceFromCurrentPosition()
	{
		AnimationStance stance = new AnimationStance();
		for(String partName : this.parts)
		{
			if(partName.equals("entitypos"))
				stance.setRotation(partName, new float[]{0, (float) entityToRender.posY, 0});
			else
			{
				PartObj part = Util.getPartFromName(partName, entityModel.parts);
				stance.setRotation(partName, new float[]{part.getRotation(0), part.getRotation(1), part.getRotation(2)});
			}
		}
		mc.displayGuiScreen(new GuiAnimationStanceNew(entityName, stance)); 
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1, par2, par3);
		fontRendererObj.drawString("Speed: " + df.format(animationSpeedSlider.getValue() + 1.0F), posX + 290, posY + 114, 0);
	}
}
