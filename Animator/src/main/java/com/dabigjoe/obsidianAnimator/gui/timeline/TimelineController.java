package com.dabigjoe.obsidianAnimator.gui.timeline;

import java.io.File;

import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.animation.AnimationPart;
import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.file.FileHandler;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.data.ModelHandler;
import com.dabigjoe.obsidianAnimator.file.FileChooser;
import com.dabigjoe.obsidianAnimator.file.FileNotChosenException;
import com.dabigjoe.obsidianAnimator.gui.GuiBlack;
import com.dabigjoe.obsidianAnimator.gui.frames.AnimationNewFrame;
import com.dabigjoe.obsidianAnimator.gui.frames.BaseFrame;
import com.dabigjoe.obsidianAnimator.gui.frames.HomeFrame;
import com.dabigjoe.obsidianAnimator.gui.timeline.changes.ChangeSetFPS;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineFrame;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineMenuBarController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineVersionController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineAnimationController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineInputController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineItemController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineKeyframeController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineMovementController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelinePartController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection.TimelineRenderController;

import net.minecraft.client.Minecraft;

public class TimelineController
{

	//The Minecraft Gui for the timeline. Contains the entity.
	public TimelineGui timelineGui;

	//The Swing frame for the timeline.
	public TimelineFrame timelineFrame;

	//Animation
	public File animationFile;
	public final AnimationSequence currentAnimation;
	public boolean unsaved = false;

	//Controllers
	public final TimelineAnimationController animationController;
	public final TimelineRenderController renderController;
	public final TimelinePartController partController;
	public final TimelineMovementController movementController;
	public final TimelineItemController itemController;
	public final TimelineInputController inputController;
	public final TimelineVersionController versionController;
	public final TimelineKeyframeController keyframeController;
	public final TimelineMenuBarController menubarController;

	//Key mapping
	private TimelineKeyMappings keyMappings;

	//Fields
	private float time = 0.0F;
	private Part exceptionPart = null;
	private float[] copiedValues = null;

	public TimelineController(AnimationSequence animation) {
		this(null, animation);
	}

	public TimelineController(File animationFile, AnimationSequence animation)
	{
		this.currentAnimation = animation;
		this.animationFile = animationFile;

		disableSpaceAction("Button");
		disableSpaceAction("CheckBox");

		timelineGui = new TimelineGui(this);

		animationController = new TimelineAnimationController(this);
		renderController = new TimelineRenderController(this);
		keyframeController = new TimelineKeyframeController(this);
		partController = new TimelinePartController(this);
		movementController = new TimelineMovementController(this);
		itemController = new TimelineItemController(this);
		inputController = new TimelineInputController(this);
		versionController = new TimelineVersionController(this);
		menubarController = new TimelineMenuBarController(this);

		timelineFrame = new TimelineFrame(this);
		timelineFrame.setTitle(currentAnimation.getName());
		keyframeController.loadKeyframes();

		this.keyMappings = new TimelineKeyMappings(this);

		keyframeController.initCopyLabel();
		updateAnimationParts();
	}

	private void disableSpaceAction(String control)
	{
		InputMap im = (InputMap)UIManager.get(control+".focusInputMap");
		im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
		im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
	}

	/* ---------------------------------------------------- *
	 * 				   		General						    *
	 * ---------------------------------------------------- */

	public void display()
	{
		Minecraft.getMinecraft().displayGuiScreen(timelineGui);
		timelineFrame.setVisible(true);
	}

	protected void handleMinecraftKey(int key)
	{
		keyMappings.handleMinecraftKey(key);
	}

	public void refresh()
	{
		inputController.updatePlayPauseButton();
		partController.updatePartLabels();
		keyframeController.panel.refresthLineColours();
		movementController.updateEntityMovement();
		keyframeController.refreshSliderAndTextBox();
		timelineFrame.repaint();
	}


	public void updateAnimationFPS(int fps)
	{
        versionController.applyChange(new ChangeSetFPS(currentAnimation.getFPS(), fps));
    }

	public void updateAnimationParts()
	{
		//Create new animation object if new version
		AnimationSequence sequence = currentAnimation;
		sequence.clearAnimations();
		//Generate animation from controller.keyframes.
		for(Part part : keyframeController.getPartsWithKeyframes())
		{
			for(Keyframe kf : keyframeController.getPartKeyframes(part))
			{
				if(kf.frameTime != 0.0F)
				{
					Keyframe prevKf = keyframeController.getPreviousKeyframe(kf);
					sequence.addAnimation(new AnimationPart(prevKf.frameTime, kf.frameTime, prevKf.values, kf.values, part));
				}
				else if(keyframeController.doesPartOnlyHaveOneKeyframe(part))
				{
					//Used for parts that only have one keyframe and where that keyframe is at the beginning
					//The part will maintain that rotation throughout the whole animation.
					sequence.addAnimation(new AnimationPart(0, keyframeController.getLastKeyFrameTime(), kf.values, kf.values, part));
				}
			}
		}

		animationController.onAnimationLengthChange();
		//versionController.updateAnimation(sequence);
	}
	
	public void trySaveAs() {
		
		String suggestedFileName = !currentAnimation.getName().equals("New") ? currentAnimation.getName() + "." + FileHandler.animationExtension : null;
		
		//Get file location
		File file;
		try {
			file = FileChooser.getAnimationSaveLocation(this.timelineFrame, suggestedFileName);
		} catch (FileNotChosenException e) {
			return;
		}
		
		//Ensure extension is correct.
        String fileName = file.getName();
        if(fileName.contains("."))
            fileName = fileName.substring(0, fileName.indexOf("."));
        fileName += "." + FileHandler.animationExtension;
        file = new File(file.getParentFile(), fileName);
		
		//Check if overwriting existing file
		if(file.exists() && (animationFile == null || !file.getAbsolutePath().equals(animationFile.getAbsolutePath()))) {
			//Prompt overwrite - exit method if don't want to overwrite.
			if(JOptionPane.showConfirmDialog(timelineFrame, "A file already exists with this name.\n Overwrite?", "Overwrite File", JOptionPane.YES_NO_OPTION) == 1)
				return;
		}

		animationFile = new File(file.getParentFile(), fileName);
		save();
	}
	
	public boolean isSaveLocationSet() {
		return animationFile != null;
	}
	
	public void save() {
		if(!isSaveLocationSet())
			trySaveAs();
		
		String animationName = animationFile.getName().substring(0, animationFile.getName().indexOf("."));
		currentAnimation.setName(animationName);
		timelineFrame.setTitle(animationName);
		setUnsaved(false);
	    FileHandler.saveAnimationSequence(animationFile, currentAnimation);
	}

	public void openAnimationNewFrame() {
		close(new AnimationNewFrame());
	}
	
	public void openAnimationChooser() {
		if(!checkSaved())
			return;
		try
		{
			File animationFile = FileChooser.loadAnimationFile(timelineFrame);
			AnimationSequence sequence = FileHandler.getAnimationFromFile(animationFile);
			if(ModelHandler.isModelImported(sequence.getEntityName()))
			{
				close(null);
				new TimelineController(animationFile, sequence).display();
			}
			else
				JOptionPane.showMessageDialog(timelineFrame, "You must import the " + sequence.getEntityName() + " model first.");
		}
		catch(FileNotChosenException e){}
	}
	
	public void close() {
		close(new HomeFrame());
	}
	
	public void close(BaseFrame frameToShow)
	{
		if(!checkSaved()) 
			return;
		
		timelineFrame.dispose();
		Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
		if(frameToShow != null)
			frameToShow.display();			
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
		if(unsaved)
			timelineFrame.setTitle(currentAnimation.getName() + "*");
		else
			timelineFrame.setTitle(currentAnimation.getName());
	}
	
	public boolean checkSaved() {
		return !unsaved || JOptionPane.showConfirmDialog(timelineFrame, "You have unsaved changes.\n Continue?", "Unsaved Changes", JOptionPane.YES_NO_OPTION) == 0;
	}
	
	public void onGuiDraw()
	{
		if(inputController.isPlaying())
		{
			setTime(ObsidianAPIUtil.getAnimationFrameTime(inputController.getPlayStartTimeMilli(), inputController.getPlayStartTimeFrame(), currentAnimation.getFPS(), animationController.getTimeMultiplier()));
			setExceptionPart(null);
			if(getTime() >= currentAnimation.getTotalTime())
			{
				if(renderController.isLooping())
				{
					setTime(0.0F);
					inputController.setPlayStartTimeMilli(System.currentTimeMillis());
					inputController.setPlayStartTimeFrame(0);
				}
				else
				{
					inputController.setPlaying(false);
					setTime(currentAnimation.getTotalTime());
				}
			}
		}

		refresh();

		if(movementController.getEntityMovement() != null && movementController.isMovementActive())
			movementController.getEntityMovement().moveEntity(getTime(), timelineGui.entityToRender);

		currentAnimation.animateAll(getTime(), timelineGui.entityModel, getExceptionPart() != null ? getExceptionPart().getName() : "");
	}

	public Part getSelectedPart()
	{
		return timelineGui.selectedPart;
	}

	public float getTime()
	{
		return time;
	}

	public void setTime(float time)
	{
		this.time = time;
	}

	public Part getExceptionPart()
	{
		return exceptionPart;
	}

	public void setExceptionPart(Part exceptionPart)
	{
		this.exceptionPart = exceptionPart;
		this.timelineFrame.requestFocusInWindow();
	}

	public float[] getCopiedValues()
	{
		if(copiedValues != null)
			return copiedValues.clone();
		return null;
	}

	public void setCopiedValues(float[] copiedValues)
	{
		this.copiedValues = copiedValues.clone();
	}

	public void checkFramePartHighlighting()
	{
		Part frameHoveredPart = keyframeController.getHoveredPart();
		if(frameHoveredPart != null)
			timelineGui.hoveredPart = frameHoveredPart;
	}

}
