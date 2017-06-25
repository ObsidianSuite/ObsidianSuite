package obsidianAnimator.gui.timeline;

import net.minecraft.client.Minecraft;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;
import obsidianAnimator.gui.timeline.changes.ChangeSetFPS;
import obsidianAnimator.gui.timeline.swing.TimelineFrame;
import obsidianAnimator.gui.timeline.swing.TimelineVersionController;
import obsidianAnimator.gui.timeline.swing.subsection.*;

import java.io.File;

public class TimelineController
{

	//The Minecraft Gui for the timeline. Contains the entity.
	public TimelineGui timelineGui;

	//The Swing frame for the timeline.
	public TimelineFrame timelineFrame;

	//Animation
	public File animationFile;
	public final AnimationSequence currentAnimation;

	//Controllers
	public final TimelineAnimationController animationController;
	public final TimelineRenderController renderController;
	public final TimelinePartController partController;
	public final TimelineMovementController movementController;
	public final TimelineItemController itemController;
	public final TimelineInputController inputController;
	public final TimelineVersionController versionController;
	public final TimelineKeyframeController keyframeController;

	//Key mapping
	private TimelineKeyMappings keyMappings;

	//Fields
	private float time = 0.0F;
	private Part exceptionPart = null;
	private float[] copiedValues = null;

	public TimelineController(File animationFile, AnimationSequence animation)
	{
		this.currentAnimation = animation;
		this.animationFile = animationFile;

		timelineGui = new TimelineGui(this);

		animationController = new TimelineAnimationController(this);
		renderController = new TimelineRenderController(this);
		keyframeController = new TimelineKeyframeController(this);
		partController = new TimelinePartController(this);
		movementController = new TimelineMovementController(this);
		itemController = new TimelineItemController(this);
		inputController = new TimelineInputController(this);
		versionController = new TimelineVersionController(this);

		timelineFrame = new TimelineFrame(this);
		keyframeController.loadKeyframes();

		this.keyMappings = new TimelineKeyMappings(this);

		keyframeController.initCopyLabel();
		updateAnimationParts();
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

	public void close()
	{
		timelineFrame.dispose();
		FileHandler.saveAnimationSequence(animationFile, currentAnimation);
		Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
		new HomeFrame().display();
	}

	public void onGuiDraw()
	{
		if(inputController.isPlaying())
		{
			setTime(Util.getAnimationFrameTime(inputController.getPlayStartTimeNano(), inputController.getPlayStartTimeFrame(), currentAnimation.getFPS(), animationController.getTimeMultiplier()));
			setExceptionPart(null);
			if(getTime() >= currentAnimation.getTotalTime())
			{
				if(renderController.isLooping())
				{
					setTime(0.0F);
					inputController.setPlayStartTimeNano(System.nanoTime());
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
	}

	public float[] getCopiedValues()
	{
		return copiedValues.clone();
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
