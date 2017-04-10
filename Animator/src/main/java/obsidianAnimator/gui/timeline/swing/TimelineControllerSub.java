package obsidianAnimator.gui.timeline.swing;

import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.GuiAnimationTimeline;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.render.entity.EntityObj;

public abstract class TimelineControllerSub 
{
	
	public TimelineController mainController;
	
	public TimelineControllerSub(TimelineController controller)
	{
		this.mainController = controller;
	}
		
	public void display() 
	{
		mainController.display();
	}
	
	public AnimationSequence getCurrentAnimation() 
	{
		return mainController.currentAnimation;
	}
	
	public Part getSelectedPart()
	{
		return mainController.getSelectedPart();
	}
	
	public GuiAnimationTimeline getTimelineGui()
	{
		return mainController.timelineGui;
	}
	
	public TimelineFrame getTimelineFrame()
	{
		return mainController.timelineFrame;
	}
	
	public float getTime()
	{
		return mainController.getTime();
	}
	
	public void setTime(float time)
	{
		mainController.setTime(time);
	}
	
	public EntityObj getEntityToRender()
	{
		return (EntityObj) getTimelineGui().entityToRender;
	}
	
	public void setExceptionPart(Part part) 
	{
		mainController.setExceptionPart(part);
	}
	
	
}
