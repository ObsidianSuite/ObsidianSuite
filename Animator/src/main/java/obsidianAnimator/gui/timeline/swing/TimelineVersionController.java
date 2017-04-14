package obsidianAnimator.gui.timeline.swing;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import obsidianAPI.animation.AnimationSequence;
import obsidianAnimator.gui.timeline.TimelineController;

public class TimelineVersionController extends TimelineControllerSub
{

	private int animationVersion;
	private List<AnimationSequence> animationVersions;

	public TimelineVersionController(TimelineController controller) 
	{
		super(controller);

		animationVersion = 0;
		animationVersions = new ArrayList<AnimationSequence>();
	}

	public void updateAnimation(AnimationSequence sequence)
	{
		//Remove all animations in front of current animation.
		//If undo has been called and then changes made, the state that was undone from is now out of sync, so remove it.
		//Several undo's could have been done together, so remove all in front.
		Iterator<AnimationSequence> iter = animationVersions.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			iter.next();
			if(i > animationVersion)
				iter.remove();
			i++;
		}
		//Add new version to animation versions and update animationVersion and controller.currentAnimation
		animationVersions.add(sequence);
		animationVersion = animationVersions.size() - 1;
		mainController.currentAnimation = sequence;

		mainController.animationController.onAnimationLengthChange();
	}
	
	public void undo()
	{
		if(animationVersion > 0)
		{
			animationVersion --;
			mainController.currentAnimation = animationVersions.get(animationVersion);
			mainController.keyframeController.loadKeyframes();
			mainController.setExceptionPart(null);
			mainController.refresh();
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}

	public void redo()
	{
		if(animationVersion < animationVersions.size() - 1)
		{
			animationVersion ++;
			mainController.currentAnimation = animationVersions.get(animationVersion);
			mainController.keyframeController.loadKeyframes();
			mainController.setExceptionPart(null);
			mainController.refresh();
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}
	
}
