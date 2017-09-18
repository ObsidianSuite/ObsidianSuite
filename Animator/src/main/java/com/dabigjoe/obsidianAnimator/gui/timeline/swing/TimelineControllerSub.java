package com.dabigjoe.obsidianAnimator.gui.timeline.swing;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineGui;
import com.dabigjoe.obsidianAnimator.render.entity.EntityObj;

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
	
	public TimelineGui getTimelineGui()
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
